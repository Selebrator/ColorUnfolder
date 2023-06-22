package org.example.logic;

import io.github.cvc5.Kind;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class QuantifiedFormula<A> extends Formula<A> {

	private final Quantifier quantifier;
	private final Set<? extends Atom<A>> variables;
	private final Formula<A> f;

	private QuantifiedFormula(Quantifier quantifier, Set<? extends Atom<A>> variables, Formula<A> f) {
		this.quantifier = quantifier;
		this.variables = variables;
		this.f = f;
	}

	public static <A> Formula<A> of(Quantifier quantifier, Set<? extends Atom<A>> variables, Formula<A> f) {
		return new QuantifiedFormula<>(quantifier, variables, f);
	}

	@Override
	protected void collectSupport(Set<A> accumulator) {
		throw new UnsupportedOperationException();  // TODO implement if needed
	}

	@Override
	public Formula<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map) {
		throw new UnsupportedOperationException();  // TODO implement if needed
	}

	@Override
	public Term toCvc5(Solver solver, Function<A, Term> atoms) {
		try {
			Map<A, Term> freeAtoms = new HashMap<>();
			Function<A, Term> newAtoms = atom -> variables.contains(atom)
					? freeAtoms.computeIfAbsent(atom, a -> solver.mkVar(solver.getIntegerSort(), a.toString()))
					: atoms.apply(atom);
			Term formula = f.toCvc5(solver, newAtoms);
			Term quantifiedVariables = solver.mkTerm(Kind.VARIABLE_LIST, variables.stream()
					.map(variable -> variable.toCvc5(solver, newAtoms))
					.toArray(Term[]::new));
			return solver.mkTerm(quantifier.toCvc5(), quantifiedVariables, formula);
		} catch (Exception e) {
			System.err.println("could not encode " + this);
			throw e;
		}
	}

	@Override
	public String toString() {
		return "(" + this.quantifier.symbol() + " " + this.variables + ": " + this.f + ")";
	}

	public enum Quantifier {

		EXISTS("∃", Kind.EXISTS),
		FORALL("∀", Kind.FORALL);

		private final String symbol;
		private final Kind cvc5;

		Quantifier(String symbol, Kind cvc5) {
			this.symbol = symbol;
			this.cvc5 = cvc5;
		}

		public Kind toCvc5() {
			return this.cvc5;
		}

		public String symbol() {
			return this.symbol;
		}
	}
}
