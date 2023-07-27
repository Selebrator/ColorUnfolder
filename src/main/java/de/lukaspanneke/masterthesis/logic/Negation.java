package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/* package-private */ final class Negation extends Formula {

	private final Formula f;

	private Negation(Formula f) {
		this.f = f;
	}

	public static Formula of(Formula f) {
		if (f instanceof Negation n) {
			return n.f;
		} else {
			return new Negation(f);
		}
	}

	@Override
	public boolean evaluate(Map<Variable, Integer> assignment) {
		return !this.f.evaluate(assignment);
	}

	@Override
	protected void collectSupport(Set<Variable> accumulator) {
		this.f.collectSupport(accumulator);
	}

	@Override
	public Formula substitute(Map<Variable, Variable> map) {
		return f.substitute(map).not();
	}

	@Override
	public Term toCvc5(Solver solver, Function<Variable, Term> atoms) {
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
