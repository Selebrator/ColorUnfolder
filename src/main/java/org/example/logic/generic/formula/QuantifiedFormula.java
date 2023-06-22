package org.example.logic.generic.formula;

import io.github.cvc5.Kind;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;
import org.example.logic.generic.Quantifier;
import org.example.logic.generic.expression.Atom;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class QuantifiedFormula<A> extends StateFormula<A> {

	private final Quantifier quantifier;
	private final Set<? extends Atom<A>> variables;
	private final StateFormula<A> f;

	private QuantifiedFormula(Quantifier quantifier, Set<? extends Atom<A>> variables, StateFormula<A> f) {
		this.quantifier = quantifier;
		this.variables = variables;
		this.f = f;
	}

	public static <A> StateFormula<A> of(Quantifier quantifier, Set<? extends Atom<A>> variables, StateFormula<A> f) {
		return new QuantifiedFormula<>(quantifier, variables, f);
	}

	@Override
	protected void collectSupport(Set<A> accumulator) {
		// TODO
	}

	@Override
	public StateFormula<A> local(String discriminator) {
		return null; // TODO
	}

	@Override
	public StateFormula<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map) {
		return null; // TODO
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
		return this.quantifier.symbol() + " " + this.variables + ": " + this.f;
	}
}
