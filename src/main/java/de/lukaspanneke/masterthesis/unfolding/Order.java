package de.lukaspanneke.masterthesis.unfolding;

import de.lukaspanneke.masterthesis.components.Configuration;
import de.lukaspanneke.masterthesis.components.IEvent;

import java.util.*;
import java.util.stream.Collectors;

public class Order {

	public static int compare(Configuration c1, Configuration c2) {
		return Comparator.comparingInt(Configuration::size)
				.thenComparing(Configuration::parikh)
				.thenComparing(Configuration::foata)
				.compare(c1, c2);
	}

	public static class Parikh implements Comparable<Parikh> {
		private static final Comparator<Iterable<IEvent>> ORDER = new LexicographicOrder<>(IEvent::compareTo);
		private final List<IEvent> data;

		public Parikh(Set<IEvent> configuration) {
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
		private static final Comparator<Iterable<Configuration>> ORDER = new LexicographicOrder<>(Comparator.comparing(Configuration::parikh));
		private final List<Configuration> data;

		public Foata(Set<IEvent> configuration) {
			this.data = configuration.stream()
					.collect(Collectors.groupingBy(IEvent::depth))
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

	public static class LexicographicOrder<T> implements Comparator<Iterable<T>> {
		private final Comparator<T> elementOrder;

		public LexicographicOrder(Comparator<T> elementOrder) {
			this.elementOrder = elementOrder;
		}

		@Override
		public int compare(Iterable<T> leftIterable, Iterable<T> rightIterable) {
			Iterator<T> left = leftIterable.iterator();
			Iterator<T> right = rightIterable.iterator();
			while (left.hasNext()) {
				if (!right.hasNext()) {
					return 1; // right is longer -> bigger
				}
				int result = elementOrder.compare(left.next(), right.next());
				if (result != 0) {
					return result;
				}
			}
			if (right.hasNext()) {
				return -1; // left is longer -> bigger
			}
			return 0;
		}
	}
}