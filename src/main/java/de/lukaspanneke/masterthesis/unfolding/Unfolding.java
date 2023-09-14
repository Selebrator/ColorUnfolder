package de.lukaspanneke.masterthesis.unfolding;

import de.lukaspanneke.masterthesis.CartesianProduct;
import de.lukaspanneke.masterthesis.logic.Formula;
import de.lukaspanneke.masterthesis.logic.QuantifiedFormula;
import de.lukaspanneke.masterthesis.logic.SatSolver;
import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.net.Place;
import de.lukaspanneke.masterthesis.net.Transition;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.lukaspanneke.masterthesis.Options.*;

public class Unfolding {

	/**
	 * The length of the longest path from an event to the point where there is no predecessor.
	 * Events past (actually at) this length will not be explored anymore.
	 */
	private final int depthBound;

	/**
	 * The transitions that we want to know if they are firable.
	 * Empty set, if we don't want to check fire-ability.
	 */
	private final Set<Transition> targetTransitions;

	/**
	 * The first event we found for a {@link #targetTransitions target transition}.
	 * Returned by {@link #foundTarget()}.
	 */
	private Event targetEvent;

	/**
	 * Counter for the next event index.
	 */
	private int eventIndex = 1;

	/**
	 * Counter for the next condition index.
	 */
	private int conditionIndex = 1;

	/**
	 * The initial event (aka. bottom event), at the root of the unfolding.
	 */
	private final Event initialEvent;

	/**
	 * Stores all unexplored events.
	 */
	private final PriorityQueue<Event> possibleExtensions = new PriorityQueue<>(
			Comparator.comparing(Event::coneConfiguration));

	/**
	 * Stores what conditions are concurrent with each other,
	 * that is, conditions that can have tokens at the same time.
	 * This is used to find co-sets.
	 */
	private final ConcurrencyMatrix concurrencyMatrix = new HashConcurrencyMatrix();

	/**
	 * Stores, for each already seen marking, which events produce that marking.
	 * Used to determine cut-offs.
	 */
	private final Map<Set<Place>, Set<Event>> marks = new HashMap<>();

	public static Unfolding unfold(Net net) {
		return unfold(net, Integer.MAX_VALUE, Set.of());
	}

	public static Unfolding unfold(Net net, int depth) {
		return unfold(net, depth, Set.of());
	}

	public static Unfolding unfold(Net net, Set<Transition> targetTransitions) {
		return unfold(net, Integer.MAX_VALUE, targetTransitions);
	}

	public static Unfolding unfold(Net net, int depth, Set<Transition> targetTransitions) {
		Unfolding ans = new Unfolding(net, depth, targetTransitions);
		ans.construct();
		return ans;
	}

	private Unfolding(Net original, int depthBound, Set<Transition> targetTransitions) {
		this.depthBound = depthBound;
		this.targetTransitions = Set.copyOf(targetTransitions);
		Marking initialMarking = original.initialMarking();
		Transition initialTransition = new Transition(
				Integer.MIN_VALUE,
				"⊥",
				Map.of(),
				initialMarking.tokens().keySet().stream()
						.collect(Collectors.toMap(Function.identity(), place -> new Variable(place.name()))),
				initialMarking.tokens().entrySet().stream()
						.map(e -> new Variable(e.getKey().name()).eq(e.getValue()))
						.collect(Formula.and())
		);
		InternalGuard internalGuard = internalGuard("e0", initialTransition, Set.of());
		Formula initialGuard = internalGuard.apply(initialTransition.guard());
		this.initialEvent = new Event(0, "e0", initialTransition, Set.of(), initialGuard, initialGuard, internalGuard.substitution);
	}

	/**
	 * Simple implementation of the ERV algorithm blueprint.
	 */
	private void construct() {
		Event event = this.initialEvent;
		do {
			if (Thread.interrupted()) {
				System.err.println("Interrupted");
				break;
			}
			if (PRINT_PROGRESS) {
				System.err.println("Next event " + event + " with preset " + event.preset());
			}
			Optional<Event> cutoffPredecessor = event.prepre().stream().filter(Event::isCutoff).findAny();
			if (cutoffPredecessor.isPresent()) {
				throw new AssertionError(event + " is after cut-off event " + cutoffPredecessor.get() + ".");
			}
			link(event);
			for (Map.Entry<Place, Variable> post : event.transition().postSet().entrySet()) {
				link(new Condition(this.conditionIndex++, post.getKey(), event, event.postVariableSubstitution().get(post.getValue())));
			}
			event.finalizePostset();
			if (this.targetTransitions.contains(event.transition())) {
				if (PRINT_PROGRESS) {
					System.err.println("Found target transition " + event + " in " + event.coneConfiguration());
				}
				this.targetEvent = event;
				break;
			}
			if (!isCutoff(event)) {
				for (Condition condition : event.postset()) {
					findPe(condition);
				}
			}
			if (PRINT_PROGRESS) {
				System.err.println("Possible Extensions: " + this.possibleExtensions);
			}
		} while ((event = this.possibleExtensions.poll()) != null);
		if (PRINT_PROGRESS && this.possibleExtensions.isEmpty()) {
			System.err.println("DONE. Complete finite prefix of symbolic unfolding constructed.");
		}
	}

