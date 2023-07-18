package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Kind;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class QuantifiedFormula extends Formula {

	private final Quantifier quantifier;
	private final Set<Variable> variables;
	private final Formula f;

	private QuantifiedFormula(Quantifier quantifier, Set<Variable> variables, Formula f) {
		this.quantifier = quantifier;
		this.variables = variables;
		this.f = f;
	}

	public static Formula of(Quantifier quantifier, Set<Variable> variables, Formula f) {
		return new QuantifiedFormula(quantifier, variables, f);
	}

	@Override
	protected void collectSupport(Set<Variable> accumulator) {
		throw new UnsupportedOperationException();  // TODO implement if needed
	}

	@Override
	public Formula substitute(Map<Variable, Variable> map) {
		throw new UnsupportedOperationException();  // TODO implement if needed
	}

	@Override
	public Term toCvc5(Solver solver, Function<Variable, Term> atoms) {
		try {
			Map<Variable, Term> freeAtoms = new HashMap<>();
			Function<Variable, Term> newAtoms = atom -> variables.contains(atom)
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
