package org.example;

import com.google.common.collect.Table;
import org.example.components.Place;
import org.example.components.Transition;
import org.example.net.Marking;
import org.example.net.Net;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;

public class Main {
	public static void main(String[] args) throws IOException {
		var p0 = new Place(0, emptySet());
		var p1 = new Place(1, emptySet());
		var p2 = new Place(2, emptySet());
		var t0 = new Transition(0, Set.of(p0, p1));
		var t1 = new Transition(1, Set.of(p1, p2));
		var p3 = new Place(3, Set.of(t0));
		var p4 = new Place(4, Set.of(t0));
		var p5 = new Place(5, Set.of(t1));
		var t2 = new Transition(2, Set.of(p3));
		var t3 = new Transition(3, Set.of(p4));
		var t4 = new Transition(4, Set.of(p5));
		var p6 = new Place(6, Set.of(t2));
		var p7 = new Place(7, Set.of(t3));
		var p8 = new Place(8, Set.of(t4));
		var t5 = new Transition(5, Set.of(p6, p7));
		var t6 = new Transition(6, Set.of(p8));
		var p9 = new Place(9, Set.of(t5));
		t6.postSet().add(p5);
		p5.preSet().add(t6);
		Net net = new Net(new Marking(Map.of(p0, 1, p1, 1, p2, 1)));


		//var p1 = new Place(1, Set.of());
		//var t1 = new Transition(1, Set.of(p1));
		//var p2 = new Place(2, Set.of(t1));
		//var t2 = new Transition(2, Set.of(p2));
		//t2.postSet().add(p2);
		//p2.preSet().add(t2);
		//var t3 = new Transition(3, Set.of(p2));
		//var p3 = new Place(3, Set.of(t3));
		//Net net = new Net(new Marking(Map.of(p1, 0)));

		BoundedUnfolding unf = BoundedUnfolding.unfold(net, 4);
		try (StringWriter stringWriter = new StringWriter()) {
			unf.render(stringWriter);
			System.out.println(stringWriter.toString());
		}
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
