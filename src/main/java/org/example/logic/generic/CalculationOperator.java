package org.example.logic.generic;

import io.github.cvc5.Kind;

import java.util.function.BiFunction;

public enum CalculationOperator implements BiFunction<Long, Long, Long> {
	PLUS("+", Kind.ADD) {
		@Override
		public Long apply(Long left, Long right) {
			return left + right;
		}
	},
	MINUS("-", Kind.SUB) {
		@Override
		public Long apply(Long left, Long right) {
			return left - right;
		}
	},
	TIMES("*", Kind.MULT) {
		@Override
		public Long apply(Long left, Long right) {
			return left * right;
		}
	};

	private final String symbol;
	private final Kind cvc5;

	CalculationOperator(String symbol, Kind cvc5) {
		this.symbol = symbol;
		this.cvc5 = cvc5;
	}

	public Kind toCvc5() {
		return this.cvc5;
	}

	public String symbol() {
		return this.symbol;

	}
}
