package org.example.logic.generic.expression;

import java.util.Map;
import java.util.Set;

public interface Atom<A> extends ArithmeticExpression<A> {
	A value();

	@Override
	default void collectSupport(Set<A> accumulator) {
		accumulator.add(this.value());
	}

	@Override
	default ArithmeticExpression<A> substitute(Map<Atom<A>, Atom<A>> map) {
		return map.getOrDefault(this, this);
	}
}
