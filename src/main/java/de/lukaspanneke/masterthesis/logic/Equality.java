package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Kind;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/* package-private */ final class Equality<A> extends Formula<A> {

	private final Set<ArithmeticExpression<A>> terms;

	private Equality(Set<ArithmeticExpression<A>> terms) {
		this.terms = terms;
	}

	public static <A> Formula<A> of(ArithmeticExpression<A> lhs, ArithmeticExpression<A> rhs) {
		return Equality.of(List.of(lhs, rhs));
	}

	public static <A> Formula<A> of(Collection<? extends ArithmeticExpression<A>> equalTerms) {
		Set<ArithmeticExpression<A>> terms = new LinkedHashSet<>(equalTerms);
		if (terms.size() <= 1) {
			return top();
		}
		return new Equality<>(Collections.unmodifiableSet(terms));
	}

	@Override
	protected void collectSupport(Set<A> accumulator) {
		for (ArithmeticExpression<A> term : terms) {
			term.collectSupport(accumulator);
		}
	}

	@Override
	public Formula<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map) {
		return terms.stream()
				.map(expr -> expr.substitute(map))
				.collect(Formula.eq());
	}

	@Override
	public Term toCvc5(Solver solver, Function<A, Term> atoms) {
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
