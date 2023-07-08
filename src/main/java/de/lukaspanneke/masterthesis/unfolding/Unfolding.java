package de.lukaspanneke.masterthesis.unfolding;

import de.lukaspanneke.masterthesis.CartesianProduct;
import de.lukaspanneke.masterthesis.components.*;
import de.lukaspanneke.masterthesis.logic.Formula;
import de.lukaspanneke.masterthesis.logic.QuantifiedFormula;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;

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
	 * true if the cutoff criterion should be applied.
	 * false if all event up to the depth bound should be explored.
	 */
	private final boolean CUTOFF = true;

	/**
	 * false if the net is low-level.
	 */
	private final boolean COLORED = true;

	/**
	 * Counter for the next event index.
	 */
	private int eventIndex = 0;

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
	private final PriorityQueue<PossibleExtension> possibleExtensions = new PriorityQueue<>(
			Comparator.comparing(PossibleExtension::coneConfiguration));

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

	public static Unfolding unfold(Net net, int depth) {
		Unfolding ans = new Unfolding(net, depth);
		ans.construct();
		return ans;
	}

	private Unfolding(Net original, int depthBound) {
		this.depthBound = depthBound;
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
		this.initialEvent = new Event(this.eventIndex++, new PossibleExtension(initialTransition, Set.of()));
	}

	/**
	 * Simple implementation of the ERV algorithm blueprint.
	 */
	private void construct() {
		if (PRINT_PROGRESS) {
			System.out.println("Initializing");
		}
		for (Map.Entry<Place, Variable> post : initialEvent.transition().postSet().entrySet()) {
			this.addCondition(makeCondition(initialEvent, post.getKey(), post.getValue()));
		}
		this.addEvent(initialEvent);
		if (PRINT_PROGRESS) {
			System.out.println("Initialization done");
		}

		PossibleExtension extension;
		if (PRINT_PROGRESS) {
			System.out.println("Possible Extensions: " + this.possibleExtensions);
		}
		while ((extension = this.possibleExtensions.poll()) != null) {
			if (PRINT_PROGRESS) {
				System.out.println("Next event " + extension + " with preset " + extension.preset());
			}
			Optional<Event> cutoffPredecessor = extension.prepre().stream().filter(Event::isCutoff).findAny();
			if (cutoffPredecessor.isPresent()) {
				if (PRINT_COLOR_CUTOFF_INFO) {
					System.out.println("  " + extension + " is after cut-off event " + cutoffPredecessor.get() + ". skipping.");
				}
				continue;
			}
			Event event = new Event(this.eventIndex++, extension);
			for (Map.Entry<Place, Variable> post : extension.transition().postSet().entrySet()) {
				this.addCondition(makeCondition(event, post.getKey(), post.getValue()));
			}
			this.addEvent(event);
			if (PRINT_PROGRESS) {
				System.out.println("Possible Extensions: " + this.possibleExtensions);
			}
		}
		if (PRINT_PROGRESS) {
			System.out.println("DONE. Complete finite prefix of symbolic unfolding constructed.");
		}
	}

	/**
	 * Create a condition and rename its variable
	 * such that the variable name uniquely identifies the event that first created the token.
	 *
	 * <p>If an event solely forwards the token, or adds constraints to it's color,
	 * the output variable is the same as the input variable.
	 * But if the event creates a token,
	 * that is, it must pick a new color, then a new variable is created for it.
	 */
	private Condition makeCondition(Event preEvent, Place correspondingPlace, Variable transitionToPlaceVariable) {
		Variable eventToConditionVariable = preEvent.transition().preSet().entrySet().stream()
				.filter(entry -> entry.getValue().equals(transitionToPlaceVariable))
				.findAny()
				.map(tEntry -> preEvent.preset().stream()
						.filter(condition -> condition.place().equals(tEntry.getKey()))
						.findAny().orElseThrow(() -> new AssertionError("such a condition must exist"))
						.preVariable())
				.orElseGet(() -> transitionToPlaceVariable.local(preEvent.name()));
		return new Condition(this.conditionIndex++, correspondingPlace, preEvent, eventToConditionVariable);
	}

	/**
	 * Add an event and check if it's a cut-off event.
	 */
	private void addEvent(Event event) {
		event.preset().forEach(condition -> condition.postset().add(event));
		if (event.depth() >= depthBound) {
			event.setCutoff(CutoffReason.DEPTH);
			return;
		}
		if (CUTOFF) {
			event.calcContext();
			Set<Place> mark = markingPlaces(event);
			Set<Event> eventsWithSameUncoloredMarking = this.marks.get(mark);
			if (eventsWithSameUncoloredMarking != null) {
				if (COLORED) {
					Formula<Variable> colorHistory = eventsWithSameUncoloredMarking.stream()
							.filter(otherEvent -> otherEvent.coneConfiguration().compareTo(event.coneConfiguration()) < 0)
							.map(Unfolding::markingColors)
							.collect(Formula.or());
					Formula<Variable> check = markingColors(event).implies(colorHistory);
					if (PRINT_COLOR_CUTOFF_INFO) {
						System.out.println("  Checking if " + event + " with h(cut(cone(" + event.name() + "))) = " + mark + " is cut-off event. Is cut-off, if tautology:");
					}
					if (Predicate.isTautology(check)) {
						event.setCutoff(CutoffReason.CUT_OFF_CONDITION);
					}
				} else {
					eventsWithSameUncoloredMarking.stream()
							.filter(otherEvent -> otherEvent.coneConfiguration().compareTo(event.coneConfiguration()) < 0)
							.findAny()
							.ifPresent(e -> event.setCutoff(CutoffReason.CUT_OFF_CONDITION));
				}
				eventsWithSameUncoloredMarking.add(event);
			} else {
				this.marks.put(mark, new HashSet<>(Set.of(event)));
			}
		}
	}

	/**
	 * Add a condition and find all new possible extensions enabled by it.
	 */
	private void addCondition(Condition condition) {
		if (PRINT_PROGRESS) {
			System.out.println("  Add condition " + condition);
		}
		this.concurrencyMatrix.add(condition);
		condition.preset().postset().add(condition);

		Map<Place, List<Condition>> placeToConditions = this.concurrencyMatrix.get(condition).stream()
				.collect(Collectors.groupingBy(Condition::place));
		if (placeToConditions.containsKey(condition.place())) {
			throw new AssertionError("net not safe. two conditions with the same place are concurrent.",
					new AssertionError("adding " + condition + ", there should be no " + condition.place() + " in " + placeToConditions));
		}
		placeToConditions.put(condition.place(), List.of(condition));
		for (Transition transition : condition.place().postSet()) {
			for (Condition[] candidate : new CartesianProduct<>(Condition[]::new, transition.preSet().keySet().stream().map(place -> placeToConditions.getOrDefault(place, Collections.emptyList())).toList())) {
				if (!this.concurrencyMatrix.isCoset(candidate)) {
					//System.out.println("  Conflict (structure) " + transition + " " + Arrays.toString(candidate));
					continue;
				}
				Set<Condition> preset = Set.of(candidate);
				if (COLORED) {
					if (PRINT_COLOR_CONFLICT_INFO) {
						System.out.println("    Checking color conflict for " + transition + " with co-set " + Arrays.toString(candidate) + ". No conflict if satisfiable:");
					}
					if (!Predicate.isSatisfiable(guard("⊤", transition, preset).and(history(preset)))) {
						//System.out.println("  Conflict (color) for " + transition + " " + Arrays.toString(candidate));
						continue;
					}
				}
				PossibleExtension extension = new PossibleExtension(transition, preset);
				if (PRINT_PROGRESS) {
					System.out.println("    Extend PE with (" + transition + ", " + Arrays.toString(candidate) + ")");
				}
				this.possibleExtensions.add(extension);
			}
		}
	}

	/**
	 * Guard of an event (with internal variables).
	 *
	 * <p>Cached as {@link Event#guard()}
	 */
	public static Formula<Variable> guard(String name, Transition transition, Set<Condition> preset) {
		Map<Variable, Variable> guardSubstitution = transition.guard().support().stream()
				.collect(Collectors.toMap(variable -> variable, variable -> variable.local(name)));
		return transition.preSet().entrySet().stream()
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
					Variable representative = mustEqVariables.get(0).value();
					guardSubstitution.put(variable, representative);
					return mustEqVariables;
				})
				.map(Formula::eq)
				.collect(Formula.and())
				.and(transition.guard().substitute(guardSubstitution));
	}

	/**
	 * Conjunction of all guards (with internal variables) in the event's past; not including the events own guard.
	 *
	 * <p>Almost cached as {@link Event#conePredicate()}. That copy is and'ed with the guard.
	 */
	public static Formula<Variable> history(Set<Condition> preset) {
		return preset.stream()
				.map(Condition::preset)
				.distinct()
				.map(Event::conePredicate)
				.collect(Formula.and());
	}

	/**
	 * h(cut(cone(event)))
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
	public static Formula<Variable> markingColors(Event event) {
		Formula<Variable> cutPlaceAliasing = event.coneCut().stream()
				.map(condition -> condition.internalVariable().eq(new Variable(condition.place().name())))
				.collect(Formula.and());
		Formula<Variable> conePredicate = event.conePredicate();
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
}
