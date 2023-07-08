package de.lukaspanneke.masterthesis.components;

import java.util.*;
import java.util.stream.Collectors;

public final class Configuration implements Comparable<Configuration> {

	private static final Comparator<Configuration> ORDER =
			Comparator.comparingInt(Configuration::size)
					.thenComparing(Configuration::parikh)
					.thenComparing(Configuration::foata);
	private final Set<Event> events;
	private Parikh parikh;
	private Foata foata;

	public Configuration(Set<Event> events) {
		this.events = Set.copyOf(events);
	}

	public int size() {
		return events.size();
	}

	public Set<Event> events() {
		return events;
	}

	private Parikh parikh() {
		if (this.parikh == null) {
			this.parikh = new Parikh(this.events);
		}
		return parikh;
	}

	private Foata foata() {
		if (this.foata == null) {
			this.foata = new Foata(this.events);
		}
		return foata;
	}

	@Override
	public int compareTo(Configuration that) {
		return ORDER.compare(this, that);
	}

	private static class Parikh implements Comparable<Parikh> {
		private static final Comparator<Iterable<Event>> ORDER = new LexicographicOrder<>(Event::compareTo);
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

	private static class Foata implements Comparable<Foata> {
		private static final Comparator<Iterable<Configuration>> ORDER = new LexicographicOrder<>(Comparator.comparing(Configuration::parikh));
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

	private static class LexicographicOrder<T> implements Comparator<Iterable<T>> {
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

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (Configuration) obj;
		return Objects.equals(this.events, that.events);
	}

	@Override
	public int hashCode() {
		return Objects.hash(events);
	}

	@Override
	public String toString() {
		return events.toString();
	}
}
