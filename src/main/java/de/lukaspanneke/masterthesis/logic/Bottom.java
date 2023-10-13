package de.lukaspanneke.masterthesis.logic;

import java.util.Map;
import java.util.Set;

/* package-private */ final class Bottom extends Formula {

	private static final Bottom INSTANCE = new Bottom();

	private Bottom() {
	}

	public static Bottom instance() {
		return INSTANCE;
	}

	@Override
	public boolean evaluate(Map<Variable, Integer> assignment) {
		return false;
	}

	@Override
	protected void collectSupport(Set<Variable> accumulator) {
		// no-op
	}

	@Override
	public Formula substitute(Map<Variable, Variable> map) {
		return this;
	}

	@Override
	public Formula not() {
		return top();
	}

	@Override
	public Formula and(Formula rhs) {
		return this;
	}

	@Override
	public Formula or(Formula rhs) {
		return rhs;
	}

	@Override
	public Formula implies(Formula rhs) {
		return top();
	}

	@Override
	public String toString() {
		return "⊥";
	}
}