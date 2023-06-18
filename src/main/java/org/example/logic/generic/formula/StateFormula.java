package org.example.logic.generic.formula;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;
import org.example.logic.generic.BinaryLogicOperator;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public abstract class StateFormula<A> {

	public abstract Set<A> support();

	public abstract StateFormula<A> local(String discriminator);

	public abstract Term toCvc5(Solver solver, Map<A, Term> atoms);

	private static final Top TOP = new Top();

	public static <A> StateFormula<A> top() {
		return TOP;
	}

	private static final class Top<A> extends StateFormula<A> {

		@Override
		public Set<A> support() {
			return Collections.emptySet();
		}

		@Override
		public StateFormula<A> local(String discriminator) {
			return this;
		}

		@Override
		public Term toCvc5(Solver solver, Map<A, Term> atoms) {
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
		public Set<A> support() {
			return Collections.emptySet();
		}

		@Override
		public StateFormula<A> local(String discriminator) {
			return this;
		}

		@Override
		public Term toCvc5(Solver solver, Map<A, Term> atoms) {
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
}
