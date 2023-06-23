package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public sealed interface ArithmeticExpression<A> permits Atom, Calculation, Constant {

	void collectSupport(Set<A> accumulator);

	ArithmeticExpression<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map);

	Term toCvc5(Solver solver, Function<A, Term> atoms);

	default Formula<A> eq(ArithmeticExpression<A> other) {
		return Equality.of(this, other);
	}

	default Formula<A> eq(long other) {
		return this.eq(Constant.of(other));
	}

	default Formula<A> lt(ArithmeticExpression<A> other) {
		return Comparison.of(this, Comparison.Operator.LESS_THEN, other);
	}

	default Formula<A> lt(long other) {
		return this.lt(Constant.of(other));
	}

	default Formula<A> leq(ArithmeticExpression<A> other) {
		return Comparison.of(this, Comparison.Operator.LESS_EQUALS, other);
	}

	default Formula<A> leq(long other) {
		return this.leq(Constant.of(other));
	}

	default Formula<A> gt(ArithmeticExpression<A> other) {
		return Comparison.of(this, Comparison.Operator.GREATER_THEN, other);
	}

	default Formula<A> gt(long other) {
		return this.gt(Constant.of(other));
	}

	default Formula<A> geq(ArithmeticExpression<A> other) {
		return Comparison.of(this, Comparison.Operator.GREATER_EQUALS, other);
	}

	default Formula<A> geq(long other) {
		return this.geq(Constant.of(other));
	}

	default Formula<A> neq(ArithmeticExpression<A> other) {
		return Comparison.of(this, Comparison.Operator.NOT_EQUALS, other);
	}

	default Formula<A> neq(long other) {
		return this.neq(Constant.of(other));
	}

	default ArithmeticExpression<A> plus(ArithmeticExpression<A> other) {
		return Calculation.of(this, Calculation.Operator.PLUS, other);
	}

	default ArithmeticExpression<A> plus(long other) {
		return this.plus(Constant.of(other));
	}

	default ArithmeticExpression<A> minus(ArithmeticExpression<A> other) {
		return Calculation.of(this, Calculation.Operator.MINUS, other);
	}

	default ArithmeticExpression<A> minus(long other) {
		return this.minus(Constant.of(other));
	}

	default ArithmeticExpression<A> times(ArithmeticExpression<A> other) {
		return Calculation.of(this, Calculation.Operator.TIMES, other);
	}

	default ArithmeticExpression<A> times(long other) {
		return this.times(Constant.of(other));
	}
}
