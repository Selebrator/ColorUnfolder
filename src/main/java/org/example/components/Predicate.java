package org.example.components;

import io.github.cvc5.Result;
import io.github.cvc5.Solver;
import io.github.cvc5.Sort;
import io.github.cvc5.Term;
import org.example.logic.generic.ComparisonOperator;
import org.example.logic.generic.formula.ComparisonFormula;
import org.example.logic.generic.formula.StateFormula;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record Predicate(StateFormula<Variable> formula) {
	public static Predicate TRUE = new Predicate(StateFormula.top());

	public Predicate local(String discriminator) {
		return new Predicate(formula.local(discriminator));
	}

	public Predicate and(Predicate rhs) {
		return new Predicate(this.formula.and(rhs.formula));
	}

	public static Predicate eq(Variable lhs, Variable rhs) {
		return new Predicate(ComparisonFormula.of(lhs, ComparisonOperator.EQUALS, rhs));
	}

	public boolean isSatisfiable() {
		Solver solver = new Solver();
		solver.setOption("produce-models", "true");
		Sort integer = solver.getIntegerSort();
		Map<Variable, Term> atoms = formula.support().stream().collect(Collectors.toMap(Function.identity(), variable -> solver.mkConst(integer, variable.name())));
		Term cvc5Formula = formula.toCvc5(solver, atoms);
		solver.assertFormula(cvc5Formula);
		Result result = solver.checkSat();
		System.out.println(result + " " + this + " encoded: " + cvc5Formula);
		if (result.isSat()) {
			for (Term atom : atoms.values()) {
				System.out.println(atom + " = " + solver.getValue(atom));
			}
		}
		if (result.isNull() || result.isUnknown()) {
			throw new AssertionError("result is " + result);
		}
		return result.isSat();
	}
}
