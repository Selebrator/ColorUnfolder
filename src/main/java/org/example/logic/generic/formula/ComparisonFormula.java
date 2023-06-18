package org.example.logic.generic.formula;

import com.google.common.collect.Sets;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;
import org.example.logic.generic.ComparisonOperator;
import org.example.logic.generic.expression.ArithmeticExpression;

import java.util.Map;
import java.util.Set;

public class ComparisonFormula<A> extends StateFormula<A> {

	private final ArithmeticExpression<A> e1;
	private final ArithmeticExpression<A> e2;
	private final ComparisonOperator operator;

	protected ComparisonFormula(ArithmeticExpression<A> e1, ComparisonOperator operator, ArithmeticExpression<A> e2) {
		this.e1 = e1;
		this.e2 = e2;
		this.operator = operator;
	}

	public static <A> ComparisonFormula<A> of(ArithmeticExpression<A> e1, ComparisonOperator operator, ArithmeticExpression<A> e2) {
		return new ComparisonFormula<>(e1, operator, e2);
	}

	public ArithmeticExpression<A> left() {
		return this.e1;
	}

	public ArithmeticExpression<A> right() {
		return this.e2;
	}

	public ComparisonOperator operator() {
		return this.operator;
	}

	@Override
	public Set<A> support() {
		return Sets.union(this.e1.support(), this.e2.support());
	}

	@Override
	public StateFormula<A> local(String discriminator) {
		return new ComparisonFormula<>(e1.local(discriminator), operator, e2.local(discriminator));
	}

	@Override
	public Term toCvc5(Solver solver, Map<A, Term> atoms) {
		return solver.mkTerm(operator.toCvc5(), e1.toCvc5(solver, atoms), e2.toCvc5(solver, atoms));
	}

	@Override
	public String toString() {
		return this.e1.toString() + " " + this.operator.symbol() + " " + this.e2.toString();
	}
}
