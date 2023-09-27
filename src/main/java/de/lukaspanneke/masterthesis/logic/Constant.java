package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Constant implements ArithmeticExpression {

	private final int constant;

	private Constant(int constant) {
		this.constant = constant;
	}

	public static Constant of(int constant) {
		return new Constant(constant);
	}

	public int value() {
		return constant;
	}

	@Override
	public int evaluate(Map<Variable, Integer> assignment, Function<Stream<Variable>, Stream<Map<Variable, Integer>>> quantifierAssignments) {
		return this.constant;
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
