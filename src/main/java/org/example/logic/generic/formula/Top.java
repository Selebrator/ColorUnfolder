package org.example.logic.generic.formula;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;
import org.example.logic.generic.expression.Atom;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/* package-private */ class Top<A> extends StateFormula<A> {

	private static final Top INSTANCE = new Top();

	private Top() {
	}

	public static <A> Top<A> instance() {
		return INSTANCE;
	}

	@Override
	protected void collectSupport(Set<A> accumulator) {
		// no-op
	}

	@Override
	public StateFormula<A> local(String discriminator) {
		return this;
	}

	@Override
	public StateFormula<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map) {
		return this;
	}

	@Override
	public Term toCvc5(Solver solver, Function<A, Term> atoms) {
		return solver.mkTrue();
	}

	@Override
	public StateFormula<A> not() {
		return bottom();
	}

	@Override
	public StateFormula<A> and(StateFormula<A> rhs) {
		return rhs;
	}

	@Override
	public StateFormula<A> or(StateFormula<A> rhs) {
		return this;
	}

	@Override
	public StateFormula<A> implies(StateFormula<A> rhs) {
		return rhs;
	}

	@Override
	public String toString() {
		return "⊤";
	}
}