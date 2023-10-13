package de.lukaspanneke.masterthesis.logic;

import java.util.*;
import java.util.stream.Collectors;

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

	public Set<ArithmeticExpression> terms() {
		return this.terms;
	}

	@Override
	public boolean evaluate(Map<Variable, Integer> assignment) {
		return terms.stream()
				.map(term -> term.evaluate(assignment))
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
	public String toString() {
		return terms.stream()
				.map(Object::toString)
				.collect(Collectors.joining(" = "));
	}
}
