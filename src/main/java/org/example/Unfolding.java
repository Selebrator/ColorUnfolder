package org.example;

import com.google.common.collect.Sets;
import org.example.components.*;
import org.example.net.Net;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

public class Unfolding {

	private final int depthBound;

	private int eventIndex = 1;
	private int conditionIndex = 1;

	private final Net net;
	private final Set<Condition> initialConditions; // Min(O)
	private final PriorityQueue<Event> possibleExtensions = new PriorityQueue<>(Comparator.comparing(Event::coneConfiguration, Order::compare));
	private final Map<Set<Place>, Set<Event>> marks = new HashMap<>();

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
				.map(place -> new Condition(this.conditionIndex++, place, Optional.empty(), new Predicate()))
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	private void construct() {
		for (Condition initialCondition : initialConditions) {
			this.addCondition(initialCondition);
		}
		System.out.println("Initialization done");

		Event e;
		System.out.println("Possible Extensions: " + this.possibleExtensions);
		while ((e = this.possibleExtensions.poll()) != null) {
			System.out.println("Next event " + e + " with preset " + e.preset());
			if (e.coneConfiguration().events().stream().anyMatch(Event::isCutoff)) {
				// if (!Collections.disjoint(e.coneConfiguration().events(), cutoff))
				continue;
			}
			for (Place place : e.transition().postSet()) {
				this.addCondition(new Condition(this.conditionIndex++, place, Optional.of(e), new Predicate()));
			}
			this.addEvent(e);
			//if (e.depth() >= depthBound) {
			//	e.setCutoff();
			//}
			System.out.println("Possible Extensions: " + this.possibleExtensions);
		}
	}

	private void addEvent(Event event) {
		System.out.println("Add event " + event + " with preset " + event.preset());
		event.preset().forEach(condition -> condition.postset().add(event));
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

	private Set<Place> mark(Event event) {
		return event.coneCut().stream()
				.map(Condition::place)
				.collect(Collectors.toSet());
	}

	private void addCondition(Condition condition) {
		System.out.println("add condition " + condition);
		this.concurrencyMatrix.add(condition);
		condition.preset().ifPresent(event -> event.postset().add(condition));

		Map<Place, List<Condition>> placeToConditions = this.concurrencyMatrix.co(condition).stream()
				.collect(Collectors.groupingBy(Condition::place));
		if (placeToConditions.containsKey(condition.place())) throw new AssertionError();
		placeToConditions.put(condition.place(), List.of(condition));
		for (Transition transition : condition.place().postSet()) {
			for (Condition[] candidate : new CartesianProduct<>(Condition[]::new, transition.preSet().stream().map(place -> placeToConditions.getOrDefault(place, Collections.emptyList())).toList())) {
				if (!this.concurrencyMatrix.isCoset(candidate)) {
					continue;
				}
				this.possibleExtensions.add(new Event(this.eventIndex++, transition, List.of(candidate), new Predicate()));
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
			for (Event event : condition.postset()) {
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
					if (node.isCutoff()) {
						options.put("xlabel", node.cutoffReason.toString());
					} else {
						options.put("xlabel", mark(node).toString());
					}
					if (!options.isEmpty()) {
						writer.append(options.entrySet().stream()
								.map(e -> e.getKey() + "=\"" + e.getValue() + "\"")
								.collect(Collectors.joining(",", "[", "]")));
					}
					writer.append("\n");
				}
			}

			for (var from : conditions) {
				for (var to : from.postset()) {
					writer.append("\"").append(from.toString()).append("\" -> \"").append(to.toString()).append("\"\n");
				}
			}
			for (var from : events) {
				for (var to : from.postset()) {
					writer.append("\"").append(from.toString()).append("\" -> \"").append(to.toString()).append("\"\n");
				}
			}
			writer.append("}\n");
		}

	}
}
