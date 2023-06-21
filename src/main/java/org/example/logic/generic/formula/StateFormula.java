package org.example.logic.generic.formula;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;
import org.example.logic.generic.BinaryLogicOperator;
import org.example.logic.generic.ComparisonOperator;
import org.example.logic.generic.expression.Atom;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public abstract class StateFormula<A> {

	public final Set<A> support() {
		Set<A> ans = new HashSet<>();
		collectSupport(ans);
		return Collections.unmodifiableSet(ans);
	}

	protected abstract void collectSupport(Set<A> accumulator);

	public abstract StateFormula<A> local(String discriminator);

	public abstract StateFormula<A> substitute(Map<Atom<A>, Atom<A>> map);

	public abstract Term toCvc5(Solver solver, Function<A, Term> atoms);

	private static final Top TOP = new Top();

	public static <A> StateFormula<A> top() {
		return TOP;
	}

	private static final class Top<A> extends StateFormula<A> {

		@Override
		protected void collectSupport(Set<A> accumulator) {
			// no-op
		}

		@Override
		public StateFormula<A> local(String discriminator) {
			return this;
		}

		@Override
		public StateFormula<A> substitute(Map<Atom<A>, Atom<A>> map) {
			return this;
		}

		@Override
		public Term toCvc5(Solver solver, Function<A, Term> atoms) {
			return solver.mkTrue();
		}

		@Override
		public StateFormula<A> not() {
			return bottom();
		}

		@Override
		public StateFormula<A> and(StateFormula<A> rhs) {
			return rhs;
		}

		@Override
		public StateFormula<A> or(StateFormula<A> rhs) {
			return this;
		}

		@Override
		public String toString() {
			return "⊤";
		}
	}

	private static final Bottom BOTTOM = new Bottom();

	public static <A> StateFormula<A> bottom() {
		return BOTTOM;
	}

	public static final class Bottom<A> extends StateFormula<A> {

		@Override
		protected void collectSupport(Set<A> accumulator) {
			// no-op
		}

		@Override
		public StateFormula<A> local(String discriminator) {
			return this;
		}

		@Override
		public StateFormula<A> substitute(Map<Atom<A>, Atom<A>> map) {
			return this;
		}

		@Override
		public Term toCvc5(Solver solver, Function<A, Term> atoms) {
			return solver.mkFalse();
		}

		@Override
		public StateFormula<A> not() {
			return top();
		}

		@Override
		public StateFormula<A> and(StateFormula<A> rhs) {
			return this;
		}

		@Override
		public StateFormula<A> or(StateFormula<A> rhs) {
			return rhs;
		}

		@Override
		public String toString() {
			return "⊥";
		}
	}

	public StateFormula<A> not() {
		return NegatedFormula.of(this);
	}

	public StateFormula<A> and(StateFormula<A> rhs) {
		if (rhs == TOP) {
			return this;
		} else if (rhs == BOTTOM) {
			return rhs;
		}
		return CompositionFormula.of(this, BinaryLogicOperator.AND, rhs);
	}

	public StateFormula<A> or(StateFormula<A> rhs) {
		if (rhs == TOP) {
			return rhs;
		} else if (rhs == BOTTOM) {
			return this;
		}
		return CompositionFormula.of(this, BinaryLogicOperator.OR, rhs);
	}

	public static <A> StateFormula<A> allEquals(List<Atom<A>> atoms) {
		if (atoms.size() <= 1) {
			return top();
		}
		List<ComparisonFormula<A>> comparisons = IntStream.range(1, atoms.size())
				.mapToObj(i -> ComparisonFormula.of(atoms.get(i - 1), ComparisonOperator.EQUALS, atoms.get(i)))
				.toList();
		if (comparisons.size() == 1) {
			return comparisons.get(0);
		}
		return new CompositionFormula<>(BinaryLogicOperator.AND, comparisons);
	}
}
