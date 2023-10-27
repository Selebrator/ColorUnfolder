package de.lukaspanneke.masterthesis.logic;

import java.util.Map;
import java.util.Set;

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

	public ArithmeticExpression lhs() {
		return this.e1;
	}

	public ArithmeticExpression rhs() {
		return this.e2;
	}

	public Operator operator() {
		return this.operator;
	}

	@Override
	public int evaluate(Map<Variable, Integer> assignment) {
		int e1 = this.e1.evaluate(assignment);
		int e2 = this.e2.evaluate(assignment);
		return switch (this.operator) {
			case PLUS -> e1 + e2;
			case MINUS -> e1 - e2;
			case TIMES -> e1 * e2;
			case INT_DIV -> e1 / e2;
			case MOD -> e1 % e2;
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
	public String toString() {
		return "(" + this.e1.toString() + " " + this.operator.symbol() + " " + this.e2.toString() + ")";
	}

	public enum Operator {

		PLUS("+"),
		MINUS("-"),
		TIMES("*"),
		INT_DIV("//"),
		MOD(" mod ");

		private final String symbol;

		Operator(String symbol) {
			this.symbol = symbol;
		}

		public String symbol() {
			return this.symbol;
		}
	}
}
