package org.example.logic;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/* package-private */ final class Constant<A> implements ArithmeticExpression<A> {

	private final long constant;

	private Constant(long constant) {
		this.constant = constant;
	}

	public static <V> Constant<V> of(long constant) {
		return new Constant<>(constant);
	}

	@Override
	public void collectSupport(Set<A> accumulator) {
		// no-op
	}

	@Override
	public ArithmeticExpression<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map) {
		return this;
	}

	@Override
	public Term toCvc5(Solver solver, Function<A, Term> atoms) {
		return solver.mkInteger(constant);
	}

	@Override
	public String toString() {
		return Long.toString(this.constant);
	}
}
