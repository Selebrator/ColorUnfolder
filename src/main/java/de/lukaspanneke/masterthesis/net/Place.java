package de.lukaspanneke.masterthesis.net;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record Place(
		int index,
		String name,
		Set<Transition> preSet,
		Set<Transition> postSet,
		Integer value // the value of a low-level place representing a high-level place. used when just in time expanding.
) implements Comparable<Place> {

	public Place(int index) {
		this(index, "p" + index);
	}

	public Place(int index, String name) {
		this(index, name, new HashSet<>(), new HashSet<>(), null);
	}

	public Place(int index, String name, int value) {
		this(index, name, new HashSet<>(), new HashSet<>(), value);
	}

	@Override
	public int compareTo(Place that) {
		return Integer.compare(this.index, that.index);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Place place = (Place) o;
		return index == place.index;
	}

	@Override
	public int hashCode() {
		return index;
	}
}
