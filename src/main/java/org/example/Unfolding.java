package org.example;

import com.google.common.collect.Sets;
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
	private final boolean CUTOFF = true;
	private final boolean SHOW_DEBUG;

	private int eventIndex = 1;
	private int conditionIndex = 1;

	private final Net net;
	private final Set<Condition> initialConditions; // Min(O)
	private final Formula<Variable> initialPredicate;
	private static final Comparator<Event> ORDER = Comparator.comparing(Event::coneConfiguration, Order::compare);
	private final PriorityQueue<Event> possibleExtensions = new PriorityQueue<>(ORDER);
	private final Map<Set<Place>, Set<Event>> marks = new HashMap<>();

	private final Map<Condition, Set<Event>> conditionPostset = new HashMap<>();

	public static Unfolding unfold(Net net, int depth, boolean showInternalVariables) {
		Unfolding ans = new Unfolding(net, depth, showInternalVariables);
		ans.construct();
		return ans;
	}

	private Unfolding(Net original, int depthBound, boolean showInternalVariables) {
		this.depthBound = depthBound;
		this.net = original;
		this.SHOW_DEBUG = showInternalVariables;
		this.initialConditions = this.net.initialMarking().tokens().keySet().stream()
				.sorted(Comparator.comparingInt(Place::index))
				.map(place -> new Condition(this.conditionIndex++, place, Optional.empty(), new Variable(place.name() + "_init")))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		this.initialPredicate = this.initialConditions.stream()
				.map(condition -> condition.preVariable().eq(net.initialMarking().tokens().get(condition.place())))
				.collect(Formula.and());
	}

	private void construct() {
		System.out.println("Initialize unfolding with " + net.initialMarking().tokens());
		for (Condition initialCondition : initialConditions) {
			this.addCondition(initialCondition);
		}
		System.out.println("Initialization done");

		Event e;
		System.out.println("Possible Extensions: " + this.possibleExtensions);
		while ((e = this.possibleExtensions.poll()) != null) {
			System.out.println("Next event " + e + " with preset " + e.preset().keySet());
			if (e.coneConfiguration().events().stream().anyMatch(Event::isCutoff)) {
				System.out.println("  " + e + " is cut-off event. skipping.");
				// if (!Collections.disjoint(e.coneConfiguration().events(), cutoff))
				continue;
			}
			for (Map.Entry<Place, Variable> post : e.transition().postSet().entrySet()) {
				this.addCondition(makeCondition(e, post.getKey(), post.getValue()));
			}
			this.addEvent(e);
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
		return new Condition(this.conditionIndex++, correspondingPlace, Optional.of(preEvent), eventToConditionVariable);
	}

	private void addEvent(Event event) {
		event.preset().forEach((condition, variable) -> conditionPostset.computeIfAbsent(condition, c -> new HashSet<>()).add(event));
		if (event.depth() >= depthBound) {
			event.setCutoff(CutoffReason.DEPTH);
			return;
		}
		if (CUTOFF) {
			event.calcContext(initialConditions);
			Set<Place> mark = mark(event);
			if (this.marks.containsKey(mark)) {
				Set<Event> events = this.marks.get(mark);
				Formula<Variable> collect = event.coloredCutPredicate(initialPredicate)
						.implies(events.stream()
								.filter(otherEvent -> ORDER.compare(otherEvent, event) < 0)
								.map(otherEvent -> otherEvent.coloredCutPredicate(initialPredicate))
								.collect(Formula.or()));
				System.out.println("  Checking if " + event + " with h(cut(cone(" + event.name() + "))) = " + mark + " is cut-off event. Is cut-off, if tautology:");
				if (Predicate.isTautology(collect)) {
					event.setCutoff(CutoffReason.CUT_OFF_CONDITION);
				}
				events.add(event);
			} else {
				this.marks.put(mark, new HashSet<>(Set.of(event)));
			}
		}
	}

	private Set<Place> mark(Event event) {
		return event.coneCut().stream()
				.map(Condition::place)
				.collect(Collectors.toSet());
	}

	private void addCondition(Condition condition) {
		System.out.println("  Add condition " + condition);
		this.concurrencyMatrix.add(condition);
		condition.preset().ifPresent(event -> event.postset().add(condition));

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
				if (!Predicate.isSatisfiable(event.conePredicate(initialPredicate))) {
					//System.out.println("  Conflict (color) for " + transition + " " + Arrays.toString(candidate));
					continue;
				}
				this.eventIndex++;
				System.out.println("    Extend PE with " + event + " " + Arrays.toString(candidate));
				this.possibleExtensions.add(event);
			}
		}
	}

	private final ConcurrencyMatrix concurrencyMatrix = new ConcurrencyMatrix();

	// sparse symmetric matrix with boolean cells. fast logical and of previous rows forms new rows. diagonal entries are always false
	private class ConcurrencyMatrix {
		private final Map<Condition, Set<Condition>> storage = new HashMap<>();

		public Set<Condition> co(Condition key) {
			return this.storage.computeIfAbsent(key, k -> new HashSet<>());
		}

		public void add(Condition newCondition) {
			Set<Condition> cob = newCondition.prepre().stream()
					.map(this::co)
					.reduce(Sets::intersection)
					.orElseGet(Collections::emptySet);
			Set<Condition> post = newCondition.preset()
					.map(Event::postset)
					.orElseGet(() -> Sets.intersection(initialConditions, this.storage.keySet()));
			Set<Condition> result = new HashSet<>(Sets.union(cob, post));
			this.storage.put(newCondition, result);
			for (Condition condition : result) {
				this.co(condition).add(newCondition);
			}
			//try {
			//	System.out.println(this);
			//} catch (Exception e) {
			//	System.out.println("matrix: " + this.storage);
			//}
		}

		public boolean isCoset(Condition[] candidate) {
			for (int i = candidate.length - 1; i >= 1; i--) {
				Set<Condition> co = co(candidate[i]);
				for (int j = 0; j < i; j++) {
					if (!co.contains(candidate[j])) {
						return false;
					}
				}
			}
			return true;
		}

		@Override
		public String toString() {
			List<Condition> order = this.storage.keySet().stream()
					.sorted(Comparator.comparingInt(Condition::index))
					.toList();
			return Main.renderTable(Main.toTable(this.storage), order, order);
		}
	}

	public void render(Writer writer) throws IOException {
		new Renderer().render(writer);
	}

	private class Renderer {
		private final Set<Condition> conditions = new LinkedHashSet<>();
		private final Set<Event> events = new LinkedHashSet<>();

		private void collectNodes(Condition condition) {
			conditions.add(condition);
			for (Event event : conditionPostset.getOrDefault(condition, Collections.emptySet())) {
				collectNodes(event);
			}
		}

		private void collectNodes(Event event) {
			events.add(event);
			for (Condition condition : event.postset()) {
				collectNodes(condition);
			}
		}

		private String nodeName(Condition node) {
			return node.toString();
		}

		private String nodeName(Event node) {
			return node.toString();
		}

		public void render(Writer writer) throws IOException {
			for (Condition condition : initialConditions) {
				collectNodes(condition);
			}
			writer.append("digraph ").append("net").append(" {\n");
			if (!conditions.isEmpty()) {
				writer.append("node[shape=ellipse];\n");
				for (var node : conditions) {
					writer
							.append("\"").append(nodeName(node)).append("\"");

					var initialToken = net.initialMarking().tokens().get(node.place());
					if (initialToken != null) {
						writer.append("[label=\"").append(nodeName(node)).append(" = ").append(String.valueOf(initialToken)).append("\"]");
					}
					writer.append("\n");
				}
			}
			if (!events.isEmpty()) {
				writer.append("node[shape=box];\n");
				for (var node : events) {
					writer
							.append("\"").append(nodeName(node)).append("\"");
					Map<String, String> options = new HashMap<>();
					node.cutoffReason().ifPresent(cutoffReason -> {
						switch (cutoffReason) {
							case CUT_OFF_CONDITION -> options.put("color", "red");
							case DEPTH -> options.put("color", "darkred");
						}
					});
					StringJoiner xlabel = new StringJoiner("\n");
					if (SHOW_DEBUG) {
						if (node.coneCut() != null) {
							xlabel.add("h(cut) = " + mark(node));
						}
						if (!Formula.top().equals(node.localPredicate())) {
							xlabel.add(node.localPredicate().toString());
						}
					} else {
						if (!Formula.top().equals(node.transition().guard())) {
							xlabel.add(node.transition().guard().toString());
						}
					}

					options.put("xlabel", xlabel.toString());
					if (!options.isEmpty()) {
						writer.append(options.entrySet().stream()
								.map(e -> e.getKey() + "=\"" + e.getValue() + "\"")
								.collect(Collectors.joining(",", "[", "]")));
					}
					writer.append("\n");
				}
			}

			for (var to : conditions) {
				if (to.preset().isPresent()) {
					var from = to.preset().get();
					writer.append("\"").append(nodeName(from))
							.append("\" -> \"")
							.append(nodeName(to)).append("\"")
							.append(" [label=\"");
					if (SHOW_DEBUG) {
						writer.append(to.preVariable().name());
					} else {
						writer.append(from.transition().postSet().get(to.place()).name());
					}
					writer.append("\"]")
							.append("\n");
				}
			}
			for (var to : events) {
				for (var from : to.preset().entrySet()) {
					writer.append("\"").append(nodeName(from.getKey()))
							.append("\" -> \"")
							.append(nodeName(to)).append("\"")
							.append(" [label=\"");
					if (SHOW_DEBUG) {
						writer.append(from.getValue().name());
					} else {
						writer.append(to.transition().preSet().get(from.getKey().place()).name());
					}
					writer
							.append("\"]")
							.append("\n");
				}
			}
			writer.append("}\n");
		}

	}
}
