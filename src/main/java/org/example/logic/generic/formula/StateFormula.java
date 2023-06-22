package org.example.logic.generic.formula;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;
import org.example.logic.generic.BinaryLogicOperator;
import org.example.logic.generic.ComparisonOperator;
import org.example.logic.generic.expression.Atom;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class StateFormula<A> {

	public final Set<A> support() {
		Set<A> ans = new HashSet<>();
		collectSupport(ans);
		return ans;
	}

	protected abstract void collectSupport(Set<A> accumulator);

	public abstract StateFormula<A> local(String discriminator);

	public abstract StateFormula<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map);

	public abstract Term toCvc5(Solver solver, Function<A, Term> atoms);

	public static <A> StateFormula<A> top() {
		return Top.instance();
	}

	public static <A> StateFormula<A> bottom() {
		return Bottom.instance();
	}

	public StateFormula<A> not() {
		return Negation.of(this);
	}

	public StateFormula<A> and(StateFormula<A> rhs) {
		if (rhs == top()) {
			return this;
		} else if (rhs == bottom()) {
			return rhs;
		}
		return CompositionFormula.of(this, BinaryLogicOperator.AND, rhs);
	}

	public static <A> StateFormula<A> and(List<? extends StateFormula<A>> formulas) {
		if (formulas.isEmpty()) {
			return top();
		}
		if (formulas.size() == 1) {
			return formulas.get(0);
		}
		return new CompositionFormula<>(BinaryLogicOperator.AND, formulas);
	}

	public static <A> Collector<StateFormula<A>, ?, StateFormula<A>> and() {
		return Collectors.collectingAndThen(Collectors.toList(), StateFormula::and);
	}

	public StateFormula<A> or(StateFormula<A> rhs) {
		if (rhs == top()) {
			return rhs;
		} else if (rhs == bottom()) {
			return this;
		}
		return CompositionFormula.of(this, BinaryLogicOperator.OR, rhs);
	}

	public static <A> StateFormula<A> or(List<? extends StateFormula<A>> formulas) {
		if (formulas.isEmpty()) {
			return bottom();
		}
		if (formulas.size() == 1) {
			return formulas.get(0);
		}
		return new CompositionFormula<>(BinaryLogicOperator.OR, formulas);
	}

	public static <A> Collector<StateFormula<A>, ?, StateFormula<A>> or() {
		return Collectors.collectingAndThen(Collectors.toList(), StateFormula::or);
	}

	public StateFormula<A> implies(StateFormula<A> rhs) {
		if (rhs == top()) {
			return rhs;
		}
		return new Implication<>(this, rhs);
	}

	public static <A> StateFormula<A> allEquals(List<? extends Atom<A>> atoms) {
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
