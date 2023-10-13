package de.lukaspanneke.masterthesis.logic;

import java.util.Map;
import java.util.Set;

public final class Constant implements ArithmeticExpression {

	private final int constant;

	private Constant(int constant) {
		this.constant = constant;
	}

	public static Constant of(int constant) {
		return new Constant(constant);
	}

	public int value() {
		return constant;
	}

	@Override
	public int evaluate(Map<Variable, Integer> assignment) {
		return this.constant;
	}

	@Override
	public void collectSupport(Set<Variable> accumulator) {
		// no-op
	}

	@Override
	public ArithmeticExpression substitute(Map<Variable, Variable> map) {
		return this;
	}

	@Override
	public String toString() {
		return Long.toString(this.constant);
	}
}
