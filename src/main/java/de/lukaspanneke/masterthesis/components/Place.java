package de.lukaspanneke.masterthesis.components;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record Place(int index, String name, Set<Transition> preSet, Set<Transition> postSet) {

	public Place(int index) {
		this(index, "p" + index);
	}

	public Place(int index, String name) {
		this(index, name, new HashSet<>(), new HashSet<>());
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
		return Objects.hash(index);
	}
}
