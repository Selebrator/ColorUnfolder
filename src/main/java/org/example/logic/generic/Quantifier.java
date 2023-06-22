package org.example.logic.generic;

import io.github.cvc5.Kind;

public enum Quantifier {
	EXISTS("∃", Kind.EXISTS),
	FORALL("∀", Kind.FORALL);


	private final String symbol;
	private final Kind cvc5;

	Quantifier(String symbol, Kind cvc5) {
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
