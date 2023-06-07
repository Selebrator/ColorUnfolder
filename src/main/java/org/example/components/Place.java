package org.example.components;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record Place(int index, String name, Set<Transition> preSet, Set<Transition> postSet) {
	public Place(int index, Set<Transition> preSet) {
		this(index, "p" + index, preSet);
	}

	public Place(int index, String name, Set<Transition> preSet) {
		this(index, name, new HashSet<>(preSet), new HashSet<>());
		preSet.forEach(pre -> pre.postSet().add(this));
	}

	@Override
	public String toString() {
		return "Place{" +
				"name='" + name + '\'' +
				'}';
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
