package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Kind;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/* package private */ final class Implication<A> extends Formula<A> {

	private final Formula<A> lhs;
	private final Formula<A> rhs;

	private Implication(Formula<A> lhs, Formula<A> rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public static <A> Implication<A> of(Formula<A> lhs, Formula<A> rhs) {
		return new Implication<>(lhs, rhs);
	}

	@Override
	protected void collectSupport(Set<A> accumulator) {
		lhs.collectSupport(accumulator);
		rhs.collectSupport(accumulator);
	}

	@Override
	public Formula<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map) {
		return lhs.substitute(map).implies(rhs.substitute(map));
	}

	@Override
	public Term toCvc5(Solver solver, Function<A, Term> atoms) {
		return solver.mkTerm(Kind.IMPLIES, lhs.toCvc5(solver, atoms), rhs.toCvc5(solver, atoms));
	}

	@Override
	public String toString() {
		return "(" + lhs + " ⇒ " + rhs + ")";
	}
}
