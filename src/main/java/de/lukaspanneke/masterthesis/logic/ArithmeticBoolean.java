package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Kind;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Wrap a Formula as an ArithmeticExpression,
 * so we can treat terms like numbers: true == 1, false == 0.
 * This allows us to count true terms by adding them.
 */
/* package-private */ final class ArithmeticBoolean implements ArithmeticExpression {

	private final Formula formula;

	private ArithmeticBoolean(Formula formula) {
		this.formula = formula;
	}

	public static ArithmeticBoolean of(Formula formula) {
		return new ArithmeticBoolean(formula);
	}

	public Formula formula() {
		return formula;
	}

	@Override
	public int evaluate(Map<Variable, Integer> assignment) {
		return formula.evaluate(assignment) ? 1 : 0;
	}

	@Override
	public void collectSupport(Set<Variable> accumulator) {
		formula.collectSupport(accumulator);
	}

	@Override
	public ArithmeticExpression substitute(Map<Variable, Variable> map) {
		return new ArithmeticBoolean(formula.substitute(map));
	}

	@Override
	public Term toCvc5(Solver solver, Function<Variable, Term> atoms) {
		return solver.mkTerm(Kind.ITE, formula.toCvc5(solver, atoms), solver.mkInteger(1), solver.mkInteger(0));
	}

	@Override
	public String toString() {
		return formula.toString();
	}
}
