package org.example.components;

import org.example.logic.Formula;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record Transition(int index, String name, Map<Place, Variable> preSet, Map<Place, Variable> postSet,
						 Formula<Variable> guard) {

	public Transition(int index) {
		this(index, "t" + index);
	}

	public Transition(int index, Formula<Variable> guard) {
		this(index, "t" + index, guard);
	}

	public Transition(int index, String name) {
		this(index, name, Formula.top());
	}

	public Transition(int index, String name, Formula<Variable> guard) {
		this(index, name, new HashMap<>(), new HashMap<>(), guard);
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
		return Objects.hash(index);
	}
}
