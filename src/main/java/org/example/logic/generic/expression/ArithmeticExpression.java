package org.example.logic.generic.expression;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public interface ArithmeticExpression<A> {

	default Set<A> support() {
		Set<A> ans = new HashSet<>();
		collectSupport(ans);
		return ans;
	}

	void collectSupport(Set<A> accumulator);

	ArithmeticExpression<A> local(String discriminator);

	ArithmeticExpression<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map);

	Term toCvc5(Solver solver, Function<A, Term> atoms);
}
