package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Kind;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/* package-private */ final class Calculation implements ArithmeticExpression {

	private final ArithmeticExpression e1;
	private final ArithmeticExpression e2;
	private final Operator operator;

	private Calculation(ArithmeticExpression e1, Operator operator, ArithmeticExpression e2) {
		this.e1 = e1;
		this.e2 = e2;
		this.operator = operator;
	}

	public static Calculation of(ArithmeticExpression e1, Operator operator, ArithmeticExpression e2) {
		return new Calculation(e1, operator, e2);
	}

	@Override
	public int evaluate(Map<Variable, Integer> assignment) {
		int e1 = this.e1.evaluate(assignment);
		int e2 = this.e2.evaluate(assignment);
		return switch (this.operator) {
			case PLUS -> e1 + e2;
			case MINUS -> e1 - e2;
			case TIMES -> e1 * e2;
		};
	}

	@Override
	public void collectSupport(Set<Variable> accumulator) {
		this.e1.collectSupport(accumulator);
		this.e2.collectSupport(accumulator);
	}

	@Override
	public ArithmeticExpression substitute(Map<Variable, Variable> map) {
		return new Calculation(e1.substitute(map), operator, e2.substitute(map));
	}

	@Override
	public Term toCvc5(Solver solver, Function<Variable, Term> atoms) {
		return solver.mkTerm(operator.toCvc5(), e1.toCvc5(solver, atoms), e2.toCvc5(solver, atoms));
	}

	@Override
	public String toString() {
		return "(" + this.e1.toString() + " " + this.operator.symbol() + " " + this.e2.toString() + ")";
	}

	public enum Operator {

		PLUS("+", Kind.ADD),
		MINUS("-", Kind.SUB),
		TIMES("*", Kind.MULT);

		private final String symbol;
		private final Kind cvc5;

		Operator(String symbol, Kind cvc5) {
			this.symbol = symbol;
			this.cvc5 = cvc5;
		}

		public Kind toCvc5() {
			return this.cvc5;
		}

		public String symbol() {
			return this.symbol;
		}
	}
}
