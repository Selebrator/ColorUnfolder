package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/* package-private */ final class Bottom extends Formula {

	private static final Bottom INSTANCE = new Bottom();

	private Bottom() {
	}

	public static Bottom instance() {
		return INSTANCE;
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
	public Term toCvc5(Solver solver, Function<Variable, Term> atoms) {
		return solver.mkFalse();
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