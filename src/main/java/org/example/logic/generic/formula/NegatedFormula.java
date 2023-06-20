package org.example.logic.generic.formula;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Set;
import java.util.function.Function;

public class NegatedFormula<A> extends StateFormula<A> {
	private final StateFormula<A> f;

	protected NegatedFormula(StateFormula<A> f) {
		this.f = f;
	}

	public static <A> StateFormula<A> of(StateFormula<A> f) {
		if (f instanceof NegatedFormula<A> n) {
			return n.f;
		} else {
			return new NegatedFormula<>(f);
		}
	}

	public StateFormula<A> formula() {
		return this.f;
	}

	@Override
	protected void collectSupport(Set<A> accumulator) {
		accumulator.addAll(this.f.support());
	}

	@Override
	public StateFormula<A> local(String discriminator) {
		return new NegatedFormula<>(f.local(discriminator));
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