	private void link(Event event) {
		event.preset().forEach(condition -> condition.postset().add(event));
	}

	private void link(Condition condition) {
		condition.preset().postset().add(condition);
	}

	/**
	 * Check if an event is a cut-off event.
	 */
	private boolean isCutoff(Event event) {
		if (event.depth() >= depthBound) {
			event.setCutoff(CutoffReason.DEPTH);
		}
		if (CUTOFF) {
			event.calcContext();
			Set<Place> mark = markingPlaces(event);
			Set<Event> eventsWithSameUncoloredMarking = this.marks.get(mark);
			if (eventsWithSameUncoloredMarking != null) {
				boolean seenBefore;
				if (COLORED) {
					Formula colorHistory = eventsWithSameUncoloredMarking.stream()
							.filter(otherEvent -> otherEvent.coneConfiguration().compareTo(event.coneConfiguration()) < 0)
							.map(Unfolding::markingColors)
							.collect(Formula.or());
					Formula check = markingColors(event).implies(colorHistory);
					if (PRINT_COLOR_CUTOFF_INFO) {
						System.err.println("  Checking if " + event + " with pi(cut(cone(" + event.name() + "))) = " + mark + " is cut-off event. Is cut-off, if tautology:");
					}
					seenBefore = SatSolver.isTautology(check);
				} else {
					seenBefore = eventsWithSameUncoloredMarking.stream()
							.anyMatch(otherEvent -> otherEvent.coneConfiguration().compareTo(event.coneConfiguration()) < 0);
				}
				if (seenBefore) {
					event.setCutoff(CutoffReason.CUT_OFF_CONDITION);
					event.dropMemoryOfCutoff();
				} else {
					eventsWithSameUncoloredMarking.add(event);
				}
			} else {
				this.marks.put(mark, new HashSet<>(Set.of(event)));
			}
		}
		return event.isCutoff();
	}

	/**
	 * Find and add all new possible extensions enabled by a new condition
	 */
	private void findPe(Condition condition) {
		if (PRINT_PROGRESS) {
			System.err.println("  Find extensions for " + condition);
		}
		this.concurrencyMatrix.add(condition);

		Map<Place, List<Condition>> placeToConditions = this.concurrencyMatrix.get(condition).stream()
				.peek(c -> {
					if (c.preset().isCutoff()) {
						throw new AssertionError("conditions in the postset of a cut-off event should not be in the concurrency matrix. but " + c + " in post(" + c.preset() + ") is.");
					}
				})
				.collect(Collectors.groupingBy(Condition::place));
		if (placeToConditions.containsKey(condition.place())) {
			throw new AssertionError("net not safe. two conditions with the same place are concurrent.",
					new AssertionError("adding " + condition + ", there should be no " + condition.place() + " in " + placeToConditions));
		}
		placeToConditions.put(condition.place(), List.of(condition));
		for (Transition transition : condition.place().postSet()) {
			for (Condition[] candidate : new CartesianProduct<>(Condition[]::new, transition.preSet().keySet().stream().map(place -> placeToConditions.getOrDefault(place, Collections.emptyList())).toList())) {
				if (!this.concurrencyMatrix.isCoset(candidate)) {
					//System.err.println("  Conflict (structure) " + transition + " " + Arrays.toString(candidate));
					continue;
				}
				Set<Condition> preset = Set.of(candidate);
				Formula guard;
				Formula conePredicate;
				String eventName = "e" + this.eventIndex;
				InternalGuard internalGuard = internalGuard(eventName, transition, preset);
				if (COLORED) {
					guard = internalGuard.apply(transition.guard());
					conePredicate = guard.and(history(preset));
					if (PRINT_COLOR_CONFLICT_INFO) {
						System.err.println("    Checking color conflict for " + transition + " with co-set " + Arrays.toString(candidate) + ". No conflict if satisfiable:");
					}
					if (!SatSolver.isSatisfiable(conePredicate)) {
						//System.err.println("  Conflict (color) for " + transition + " " + Arrays.toString(candidate));
						continue;
					}
				} else {
					guard = null;
					conePredicate = null;
				}
				Event extension = new Event(this.eventIndex++, eventName, transition, preset, guard, conePredicate, internalGuard.substitution);
				if (PRINT_PROGRESS) {
					System.err.println("    Extend PE with " + extension + " with preset " + extension.preset());
				}
				this.possibleExtensions.add(extension);
			}
		}
	}

