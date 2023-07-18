package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/* package-private */ final class Constant implements ArithmeticExpression {

	private final long constant;

	private Constant(long constant) {
		this.constant = constant;
	}

	public static Constant of(long constant) {
		return new Constant(constant);
	}

	@Override
	public void collectSupport(Set<Variable> accumulator) {
		// no-op
	}

	@Override
	public ArithmeticExpression substitute(Map<Variable, Variable> map) {
		return this;
	}

	@Override
	public Term toCvc5(Solver solver, Function<Variable, Term> atoms) {
		return solver.mkInteger(constant);
	}

	@Override
	public String toString() {
		return Long.toString(this.constant);
	}
}
