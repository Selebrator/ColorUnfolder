package de.lukaspanneke.masterthesis.logic;

import java.util.Map;
import java.util.Set;

/* package-private */ final class Comparison extends Formula {

	private final ArithmeticExpression e1;
	private final ArithmeticExpression e2;
	private final Operator operator;

	private Comparison(ArithmeticExpression e1, Operator operator, ArithmeticExpression e2) {
		this.e1 = e1;
		this.e2 = e2;
		this.operator = operator;
	}

	public static Comparison of(ArithmeticExpression e1, Operator operator, ArithmeticExpression e2) {
		return new Comparison(e1, operator, e2);
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
	public boolean evaluate(Map<Variable, Integer> assignment) {
		long e1 = this.e1.evaluate(assignment);
		long e2 = this.e2.evaluate(assignment);
		return switch (this.operator) {
			case LESS_THEN -> e1 < e2;
			case LESS_EQUALS -> e1 <= e2;
			case NOT_EQUALS -> e1 != e2;
			case GREATER_EQUALS -> e1 >= e2;
			case GREATER_THEN -> e1 > e2;
		};
	}

	@Override
	protected void collectSupport(Set<Variable> accumulator) {
		this.e1.collectSupport(accumulator);
		this.e2.collectSupport(accumulator);
	}

	@Override
	public Formula substitute(Map<Variable, Variable> map) {
		return new Comparison(e1.substitute(map), operator, e2.substitute(map));
	}

	@Override
	public String toString() {
		return this.e1.toString() + " " + this.operator.symbol() + " " + this.e2.toString();
	}

	public enum Operator {

		LESS_THEN("<"),
		LESS_EQUALS("≤"),
		NOT_EQUALS("≠"),
		GREATER_EQUALS("≥"),
		GREATER_THEN(">");

		private final String symbol;

		Operator(String symbol) {
			this.symbol = symbol;
		}

		public String symbol() {
			return this.symbol;
		}
	}
}
