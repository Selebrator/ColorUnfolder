package org.example;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import org.example.components.*;
import org.example.net.Net;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class Unfolder {

	public static Prefix unfold(Net net) {
		Prefix prefix = new Prefix(net);
		prefix.construct();
		return prefix;
	}

	static class Prefix {
		private int eventIndex = 0;
		private int conditionIndex = 0;

		private final Net net;
		public final PriorityQueue<Event> possibleExtensions = new PriorityQueue<>(Comparator.comparing(Event::coneConfiguration, Order::compare));
		private final Map<Place, Set<Condition>> conditions = new HashMap<>();
		private final Set<Condition> initialConditions;

		private Prefix(Net original) {
			this.net = original;
			Set<Condition> initialConditions = new HashSet<>();
			for (Map.Entry<Place, Object> markedPlace : net.initialMarking().tokens().entrySet()) {
				Condition condition = new Condition(this.conditionIndex++, markedPlace.getKey(), Optional.empty(), new Predicate());
				initialConditions.add(condition);
			}
			this.initialConditions = Collections.unmodifiableSet(initialConditions);
		}

		private boolean isCutoff(Event event) {
			return event.depth() > 4;
		}

		private void construct() {
			System.out.println("begin unfolding construction");

			System.out.println("InitialConditions: " + initialConditions);
			for (Condition initialCondition : initialConditions) {
				this.addCondition(initialCondition);
			}

			System.out.println("Initialization done");
			//System.out.println("Concurrency matrix:\n" + concurrencyMatrix);

			Set<Event> cutoff = new HashSet<>();

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
				//System.out.println("new concurrency matrix:\n" + concurrencyMatrix);

				if (isCutoff(e)) {
					cutoff.add(e);
				}
				System.out.println("Possible Extensions: " + this.possibleExtensions);
			}
		}

		public void addEvent(Event event) {
			System.out.println("Add event " + event + " with preset " + event.preset());
			event.preset().forEach(condition -> condition.postset().add(event));
		}

		private Set<Condition> getConditionsForPlace(Place place) {
			return this.conditions.computeIfAbsent(place, p -> new HashSet<>());
		}

		public void addCondition(Condition condition) {
			getConditionsForPlace(condition.place()).add(condition);
			condition.preset().ifPresent(event -> event.postset().add(condition));
			this.concurrencyMatrix.add(condition);
			transitionsLoop:
			for (Transition postTransition : condition.place().postSet()) {
				List<Set<Condition>> aaa = new ArrayList<>();
				for (Place siblingPlace : postTransition.preSet()) {
					if (siblingPlace.equals(condition.place())) {
						continue;
					}
					Set<Condition> siblingConditions = getConditionsForPlace(siblingPlace);
					Sets.SetView<Condition> concurrentSiblings = Sets.intersection(siblingConditions, this.concurrencyMatrix.get(condition));
					if (siblingConditions.isEmpty()) {
						// the considered transition has a place in it's preset
						// for which no condition has been discovered yet.
						// thus, the transition can't be fired, and the other places don't matter.
						continue transitionsLoop;
					}
					aaa.add(concurrentSiblings);
				}
				Set<List<Condition>> candidates = Sets.cartesianProduct(aaa);
				for (List<Condition> cosetCandidate : candidates) {
					if (this.concurrencyMatrix.isCoset(condition, cosetCandidate)) {
						ArrayList<Condition> coset = new ArrayList<>(cosetCandidate);
						coset.add(condition);
						this.possibleExtensions.add(new Event(this.eventIndex++, postTransition, coset, new Predicate()));
					} else {
						throw new AssertionError("each condition from the candidate has been picked to be concurrent to the new condition");
					}
				}
			}
		}

		private final ConcurrencyMatrix concurrencyMatrix = new ConcurrencyMatrix();

		// sparse symmetric matrix with boolean cells. fast logical and of previous rows forms new rows. diagonal entries are always false
		class ConcurrencyMatrix {
			private final Map<Condition, Set<Condition>> storage = new HashMap<>();

			public Set<Condition> get(Condition key) {
				return this.storage.computeIfAbsent(key, k -> new HashSet<>());
			}

			public void add(Condition newCondition) {
				Set<Condition> cob = newCondition.prepre().stream()
						.map(this::get)
						.reduce(Sets::intersection)
						.orElseGet(Collections::emptySet);
				Set<Condition> post = newCondition.preset()
						.map(Event::postset)
						.orElse(initialConditions);
				Set<Condition> result = new HashSet<>(Sets.union(cob, Sets.difference(post, Set.of(newCondition))));
				this.storage.put(newCondition, result);
				for (Condition condition : result) {
					get(condition).add(newCondition);
				}
			}

			public boolean isCoset(Condition initiator, Collection<Condition> candidate) {
				return get(initiator).containsAll(candidate);
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

//        private boolean hasCausalConflict(Collection<Condition> conditions) {
//            for (Condition x : conditions) {
//                for (Condition y : conditions) {
//                    if (causalRelation(x, y)) {
//                        return true;
//                    }
//                }
//            }
//            return false;
//        }
//
//        private boolean causalRelation(Condition x, Condition y) {
//            return y.preset().map(event -> event.coneConfiguration().events()).orElseGet(Collections::emptySet)
//                    .stream()
//                    .flatMap(event -> event.inputConditions().stream())
//                    .collect(Collectors.toSet())
//                    .contains(x);
//        }
//
//        /**
//         * From every condition, walk up it's history and search for a shared place
//         */
//        private boolean hasStructuralConflict(Collection<Condition> conditions) {
//            if (conditions.size() <= 1) {
//                return false;
//            }
//            Comparator<Condition> order = Comparator.comparingInt(condition -> condition.preset().map(Event::depth).orElse(0));
//            PriorityQueue<Condition> q = new PriorityQueue<>(conditions.size(), order.reversed());
//
//            q.addAll(conditions);
//
//            Set<Condition> split = new HashSet<>();
//            Condition next;
//            while ((next = q.poll()) != null) {
//                if (next.preset().isEmpty()) {
//                    continue;
//                }
//                Set<Condition> pre = next.preset().map(Event::inputConditions).orElseGet(Collections::emptySet);
//                for (Condition condition : pre) {
//                    if (this.post(condition).size() > 1) {
//                        boolean added = split.add(condition);
//                        if (!added) { // already present
//                            return true;
//                        }
//                    }
//                }
//                q.addAll(pre);
//            }
//            return false;
//        }

		public class Renderer {
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
								.append("\"").append(node.toString()).append("\"")
								//.append("[xlabel=\"").append(node.toString()).append("\"]")
								.append("\n");
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
}
