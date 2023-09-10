package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Kind;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/* package private */ final class Implication extends Formula {

	private final Formula lhs;
	private final Formula rhs;

	private Implication(Formula lhs, Formula rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public static Implication of(Formula lhs, Formula rhs) {
		return new Implication(lhs, rhs);
	}

	@Override
	public boolean evaluate(Map<Variable, Integer> assignment, Function<Stream<Variable>, Stream<Map<Variable, Integer>>> assignments) {
		return !this.lhs.evaluate(assignment, assignments) || this.rhs.evaluate(assignment, assignments);
	}

	@Override
	protected void collectSupport(Set<Variable> accumulator) {
		lhs.collectSupport(accumulator);
		rhs.collectSupport(accumulator);
	}

	@Override
	public Formula substitute(Map<Variable, Variable> map) {
		return lhs.substitute(map).implies(rhs.substitute(map));
	}

	@Override
	public Term toCvc5(Solver solver, Function<Variable, Term> atoms) {
		return solver.mkTerm(Kind.IMPLIES, lhs.toCvc5(solver, atoms), rhs.toCvc5(solver, atoms));
	}

	@Override
	public String toString() {
		return "(" + lhs + " ⇒ " + rhs + ")";
	}
}
