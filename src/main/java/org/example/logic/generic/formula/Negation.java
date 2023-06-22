package org.example.logic.generic.formula;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;
import org.example.logic.generic.expression.Atom;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/* package-private */ class Negation<A> extends StateFormula<A> {
	private final StateFormula<A> f;

	private Negation(StateFormula<A> f) {
		this.f = f;
	}

	protected static <A> StateFormula<A> of(StateFormula<A> f) {
		if (f instanceof Negation<A> n) {
			return n.f;
		} else {
			return new Negation<>(f);
		}
	}

	public StateFormula<A> formula() {
		return this.f;
	}

	@Override
	protected void collectSupport(Set<A> accumulator) {
		this.f.collectSupport(accumulator);
	}

	@Override
	public StateFormula<A> local(String discriminator) {
		return new Negation<>(f.local(discriminator));
	}

	@Override
	public StateFormula<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map) {
		return new Negation<>(f.substitute(map));
	}

	@Override
	public Term toCvc5(Solver solver, Function<A, Term> atoms) {
		return f.toCvc5(solver, atoms).notTerm();
	}

	@Override
	public String toString() {
		if (this.f instanceof ComparisonFormula) {
			return "¬(" + this.f + ")";
		} else {
			return "¬" + this.f;
		}
	}
}
