package org.example;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

	public static <R, C> Table<R, C, Boolean> toTable(Map<R, Set<C>> map) {
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

	public static <R, C> String renderTable(Table<R, C, Boolean> table, Collection<R> rowOrder, Collection<C> columnOrder) {
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
