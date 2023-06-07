package org.example;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import org.example.components.*;
import org.example.net.Net;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

public class BoundedUnfolding {

	private final int depthBound;

	private int eventIndex = 0;
	private int conditionIndex = 0;

	private final Net net;
	private final Set<Condition> initialConditions; // Min(O)
	private final PriorityQueue<Event> possibleExtensions = new PriorityQueue<>(Comparator.comparing(Event::coneConfiguration, Order::compare));

	public static BoundedUnfolding unfold(Net net, int depth) {
		BoundedUnfolding ans = new BoundedUnfolding(net, depth);
		ans.construct();
		return ans;
	}

	private BoundedUnfolding(Net original, int depthBound) {
		this.depthBound = depthBound;
		this.net = original;
		this.initialConditions = this.net.initialMarking().tokens().keySet().stream()
				.map(place -> new Condition(this.conditionIndex++, place, Optional.empty(), new Predicate()))
				.collect(Collectors.toUnmodifiableSet());
	}

	private boolean isCutoff(Event event) {
		return event.depth() > this.depthBound;
	}

	private void construct() {
		for (Condition initialCondition : initialConditions) {
			this.addCondition(initialCondition);
		}
		Set<Event> cutoff = new HashSet<>();
		System.out.println("Initialization done");

		Event e;
		System.out.println("Possible Extensions: " + this.possibleExtensions);
		while ((e = this.possibleExtensions.poll()) != null) {
			if (!Collections.disjoint(e.coneConfiguration().events(), cutoff)) {
				continue;
			}
			this.addEvent(e);
			for (Place place : e.transition().postSet()) {
				this.addCondition(new Condition(this.conditionIndex++, place, Optional.of(e), new Predicate()));
			}

			if (isCutoff(e)) {
				cutoff.add(e);
				e.setCutoff();
			}
			System.out.println("Possible Extensions: " + this.possibleExtensions);
		}
	}

	private void addEvent(Event event) {
		System.out.println("Add event " + event + " with preset " + event.preset());
		event.preset().forEach(condition -> condition.postset().add(event));
	}

	private void addCondition(Condition condition) {
		System.out.println("add condition " + condition);
		condition.preset().ifPresent(event -> event.postset().add(condition));
		this.concurrencyMatrix.add(condition);

		Map<Place, List<Condition>> placeToConditions = this.concurrencyMatrix.get(condition).stream()
				.collect(Collectors.groupingBy(Condition::place));
		if (placeToConditions.containsKey(condition.place())) throw new AssertionError();
		placeToConditions.put(condition.place(), List.of(condition));
		for (Transition transition : condition.place().postSet()) {
			for (List<Condition> coset : Lists.cartesianProduct(transition.preSet().stream().map(place -> placeToConditions.getOrDefault(place, Collections.emptyList())).toList())) {
				this.possibleExtensions.add(new Event(this.eventIndex++, transition, coset, new Predicate()));
			}
		}
	}

	private final ConcurrencyMatrix concurrencyMatrix = new ConcurrencyMatrix();

	// sparse symmetric matrix with boolean cells. fast logical and of previous rows forms new rows. diagonal entries are always false
	private class ConcurrencyMatrix {
		private final Map<Condition, Set<Condition>> storage = new HashMap<>();

		public Set<Condition> get(Condition key) {
			return this.storage.computeIfAbsent(key, k -> new HashSet<>());
		}

		public void add(Condition newCondition) {
			Set<Condition> cob = newCondition.prepre().stream()
					.map(this::get)
					.reduce(Sets::intersection)
					.orElseGet(Collections::emptySet);
			Set<Condition> post = newCondition.preset().map(Event::postset).orElse(initialConditions).stream()
					.filter(this.storage::containsKey)
					.collect(Collectors.toSet());
			Set<Condition> result = new HashSet<>(Sets.union(cob, post));
			this.storage.put(newCondition, result);
			for (Condition condition : result) {
				this.get(condition).add(newCondition);
			}
		}

		public Table<Condition, Condition, Boolean> toTable() {
			OptionalInt max = this.storage.values().stream()
					.mapToInt(Set::size)
					.max();
			Table<Condition, Condition, Boolean> table = HashBasedTable.create(this.storage.size(), max.orElse(0));

			for (Map.Entry<Condition, Set<Condition>> e : this.storage.entrySet()) {
				Condition row = e.getKey();
				for (Condition col : e.getValue()) {
					table.put(row, col, true);
				}
			}
			return table;
		}

		@Override
		public String toString() {
			List<Condition> order = this.storage.keySet().stream()
					.sorted(Comparator.comparingInt(Condition::index))
					.toList();
			return Main.renderTable(this.toTable(), order, order);
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
					if (node.isCutoff()) {
						writer.append("[color=\"red\"]");
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
