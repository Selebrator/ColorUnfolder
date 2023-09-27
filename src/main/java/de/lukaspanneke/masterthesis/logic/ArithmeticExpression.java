package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public sealed interface ArithmeticExpression permits ArithmeticBoolean, Calculation, Constant, Variable {

	int evaluate(Map<Variable, Integer> assignment, Function<Stream<Variable>, Stream<Map<Variable, Integer>>> quantifierAssignments);

	void collectSupport(Set<Variable> accumulator);

	ArithmeticExpression substitute(Map<Variable, Variable> map);

	Term toCvc5(Solver solver, Function<Variable, Term> atoms);

	default Formula eq(ArithmeticExpression other) {
		return Equality.of(this, other);
	}

	default Formula eq(int other) {
		return this.eq(Constant.of(other));
	}

	default Formula lt(ArithmeticExpression other) {
		return Comparison.of(this, Comparison.Operator.LESS_THEN, other);
	}

	default Formula lt(int other) {
		return this.lt(Constant.of(other));
	}

	default Formula leq(ArithmeticExpression other) {
		return Comparison.of(this, Comparison.Operator.LESS_EQUALS, other);
	}

	default Formula leq(int other) {
		return this.leq(Constant.of(other));
	}

	default Formula gt(ArithmeticExpression other) {
		return Comparison.of(this, Comparison.Operator.GREATER_THEN, other);
	}

	default Formula gt(int other) {
		return this.gt(Constant.of(other));
	}

	default Formula geq(ArithmeticExpression other) {
		return Comparison.of(this, Comparison.Operator.GREATER_EQUALS, other);
	}

	default Formula geq(int other) {
		return this.geq(Constant.of(other));
	}

	default Formula neq(ArithmeticExpression other) {
		return Comparison.of(this, Comparison.Operator.NOT_EQUALS, other);
	}

	default Formula neq(int other) {
		return this.neq(Constant.of(other));
	}

	default ArithmeticExpression plus(ArithmeticExpression other) {
		return Calculation.of(this, Calculation.Operator.PLUS, other);
	}

	default ArithmeticExpression plus(int other) {
		return this.plus(Constant.of(other));
	}

	default ArithmeticExpression minus(ArithmeticExpression other) {
		return Calculation.of(this, Calculation.Operator.MINUS, other);
	}

	default ArithmeticExpression minus(int other) {
		return this.minus(Constant.of(other));
	}

	default ArithmeticExpression times(ArithmeticExpression other) {
		return Calculation.of(this, Calculation.Operator.TIMES, other);
	}

	default ArithmeticExpression times(int other) {
		return this.times(Constant.of(other));
	}
}
