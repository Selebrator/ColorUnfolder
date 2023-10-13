package de.lukaspanneke.masterthesis.logic;

import java.util.Map;
import java.util.Set;

/* package private */ final class Implication extends Formula {

	private final Formula lhs;
	private final Formula rhs;

	private Implication(Formula lhs, Formula rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public static Implication of(Formula lhs, Formula rhs) {
		return new Implication(lhs, rhs);
	}

	public Formula lhs() {
		return this.lhs;
	}

	public Formula rhs() {
		return this.rhs;
	}

	@Override
	public boolean evaluate(Map<Variable, Integer> assignment) {
		return !this.lhs.evaluate(assignment) || this.rhs.evaluate(assignment);
	}

	@Override
	protected void collectSupport(Set<Variable> accumulator) {
		lhs.collectSupport(accumulator);
		rhs.collectSupport(accumulator);
	}

	@Override
	public Formula substitute(Map<Variable, Variable> map) {
		return lhs.substitute(map).implies(rhs.substitute(map));
	}

	@Override
	public String toString() {
		return "(" + lhs + " ⇒ " + rhs + ")";
	}
}
