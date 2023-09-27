package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Kind;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* package-private */ final class Equality extends Formula {

	private final Set<ArithmeticExpression> terms;

	private Equality(Set<ArithmeticExpression> terms) {
		this.terms = terms;
	}

	public static Formula of(ArithmeticExpression lhs, ArithmeticExpression rhs) {
		return Equality.of(List.of(lhs, rhs));
	}

	public static Formula of(Collection<? extends ArithmeticExpression> equalTerms) {
		Set<ArithmeticExpression> terms = new LinkedHashSet<>(equalTerms);
		if (terms.size() <= 1) {
			return top();
		}
		return new Equality(Collections.unmodifiableSet(terms));
	}

	@Override
	public boolean evaluate(Map<Variable, Integer> assignment, Function<Stream<Variable>, Stream<Map<Variable, Integer>>> quantifierAssignments) {
		return terms.stream()
				.map(term -> term.evaluate(assignment, quantifierAssignments))
				.collect(Collectors.toSet()).size() <= 1;
	}

	@Override
	protected void collectSupport(Set<Variable> accumulator) {
		for (ArithmeticExpression term : terms) {
			term.collectSupport(accumulator);
		}
	}

	@Override
	public Formula substitute(Map<Variable, Variable> map) {
		return terms.stream()
				.map(expr -> expr.substitute(map))
				.collect(Formula.eq());
	}

	@Override
	public Term toCvc5(Solver solver, Function<Variable, Term> atoms) {
		return solver.mkTerm(Kind.EQUAL, terms.stream()
				.map(expr -> expr.toCvc5(solver, atoms))
				.toArray(Term[]::new));
	}

	@Override
	public String toString() {
		return terms.stream()
				.map(Object::toString)
				.collect(Collectors.joining(" = "));
	}
}
