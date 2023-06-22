package org.example.components;

import java.util.*;

/* Place (p, e, pred) in B */
public record Condition(int index, String name, Place place, Optional<Event> preset, Variable preVariable) {
	public Condition(int index, Place place, Optional<Event> preset, Variable preVariable) {
		this(index, "b" + index, place, preset, preVariable);
	}

	@Override
	public String toString() {
		return name + "(" + place.name() + ")";
	}

	public Set<Condition> prepre() {
		return this.preset.map(Event::preset).map(Map::keySet).orElseGet(Collections::emptySet);
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
