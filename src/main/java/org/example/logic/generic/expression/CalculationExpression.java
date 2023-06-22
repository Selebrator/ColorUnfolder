package org.example.logic.generic.expression;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;
import org.example.logic.generic.CalculationOperator;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class CalculationExpression<A> implements ArithmeticExpression<A> {
	private final ArithmeticExpression<A> e1;
	private final ArithmeticExpression<A> e2;
	private final CalculationOperator operator;

	protected CalculationExpression(ArithmeticExpression<A> e1, CalculationOperator operator, ArithmeticExpression<A> e2) {
		this.e1 = e1;
		this.e2 = e2;
		this.operator = operator;
	}

	public static <A> CalculationExpression<A> of(ArithmeticExpression<A> e1, CalculationOperator operator, ArithmeticExpression<A> e2) {
		return new CalculationExpression<>(e1, operator, e2);
	}

	public ArithmeticExpression<A> left() {
		return this.e1;
	}

	public ArithmeticExpression<A> right() {
		return this.e2;
	}

	public CalculationOperator operator() {
		return this.operator;
	}

	@Override
	public void collectSupport(Set<A> accumulator) {
		this.e1.collectSupport(accumulator);
		this.e2.collectSupport(accumulator);
	}

	@Override
	public ArithmeticExpression<A> local(String discriminator) {
		return new CalculationExpression<>(e1.local(discriminator), operator, e2.local(discriminator));
	}

	@Override
	public ArithmeticExpression<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map) {
		return new CalculationExpression<>(e1.substitute(map), operator, e2.substitute(map));
	}

	@Override
	public Term toCvc5(Solver solver, Function<A, Term> atoms) {
		return solver.mkTerm(operator.toCvc5(), e1.toCvc5(solver, atoms), e2.toCvc5(solver, atoms));
	}

	@Override
	public String toString() {
		return "(" + this.e1.toString() + " " + this.operator.symbol() + " " + this.e2.toString() + ")";
	}
}
