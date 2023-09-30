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

public sealed abstract class Formula permits AndOr, Bottom, Comparison, Equality, Implication, Negation, QuantifiedFormula, Top {

	public abstract boolean evaluate(Map<Variable, Integer> assignment);

	public final Set<Variable> support() {
		Set<Variable> ans = new HashSet<>();
		collectSupport(ans);
		return ans;
	}

	protected abstract void collectSupport(Set<Variable> accumulator);

	public abstract Formula substitute(Map<Variable, Variable> map);

	public abstract Term toCvc5(Solver solver, Function<Variable, Term> atoms);

	public static Formula top() {
		return Top.instance();
	}

	public static Formula bottom() {
		return Bottom.instance();
	}

	public ArithmeticExpression asArithmetic() {
		return ArithmeticBoolean.of(this);
	}

	public Formula not() {
		return Negation.of(this);
	}

	public Formula and(Formula rhs) {
		if (rhs == top()) {
			return this;
		} else if (rhs == bottom()) {
			return rhs;
		}
		return AndOr.of(this, AndOr.Operator.AND, rhs);
	}

	public static Formula and(List<? extends Formula> formulas) {
		return AndOr.of(AndOr.Operator.AND, formulas);
	}

	public static Collector<Formula, ?, Formula> and() {
		return Collectors.collectingAndThen(Collectors.toList(), Formula::and);
	}

	public Formula or(Formula rhs) {
		if (rhs == top()) {
			return rhs;
		} else if (rhs == bottom()) {
			return this;
		}
		return AndOr.of(this, AndOr.Operator.OR, rhs);
	}

	public static Formula or(List<? extends Formula> formulas) {
		return AndOr.of(AndOr.Operator.OR, formulas);
	}

	public static Collector<Formula, ?, Formula> or() {
		return Collectors.collectingAndThen(Collectors.toList(), Formula::or);
	}

	public Formula implies(Formula rhs) {
		if (rhs == top()) {
			return rhs;
		}
		return Implication.of(this, rhs);
	}

	public static Formula eq(List<? extends ArithmeticExpression> atoms) {
		return Equality.of(atoms);
	}

	public static Collector<ArithmeticExpression, ?, Formula> eq() {
		return Collectors.collectingAndThen(Collectors.toList(), Formula::eq);
	}
}
