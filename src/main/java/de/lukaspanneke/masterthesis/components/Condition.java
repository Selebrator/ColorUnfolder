package de.lukaspanneke.masterthesis.components;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/* Place (p, e, pred) in B */
public record Condition(int index, String name, Place place, Event preset, Variable preVariable, Set<Event> postset) {
	public Condition(int index, Place place, Event preset, Variable preVariable) {
		this(index, "b" + index, place, preset, preVariable, new HashSet<>());
	}

	@Override
	public String toString() {
		return name + "(" + place.name() + ")";
	}

	public Set<Condition> prepre() {
		return this.preset().preset();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Condition condition = (Condition) o;
		return index == condition.index;
	}

	@Override
	public int hashCode() {
		return Objects.hash(index);
	}
}