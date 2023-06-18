package org.example.logic.generic.expression;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;

public interface ArithmeticExpression<A> {

	Set<A> support();

	ArithmeticExpression<A> local(String discriminator);

	Term toCvc5(Solver solver, Map<A, Term> atoms);
}
