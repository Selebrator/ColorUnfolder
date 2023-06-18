package org.example.logic.generic;

import io.github.cvc5.Kind;

import java.util.function.BiFunction;

public enum BinaryLogicOperator implements BiFunction<Boolean, Boolean, Boolean> {

	AND("∧", Kind.AND) {
		@Override
		public Boolean apply(Boolean left, Boolean right) {
			return left && right;
		}

	},
	OR("∨", Kind.OR) {
		@Override
		public Boolean apply(Boolean left, Boolean right) {
			return left || right;
		}

	};

	private final String symbol;
	private final Kind cvc5;

	BinaryLogicOperator(String symbol, Kind cvc5) {
		this.symbol = symbol;
		this.cvc5 = cvc5;
	}

	public String symbol() {
		return this.symbol;
	}

	public Kind toCvc5() {
		return cvc5;
	}
}
