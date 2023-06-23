package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/* package-private */ final class Negation<A> extends Formula<A> {

	private final Formula<A> f;

	private Negation(Formula<A> f) {
		this.f = f;
	}

	public static <A> Formula<A> of(Formula<A> f) {
		if (f instanceof Negation<A> n) {
			return n.f;
		} else {
			return new Negation<>(f);
		}
	}

	@Override
	protected void collectSupport(Set<A> accumulator) {
		this.f.collectSupport(accumulator);
	}

	@Override
	public Formula<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map) {
		return f.substitute(map).not();
	}

	@Override
	public Term toCvc5(Solver solver, Function<A, Term> atoms) {
		return f.toCvc5(solver, atoms).notTerm();
	}

	@Override
	public String toString() {
		if (this.f instanceof Comparison || this.f instanceof Equality) {
			return "¬(" + this.f + ")";
		} else {
			return "¬" + this.f;
		}
	}
}
