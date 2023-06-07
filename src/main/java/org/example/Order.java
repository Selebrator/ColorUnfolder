package org.example;

import com.google.common.collect.Comparators;
import org.example.components.Configuration;
import org.example.components.Event;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Order {

	public static int compare(Configuration c1, Configuration c2) {
		return Comparator.comparingInt(Configuration::size)
				.thenComparing(Configuration::parikh)
				.thenComparing(Configuration::foata)
				.compare(c1, c2);
	}

	public static class Parikh implements Comparable<Parikh> {
		private static final Comparator<Iterable<Event>> ORDER = Comparators.lexicographical(Event::compareTo);
		private final List<Event> data;

		public Parikh(Set<Event> configuration) {
			this.data = configuration.stream()
					.sorted(Comparator.comparingInt(event -> event.transition().index()))
					.toList();
		}

		@Override
		public int compareTo(Parikh that) {
			return ORDER.compare(this.data, that.data);
		}
	}

	public static class Foata implements Comparable<Foata> {
		private static final Comparator<Iterable<Configuration>> ORDER = Comparators.lexicographical(Comparator.comparing(Configuration::parikh));
		private final List<Configuration> data;

		public Foata(Set<Event> configuration) {
			this.data = configuration.stream()
					.collect(Collectors.groupingBy(Event::depth))
					.entrySet()
					.stream()
					.sorted(Comparator.comparingInt(Map.Entry::getKey))
					.map(Map.Entry::getValue)
					.map(Set::copyOf)
					.map(Configuration::new)
					.toList();
		}

		@Override
		public int compareTo(Foata that) {
			return ORDER.compare(this.data, that.data);
		}
	}
}
