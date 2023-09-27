package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Kind;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

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

	@Override
	public boolean evaluate(Map<Variable, Integer> assignment, Function<Stream<Variable>, Stream<Map<Variable, Integer>>> quantifierAssignments) {
		long e1 = this.e1.evaluate(assignment, quantifierAssignments);
		long e2 = this.e2.evaluate(assignment, quantifierAssignments);
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
	public Term toCvc5(Solver solver, Function<Variable, Term> atoms) {
		return solver.mkTerm(operator.toCvc5(), e1.toCvc5(solver, atoms), e2.toCvc5(solver, atoms));
	}

	@Override
	public String toString() {
		return this.e1.toString() + " " + this.operator.symbol() + " " + this.e2.toString();
	}

	public enum Operator {

		LESS_THEN("<", Kind.LT),
		LESS_EQUALS("≤", Kind.LEQ),
		NOT_EQUALS("≠", Kind.DISTINCT),
		GREATER_EQUALS("≥", Kind.GEQ),
		GREATER_THEN(">", Kind.GT);

		private final String symbol;
		private final Kind cvc5;

		Operator(String symbol, Kind cvc5) {
			this.symbol = symbol;
			this.cvc5 = cvc5;
		}

		public String symbol() {
			return this.symbol;
		}

		public Kind toCvc5() {
			return this.cvc5;
		}
	}
}
