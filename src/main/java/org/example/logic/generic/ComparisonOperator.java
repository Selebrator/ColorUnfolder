package org.example.logic.generic;

import io.github.cvc5.Kind;

import java.util.function.BiPredicate;

public enum ComparisonOperator implements BiPredicate<Long, Long> {
	LESS_THEN("<", Kind.LT, true) {
		@Override
		public boolean test(Long x, Long y) {
			return x < y;
		}
	},
	LESS_EQUALS("<=", Kind.LEQ, true) {
		@Override
		public boolean test(Long x, Long y) {
			return x <= y;
		}
	},
	EQUALS("==", Kind.EQUAL, false) {
		@Override
		public boolean test(Long x, Long y) {
			return x.equals(y);
		}
	},
	NOT_EQUALS("!=", Kind.DISTINCT, false) {
		@Override
		public boolean test(Long x, Long y) {
			return !x.equals(y);
		}
	},
	GREATER_EQUALS(">=", Kind.GEQ, true) {
		@Override
		public boolean test(Long x, Long y) {
			return x >= y;
		}
	},
	GREATER_THEN(">", Kind.GT, true) {
		@Override
		public boolean test(Long x, Long y) {
			return x > y;
		}
	};

	private final String symbol;
	private final Kind cvc5;
	private final boolean inequality;

	ComparisonOperator(String symbol, Kind cvc5, boolean inequality) {
		this.symbol = symbol;
		this.cvc5 = cvc5;
		this.inequality = inequality;
	}

	public String symbol() {
		return this.symbol;
	}

	public Kind toCvc5() {
		return this.cvc5;
	}

	public boolean isInequality() {
		return this.inequality;
	}
}
