package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public sealed interface ArithmeticExpression permits Variable, Calculation, Constant {

	void collectSupport(Set<Variable> accumulator);

	ArithmeticExpression substitute(Map<Variable, Variable> map);

	Term toCvc5(Solver solver, Function<Variable, Term> atoms);

	default Formula eq(ArithmeticExpression other) {
		return Equality.of(this, other);
	}

	default Formula eq(long other) {
		return this.eq(Constant.of(other));
	}

	default Formula lt(ArithmeticExpression other) {
		return Comparison.of(this, Comparison.Operator.LESS_THEN, other);
	}

	default Formula lt(long other) {
		return this.lt(Constant.of(other));
	}

	default Formula leq(ArithmeticExpression other) {
		return Comparison.of(this, Comparison.Operator.LESS_EQUALS, other);
	}

	default Formula leq(long other) {
		return this.leq(Constant.of(other));
	}

	default Formula gt(ArithmeticExpression other) {
		return Comparison.of(this, Comparison.Operator.GREATER_THEN, other);
	}

	default Formula gt(long other) {
		return this.gt(Constant.of(other));
	}

	default Formula geq(ArithmeticExpression other) {
		return Comparison.of(this, Comparison.Operator.GREATER_EQUALS, other);
	}

	default Formula geq(long other) {
		return this.geq(Constant.of(other));
	}

	default Formula neq(ArithmeticExpression other) {
		return Comparison.of(this, Comparison.Operator.NOT_EQUALS, other);
	}

	default Formula neq(long other) {
		return this.neq(Constant.of(other));
	}

	default ArithmeticExpression plus(ArithmeticExpression other) {
		return Calculation.of(this, Calculation.Operator.PLUS, other);
	}

	default ArithmeticExpression plus(long other) {
		return this.plus(Constant.of(other));
	}

	default ArithmeticExpression minus(ArithmeticExpression other) {
		return Calculation.of(this, Calculation.Operator.MINUS, other);
	}

	default ArithmeticExpression minus(long other) {
		return this.minus(Constant.of(other));
	}

	default ArithmeticExpression times(ArithmeticExpression other) {
		return Calculation.of(this, Calculation.Operator.TIMES, other);
	}

	default ArithmeticExpression times(long other) {
		return this.times(Constant.of(other));
	}
}
