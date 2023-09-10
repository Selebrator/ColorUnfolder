package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/* package-private */ final class Top extends Formula {

	private static final Top INSTANCE = new Top();

	private Top() {
	}

	public static Top instance() {
		return INSTANCE;
	}

	@Override
	public boolean evaluate(Map<Variable, Integer> assignment, Function<Stream<Variable>, Stream<Map<Variable, Integer>>> assignments) {
		return true;
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
		return solver.mkTrue();
	}

	@Override
	public Formula not() {
		return bottom();
	}

	@Override
	public Formula and(Formula rhs) {
		return rhs;
	}

	@Override
	public Formula or(Formula rhs) {
		return this;
	}

	@Override
	public Formula implies(Formula rhs) {
		return rhs;
	}

	@Override
	public String toString() {
		return "⊤";
	}
}