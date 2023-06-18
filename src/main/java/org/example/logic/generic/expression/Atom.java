package org.example.logic.generic.expression;

import java.util.Set;

public interface Atom<A> extends ArithmeticExpression<A> {
	A value();

	default Set<A> support() {
		return Set.of(this.value());
	}
}
