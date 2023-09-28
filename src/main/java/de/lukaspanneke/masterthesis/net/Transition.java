package de.lukaspanneke.masterthesis.net;

import de.lukaspanneke.masterthesis.logic.Formula;
import de.lukaspanneke.masterthesis.logic.Variable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Transition(
		int index,
		String name,
		Map<Place, Variable> preSet,
		Map<Place, Variable> postSet,
		Formula guard,
		int orderId
) implements Comparable<Transition> {

	public Transition(int index) {
		this(index, "t" + index);
	}

	public Transition(int index, Formula guard) {
		this(index, "t" + index, guard);
	}

	public Transition(int index, String name) {
		this(index, name, Formula.top());
	}

	public Transition(int index, String name, Formula guard) {
		this(index, name, new HashMap<>(), new HashMap<>(), guard, index);
	}

	public Transition(int index, String name, Map<Place, Variable> preSet, Map<Place, Variable> postSet, Formula guard) {
		this(index, name, preSet, postSet, guard, index);
	}

	public void validate() {
		Set<Variable> support = guard.support();
		Set<Variable> adjacent = Stream.concat(preSet.values().stream(), postSet.values().stream()).collect(Collectors.toSet());
		support.removeAll(adjacent);
		if (!support.isEmpty()) {
			throw new IllegalArgumentException("guard uses undeclared variable: " + support);
		}
	}

	@Override
	public int compareTo(Transition that) {
		return Integer.compare(this.orderId, that.orderId);
	}

	@Override
	public String toString() {
		return name;
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
		return index;
	}
}
