package org.example;

import org.example.components.*;
import org.example.logic.Formula;
import org.example.net.Net;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Unfolding {

	private final int depthBound;
	private final boolean CUTOFF = false;

	private int eventIndex = 1;
	private int conditionIndex = 1;

	private final BottomEvent initialEvent;
	private static final Comparator<Event> ORDER = Comparator.comparing(Event::coneConfiguration, Order::compare);
	private final PriorityQueue<Event> possibleExtensions = new PriorityQueue<>(ORDER);
	private final ConcurrencyMatrix concurrencyMatrix = new ConcurrencyMatrix();
	private final Map<Set<Place>, Set<Event>> marks = new HashMap<>();

	public static Unfolding unfold(Net net, int depth) {
		Unfolding ans = new Unfolding(net, depth);
		ans.construct();
		return ans;
	}

	private Unfolding(Net original, int depthBound) {
		this.depthBound = depthBound;
		this.initialEvent = new BottomEvent(original.initialMarking());
	}

	private void construct() {
		this.possibleExtensions.add(this.initialEvent);

		Event event;
		System.out.println("Possible Extensions: " + this.possibleExtensions);
		while ((event = this.possibleExtensions.poll()) != null) {
			System.out.println("Next event " + event + " with preset " + event.preset().keySet());
			Optional<Event> cutoffPredecessor = event.coneConfiguration().events().stream().filter(Event::isCutoff).findAny();
			if (cutoffPredecessor.isPresent()) {
				System.out.println("  " + event + " is after cut-off event " + cutoffPredecessor.get() + ". skipping.");
				continue;
			}
			for (Map.Entry<Place, Variable> post : event.transition().postSet().entrySet()) {
				this.addCondition(makeCondition(event, post.getKey(), post.getValue()));
			}
			this.addEvent(event);
			System.out.println("Possible Extensions: " + this.possibleExtensions);
		}
		System.out.println("DONE. Complete finite prefix of symbolic unfolding constructed.");
	}

	private Condition makeCondition(Event preEvent, Place correspondingPlace, Variable transitionToPlaceVariable) {
		Variable eventToConditionVariable = preEvent.transition().preSet().entrySet().stream()
				.filter(entry -> entry.getValue().equals(transitionToPlaceVariable))
				.findAny()
				.map(tEntry -> preEvent.preset().entrySet().stream()
						.filter(eEntry -> eEntry.getKey().place().equals(tEntry.getKey()))
						.findAny().orElseThrow(() -> new AssertionError("such a condition must exist"))
						.getValue())
				.orElseGet(() -> transitionToPlaceVariable.local(preEvent.name()));
		return new Condition(this.conditionIndex++, correspondingPlace, preEvent, eventToConditionVariable);
	}

	private void addEvent(Event event) {
		event.preset().forEach((condition, variable) -> condition.postset().add(event));
		if (event.depth() >= depthBound) {
			event.setCutoff(CutoffReason.DEPTH);
			return;
		}
		if (CUTOFF) {
			event.calcContext(this.initialEvent.postset());
			Set<Place> mark = event.uncoloredCut();
			Set<Event> eventsWithSameUncoloredMarking = this.marks.get(mark);
			if (eventsWithSameUncoloredMarking != null) {
				Formula<Variable> colorHistory = eventsWithSameUncoloredMarking.stream()
						.filter(otherEvent -> ORDER.compare(otherEvent, event) < 0)
						.map(Event::coloredCutPredicate)
						.collect(Formula.or());
				Formula<Variable> check = event.coloredCutPredicate().implies(colorHistory);
				System.out.println("  Checking if " + event + " with h(cut(cone(" + event.name() + "))) = " + mark + " is cut-off event. Is cut-off, if tautology:");
				if (Predicate.isTautology(check)) {
					event.setCutoff(CutoffReason.CUT_OFF_CONDITION);
				}
				eventsWithSameUncoloredMarking.add(event);
			} else {
				this.marks.put(mark, new HashSet<>(Set.of(event)));
			}
		}
	}

	private void addCondition(Condition condition) {
		System.out.println("  Add condition " + condition);
		this.concurrencyMatrix.add(condition);
		condition.preset().postset().add(condition);

		Map<Place, List<Condition>> placeToConditions = this.concurrencyMatrix.co(condition).stream()
				.collect(Collectors.groupingBy(Condition::place));
		if (placeToConditions.containsKey(condition.place())) {
			throw new AssertionError("no " + condition.place() + " in " + placeToConditions);
		}
		placeToConditions.put(condition.place(), List.of(condition));
		for (Transition transition : condition.place().postSet()) {
			for (Condition[] candidate : new CartesianProduct<>(Condition[]::new, transition.preSet().keySet().stream().map(place -> placeToConditions.getOrDefault(place, Collections.emptyList())).toList())) {
				if (!this.concurrencyMatrix.isCoset(candidate)) {
					//System.out.println("  Conflict (structure) " + transition + " " + Arrays.toString(candidate));
					continue;
				}
				Map<Condition, Variable> preset = Arrays.stream(candidate)
						.collect(Collectors.toMap(Function.identity(), Condition::preVariable));
				Event event = new Event(this.eventIndex, transition, preset);
				System.out.println("    Checking color conflict for " + transition + " with co-set " + Arrays.toString(candidate) + ". No conflict if satisfiable:");
				if (!Predicate.isSatisfiable(event.conePredicate())) {
					//System.out.println("  Conflict (color) for " + transition + " " + Arrays.toString(candidate));
					continue;
				}
				this.eventIndex++;
				System.out.println("    Extend PE with " + event + " " + Arrays.toString(candidate));
				this.possibleExtensions.add(event);
			}
		}
	}

	public void render(Writer writer) throws IOException {
		new UnfoldingRenderer(this.initialEvent, true).render(writer);
	}
}
