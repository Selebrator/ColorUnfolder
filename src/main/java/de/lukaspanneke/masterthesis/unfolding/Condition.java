package de.lukaspanneke.masterthesis.unfolding;

import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Place;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/* Place (p, e, pred) in B */
public record Condition(
		int index,
		String name,
		Place place,
		Event preset,
		Variable internalVariable,
		Set<Event> postset
) implements Comparable<Condition> {

	public Condition(int index, Place place, Event preset, Variable preVariable) {
		this(index, "b" + index, place, preset, preVariable, new HashSet<>());
	}

	public Set<Condition> prepre() {
		return this.preset().preset();
	}

	@Override
	public int compareTo(Condition that) {
		return Integer.compare(this.index, that.index);
	}

	@Override
	public String toString() {
		return name + "(" + place.name() + ")";
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
