package de.lukaspanneke.masterthesis.logic;

import java.util.Map;
import java.util.Set;

public non-sealed interface Atom<A> extends ArithmeticExpression<A> {

	A value();

	@Override
	default void collectSupport(Set<A> accumulator) {
		accumulator.add(this.value());
	}

	@Override
	default ArithmeticExpression<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map) {
		Atom<A> ans = map.get(this);
		return ans != null ? ans : this;
	}
}