	private record InternalGuard(Map<Variable, Variable> substitution, Formula modification) {
		public Formula apply(Formula guard) {
			return guard.substitute(substitution).and(modification);
		}
	}

	/**
	 * Find internal variable to use for the guard,
	 * such that the variable name uniquely identifies the event that first created the token.
	 *
	 * <p>If an event solely forwards the token, or adds constraints to it's color,
	 * the output variable is the same as the input variable.
	 * But if the event creates a token,
	 * that is, it must pick a new color, then a new variable is created for it.
	 */
	public static InternalGuard internalGuard(String name, Transition transition, Set<Condition> preset) {
		Map<Variable, Variable> guardSubstitution = new HashMap<>();
		Formula eq = transition.preSet().entrySet().stream()
				.collect(Collectors.groupingBy(
						Map.Entry::getValue,
						Collectors.mapping(Map.Entry::getKey, Collectors.toList())
				))
				.entrySet().stream()
				.map(originalVariableToPresetPlaces -> {
					Variable variable = originalVariableToPresetPlaces.getKey();
					List<Place> transitionPreset = originalVariableToPresetPlaces.getValue();
					List<Variable> mustEqVariables = preset.stream()
							.filter(condition -> transitionPreset.contains(condition.place()))
							.map(Condition::internalVariable)
							.distinct()
							.collect(Collectors.toList());
					Variable representative = mustEqVariables.get(0);
					guardSubstitution.put(variable, representative);
					return mustEqVariables;
				})
				.map(Formula::eq)
				.collect(Formula.and());

		List<Variable> fresh = new ArrayList<>();
		transition.postSet().values().stream()
				.distinct()
				.forEach(variable -> guardSubstitution.computeIfAbsent(variable, v -> {
					Variable ans = v.local(name);
					fresh.add(ans);
					return ans;
				}));
		Formula constraints = fresh.stream()
				.map(Variable::domainConstraint)
				.collect(Formula.and());
		return new InternalGuard(Map.copyOf(guardSubstitution), eq.and(constraints));
	}

	/**
	 * Conjunction of all guards (with internal variables) in the event's past; not including the events own guard.
	 *
	 * <p>Almost cached as {@link Event#conePredicate()}. That copy is and'ed with the guard.
	 */
	public static Formula history(Set<Condition> preset) {
		return preset.stream()
				.map(Condition::preset)
				.distinct()
				.map(Event::conePredicate)
				.collect(Formula.and());
	}

	/**
	 * pi(cut(cone(event)))
	 */
	public static Set<Place> markingPlaces(Event event) {
		return event.coneCut().stream()
				.map(Condition::place)
				.collect(Collectors.toSet());
	}

	/**
	 * The events history (with internal variables), including its own guard, all variables quantified with existence.
	 * Conjunction with a free variable for every place, setting it equal to the variable leading into that place.
	 *
	 * <p>Example: ∃ p1_⊥, x_e1: p1_⊥ = 1 ∧ p1_⊥ < x_e1 ∧ x_e1 = p2
	 */
	public static Formula markingColors(Event event) {
		Formula cutPlaceAliasing = event.coneCut().stream()
				.map(condition -> condition.internalVariable().eq(new Variable(condition.place().name())))
				.collect(Formula.and());
		Formula conePredicate = event.conePredicate();
		Set<Variable> quantifiedVariables = conePredicate.support();
		quantifiedVariables.addAll(event.coneCut().stream()
				.map(Condition::internalVariable)
				.collect(Collectors.toSet()));
		return QuantifiedFormula.of(QuantifiedFormula.Quantifier.EXISTS, quantifiedVariables, conePredicate.and(cutPlaceAliasing));
	}

	public void render(Writer writer) throws IOException {
		new UnfoldingRenderer(this.initialEvent).render(writer);
	}

	public Event getInitialEvent() {
		return initialEvent;
	}

	/**
	 * Excludes initial event.
	 */
	public int getNumberEvents() {
		return this.eventIndex - 1;
	}

	public int getNumberConditions() {
		return this.conditionIndex - 1;
	}

	public Optional<Event> foundTarget() {
		return Optional.ofNullable(this.targetEvent);
	}
}
