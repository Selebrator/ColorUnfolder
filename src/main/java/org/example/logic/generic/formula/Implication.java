package org.example.logic.generic.formula;

import io.github.cvc5.Kind;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;
import org.example.logic.generic.expression.Atom;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/* package private */ class Implication<A> extends StateFormula<A> {

	private final StateFormula<A> lhs;
	private final StateFormula<A> rhs;

	protected Implication(StateFormula<A> lhs, StateFormula<A> rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	protected void collectSupport(Set<A> accumulator) {
		lhs.collectSupport(accumulator);
		rhs.collectSupport(accumulator);
	}

	@Override
	public StateFormula<A> local(String discriminator) {
		return new Implication<>(lhs.local(discriminator), rhs.local(discriminator));
	}

	@Override
	public StateFormula<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map) {
		return new Implication<>(lhs.substitute(map), rhs.substitute(map));
	}

	@Override
	public Term toCvc5(Solver solver, Function<A, Term> atoms) {
		return solver.mkTerm(Kind.IMPLIES, lhs.toCvc5(solver, atoms), rhs.toCvc5(solver, atoms));
	}
}
