package org.example.logic.generic.expression;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class ConstantExpression<A> implements ArithmeticExpression<A> {
	private final long constant;

	protected ConstantExpression(long constant) {
		this.constant = constant;
	}

	public static <V> ConstantExpression<V> of(long constant) {
		return new ConstantExpression<>(constant);
	}

	public long constant() {
		return this.constant;
	}

	@Override
	public Set<A> support() {
		return Collections.emptySet();
	}

	@Override
	public ArithmeticExpression<A> local(String discriminator) {
		return this;
	}

	@Override
	public Term toCvc5(Solver solver, Map<A, Term> atoms) {
		return solver.mkInteger(constant);
	}

	@Override
	public String toString() {
		return Long.toString(this.constant);
	}
}
