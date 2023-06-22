package org.example.logic;

import io.github.cvc5.Kind;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/* package-private */ final class Comparison<A> extends Formula<A> {

	private final ArithmeticExpression<A> e1;
	private final ArithmeticExpression<A> e2;
	private final Operator operator;

	private Comparison(ArithmeticExpression<A> e1, Operator operator, ArithmeticExpression<A> e2) {
		this.e1 = e1;
		this.e2 = e2;
		this.operator = operator;
	}

	public static <A> Comparison<A> of(ArithmeticExpression<A> e1, Operator operator, ArithmeticExpression<A> e2) {
		return new Comparison<>(e1, operator, e2);
	}

	@Override
	protected void collectSupport(Set<A> accumulator) {
		this.e1.collectSupport(accumulator);
		this.e2.collectSupport(accumulator);
	}

	@Override
	public Formula<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map) {
		return new Comparison<>(e1.substitute(map), operator, e2.substitute(map));
	}

	@Override
	public Term toCvc5(Solver solver, Function<A, Term> atoms) {
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
