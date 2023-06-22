package org.example.logic;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/* package-private */ final class Bottom<A> extends Formula<A> {

	private static final Bottom INSTANCE = new Bottom();

	private Bottom() {
	}

	public static <A> Bottom<A> instance() {
		return INSTANCE;
	}

	@Override
	protected void collectSupport(Set<A> accumulator) {
		// no-op
	}

	@Override
	public Formula<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map) {
		return this;
	}

	@Override
	public Term toCvc5(Solver solver, Function<A, Term> atoms) {
		return solver.mkFalse();
	}

	@Override
	public Formula<A> not() {
		return top();
	}

	@Override
	public Formula<A> and(Formula<A> rhs) {
		return this;
	}

	@Override
	public Formula<A> or(Formula<A> rhs) {
		return rhs;
	}

	@Override
	public Formula<A> implies(Formula<A> rhs) {
		return top();
	}

	@Override
	public String toString() {
		return "⊥";
	}
}