package de.lukaspanneke.masterthesis.unfolding;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import de.lukaspanneke.masterthesis.components.Condition;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// sparse symmetric matrix with boolean cells. fast logical and of previous rows forms new rows. diagonal entries are always false
public class ConcurrencyMatrix {
	private final Map<Condition, Set<Condition>> storage = new HashMap<>();

	public Set<Condition> co(Condition key) {
		return this.storage.computeIfAbsent(key, k -> new HashSet<>());
	}

	public void add(Condition newCondition) {
		Set<Condition> cob = newCondition.prepre().stream()
				.map(this::co)
				.reduce(Sets::intersection)
				.orElseGet(Collections::emptySet);
		Set<Condition> result = new HashSet<>(Sets.union(cob, newCondition.preset().postset()));
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
		return renderTable(toTable(this.storage), order, order);
	}

	private static <R, C> Table<R, C, Boolean> toTable(Map<R, Set<C>> map) {
		OptionalInt max = map.values().stream()
				.mapToInt(Set::size)
				.max();
		Table<R, C, Boolean> table = HashBasedTable.create(map.size(), max.orElse(0));

		for (Map.Entry<R, Set<C>> e : map.entrySet()) {
			R row = e.getKey();
			for (C col : e.getValue()) {
				table.put(row, col, true);
			}
		}
		return table;
	}

	private static <R, C> String renderTable(Table<R, C, Boolean> table, Collection<R> rowOrder, Collection<C> columnOrder) {
		Map<C, Integer> maxColumnWidth = table.columnMap().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> Stream.concat(
								Stream.of(entry.getKey()).map(Objects::toString),
								entry.getValue().values().stream().map(Objects::toString))
						.mapToInt(String::length)
						.max().orElse(0)));
		int rowHeaderWidth = table.rowKeySet().stream()
				.map(Objects::toString)
				.mapToInt(String::length)
				.max().orElse(0);

		String format = "%-" + rowHeaderWidth + "s │ " + columnOrder.stream()
				.map(maxColumnWidth::get)
				.map(width -> "%-" + (width) + "s")
				.collect(Collectors.joining(" ┆ "));

		String header = String.format(format, Stream.concat(
				Stream.of(""),
				columnOrder.stream().map(Objects::toString)
		).toArray());
		String horizontalLine = Stream.concat(Stream.of(rowHeaderWidth), columnOrder.stream().map(maxColumnWidth::get))
				.map("─"::repeat)
				.collect(Collectors.joining("─┼─"));

		String body = rowOrder.stream()
				.map(r -> String.format(format, Stream.concat(
								Stream.of(r.toString()),
								columnOrder.stream().map(c -> Boolean.TRUE.equals(table.get(r, c)) ? 1 : 0))
						.toArray()))
				.collect(Collectors.joining("\n"));
		return header + "\n" + horizontalLine + "\n" + body;
	}
}