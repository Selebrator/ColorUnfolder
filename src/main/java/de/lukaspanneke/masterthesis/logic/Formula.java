package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public sealed abstract class Formula<A> permits AndOr, Bottom, Comparison, Equality, Implication, Negation, QuantifiedFormula, Top {

	public final Set<A> support() {
		Set<A> ans = new HashSet<>();
		collectSupport(ans);
		return ans;
	}

	protected abstract void collectSupport(Set<A> accumulator);

	public abstract Formula<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map);

	public abstract Term toCvc5(Solver solver, Function<A, Term> atoms);

	public static <A> Formula<A> top() {
		return Top.instance();
	}

	public static <A> Formula<A> bottom() {
		return Bottom.instance();
	}

	public Formula<A> not() {
		return Negation.of(this);
	}

	public Formula<A> and(Formula<A> rhs) {
		if (rhs == top()) {
			return this;
		} else if (rhs == bottom()) {
			return rhs;
		}
		return AndOr.of(this, AndOr.Operator.AND, rhs);
	}

	public static <A> Formula<A> and(List<? extends Formula<A>> formulas) {
		return AndOr.of(AndOr.Operator.AND, formulas);
	}

	public static <A> Collector<Formula<A>, ?, Formula<A>> and() {
		return Collectors.collectingAndThen(Collectors.toList(), Formula::and);
	}

	public Formula<A> or(Formula<A> rhs) {
		if (rhs == top()) {
			return rhs;
		} else if (rhs == bottom()) {
			return this;
		}
		return AndOr.of(this, AndOr.Operator.OR, rhs);
	}

	public static <A> Formula<A> or(List<? extends Formula<A>> formulas) {
		return AndOr.of(AndOr.Operator.OR, formulas);
	}

	public static <A> Collector<Formula<A>, ?, Formula<A>> or() {
		return Collectors.collectingAndThen(Collectors.toList(), Formula::or);
	}

	public Formula<A> implies(Formula<A> rhs) {
		if (rhs == top()) {
			return rhs;
		}
		return Implication.of(this, rhs);
	}

	public static <A> Formula<A> eq(List<? extends ArithmeticExpression<A>> atoms) {
		return Equality.of(atoms);
	}

	public static <A> Collector<ArithmeticExpression<A>, ?, Formula<A>> eq() {
		return Collectors.collectingAndThen(Collectors.toList(), Formula::eq);
	}
}
