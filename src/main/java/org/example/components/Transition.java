package org.example.components;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record Transition(int index, String name, Set<Place> preSet, Set<Place> postSet) {

	public Transition(int index) {
		this(index, Collections.emptySet());
	}

	public Transition(int index, String name) {
		this(index, name, Collections.emptySet());
	}

	public Transition(int index, Set<Place> preSet) {
		this(index, "t" + index, preSet);
	}

	public Transition(int index, String name, Set<Place> preSet) {
		this(index, name, new HashSet<>(preSet), new HashSet<>());
		preSet.forEach(pre -> pre.postSet().add(this));
	}

	@Override
	public String toString() {
		return "Transition{" +
				"name='" + name + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Transition that = (Transition) o;
		return index == that.index;
	}

	@Override
	public int hashCode() {
		return Objects.hash(index);
	}
}
