package org.example.components;


import java.util.*;

/* Place (p, e, pred) in B */
public record Condition(int index, String name, Place place, Optional<Event> preset, Set<Event> postset, Predicate predicate) {
	public Condition(int index, Place place, Optional<Event> preset, Predicate predicate) {
		this(index, "b" + index, place, preset, new HashSet<>(), predicate);
	}

	@Override
	public String toString() {
		return name + "(" + place.name() + ")";
	}

	public Set<Condition> prepre() {
		return this.preset.map(Event::preset).orElseGet(Collections::emptySet);
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
