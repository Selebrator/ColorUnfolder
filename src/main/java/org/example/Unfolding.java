package org.example;

import com.google.common.collect.Sets;
import org.example.components.*;
import org.example.logic.generic.ComparisonOperator;
import org.example.logic.generic.expression.ConstantExpression;
import org.example.logic.generic.formula.ComparisonFormula;
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

	private final Net net;
	private final Set<Condition> initialConditions; // Min(O)
	private final Predicate initialPredicate;
	private final PriorityQueue<Event> possibleExtensions = new PriorityQueue<>(Comparator.comparing(Event::coneConfiguration, Order::compare));
	private final Map<Set<Place>, Set<Event>> marks = new HashMap<>();

	private final Map<Condition, Set<Event>> conditionPostset = new HashMap<>();

	public static Unfolding unfold(Net net, int depth) {
		Unfolding ans = new Unfolding(net, depth);
		ans.construct();
		return ans;
	}

	private Unfolding(Net original, int depthBound) {
		this.depthBound = depthBound;
		this.net = original;
		this.initialConditions = this.net.initialMarking().tokens().keySet().stream()
				.sorted(Comparator.comparingInt(Place::index))
				.map(place -> new Condition(this.conditionIndex++, place, Optional.empty(), new Variable(place.name() + "_init")))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		this.initialPredicate = this.initialConditions.stream()
				.map(condition -> new Predicate(ComparisonFormula.of(condition.preVariable(), ComparisonOperator.EQUALS,
						ConstantExpression.of(net.initialMarking().tokens().get(condition.place())))))
				.reduce(Predicate::and).orElse(Predicate.TRUE);
		System.out.println(initialPredicate);
	}

	private void construct() {
		for (Condition initialCondition : initialConditions) {
			this.addCondition(initialCondition);
		}
		System.out.println("Initialization done");

		Event e;
		System.out.println("Possible Extensions: " + this.possibleExtensions);
		while ((e = this.possibleExtensions.poll()) != null) {
			System.out.println("Next event " + e + " with preset " + e.preset().keySet());
			if (e.coneConfiguration().events().stream().anyMatch(Event::isCutoff)) {
				System.out.println("  is cut-off");
				// if (!Collections.disjoint(e.coneConfiguration().events(), cutoff))
				continue;
			}
			for (Map.Entry<Place, Variable> post : e.transition().postSet().entrySet()) {
				this.addCondition(new Condition(this.conditionIndex++, post.getKey(), Optional.of(e), post.getValue()));
			}
			this.addEvent(e);
			if (!CUTOFF && e.depth() >= depthBound) {
				e.setCutoff(e);
			}
			System.out.println("Possible Extensions: " + this.possibleExtensions);
		}
	}

	private void addEvent(Event event) {
		//System.out.println("Add event " + event + " with preset " + event.preset().keySet());
		event.preset().forEach((condition, variable) -> conditionPostset.computeIfAbsent(condition, c -> new HashSet<>()).add(event));
		if (CUTOFF) {
			event.calcContext(initialConditions);
			Set<Place> mark = mark(event);
			if (this.marks.containsKey(mark)) {
				Set<Event> events = this.marks.get(mark);
				events.stream()
						.filter(o -> Order.compare(o.coneConfiguration(), event.coneConfiguration()) < 0)
						.findAny()
						.ifPresent(event::setCutoff);
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
		System.out.println("Add condition " + condition);
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
					System.out.println("  Conflict (structure) " + transition + " " + Arrays.toString(candidate));
					continue;
				}
				Map<Condition, Variable> preset = Arrays.stream(candidate)
						.collect(Collectors.toMap(Function.identity(), cond -> transition.preSet().get(cond.place())));
				Event event = new Event(this.eventIndex, transition, preset);
				if (!initialPredicate.and(event.conePredicate()).isSatisfiable()) {
					System.out.println("  Conflict (color) for " + transition + " " + Arrays.toString(candidate));
					continue;
				}
				this.eventIndex++;
				System.out.println("  Extend PE with " + event + " " + Arrays.toString(candidate));
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

		public void render(Writer writer) throws IOException {
			for (Condition condition : initialConditions) {
				collectNodes(condition);
			}
			writer.append("digraph ").append("net").append(" {\n");
			if (!conditions.isEmpty()) {
				writer.append("node[shape=ellipse];\n");
				for (var node : conditions) {
					writer
							.append("\"").append(node.toString()).append("\"")
							//.append("[xlabel=\"").append(node.toString()).append("\"]")
							.append("\n");
				}
			}
			if (!events.isEmpty()) {
				writer.append("node[shape=box];\n");
				for (var node : events) {
					writer
							.append("\"").append(node.toString()).append("\"");
					Map<String, String> options = new HashMap<>();
					if (node.isCutoff()) {
						options.put("color", "red");
					}
					StringJoiner xlabel = new StringJoiner("\n");
					if (CUTOFF) {
						if (node.isCutoff()) {
							xlabel.add("mark same as " + node.cutoffReason);
						} else {
							xlabel.add("mark: " + mark(node).toString());
						}
					}
					node.transition().guard().ifPresent(guard -> xlabel.add(guard.formula().toString()));
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
					writer.append("\"").append(to.preset().get().toString())
							.append("\" -> \"")
							.append(to.toString()).append("\"")
							.append(" [label=\"").append(to.preVariable().name()).append("\"]")
							.append("\n");
				}
			}
			for (var to : events) {
				for (var from : to.preset().entrySet()) {
					writer.append("\"").append(from.getKey().toString())
							.append("\" -> \"")
							.append(to.toString()).append("\"")
							.append(" [label=\"").append(from.getValue().name()).append("\"]")
							.append("\n");
				}
			}
			writer.append("}\n");
		}

	}
}
