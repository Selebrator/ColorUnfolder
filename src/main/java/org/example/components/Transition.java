package org.example.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record Transition(int index, String name, Map<Place, Variable> preSet, Map<Place, Variable> postSet,
						 Optional<Predicate> guard) {

	public Transition(int index) {
		this(index, "t" + index);
	}

	public Transition(int index, Predicate guard) {
		this(index, "t" + index, guard);
	}

	public Transition(int index, String name) {
		this(index, name, null);
	}

	public Transition(int index, String name, Predicate guard) {
		this(index, name, new HashMap<>(), new HashMap<>(), Optional.ofNullable(guard));
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
