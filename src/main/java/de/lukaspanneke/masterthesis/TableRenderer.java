package de.lukaspanneke.masterthesis;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TableRenderer {
	public static <R, C> String renderTable(Map<R, Set<C>> table, Collection<R> rowOrder, Collection<C> columnOrder, Function<R, String> rowLabel, Function<C, String> columnLabel) {
		Map<C, Integer> maxColumnWidth = columnOrder.stream()
				.collect(Collectors.toMap(col -> col, col -> columnLabel.apply(col).length()));
		int rowHeaderWidth = table.keySet().stream()
				.map(rowLabel)
				.mapToInt(String::length)
				.max().orElse(0);

		String format = "%-" + rowHeaderWidth + "s │ " + columnOrder.stream()
				.map(maxColumnWidth::get)
				.map(width -> "%-" + (width) + "s")
				.collect(Collectors.joining(" ┆ "));

		String header = String.format(format, Stream.concat(
				Stream.of(""),
				columnOrder.stream().map(columnLabel)
		).toArray());
		String horizontalLine = Stream.concat(Stream.of(rowHeaderWidth), columnOrder.stream().map(maxColumnWidth::get))
				.map("─"::repeat)
				.collect(Collectors.joining("─┼─"));

		String body = rowOrder.stream()
				.map(r -> String.format(format, Stream.concat(
								Stream.of(rowLabel.apply(r)),
								columnOrder.stream().map(c -> table.getOrDefault(r, Set.of()).contains(c) ? "1" : " "))
						.toArray()))
				.collect(Collectors.joining("\n"));
		return header + "\n" + horizontalLine + "\n" + body;
	}
}
