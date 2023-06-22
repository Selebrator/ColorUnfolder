package org.example;

import io.github.cvc5.Result;
import io.github.cvc5.Solver;
import io.github.cvc5.Sort;
import io.github.cvc5.Term;
import org.example.components.Variable;
import org.example.logic.generic.formula.StateFormula;

import java.util.HashMap;
import java.util.Map;

public class Predicate {

	public static boolean isSatisfiable(StateFormula<Variable> formula) {
		Solver solver = new Solver();
		solver.setOption("produce-models", "true");
		Sort integer = solver.getIntegerSort();
		Map<Variable, Term> atoms = new HashMap<>();
		Term cvc5Formula = formula.toCvc5(solver, v -> atoms.computeIfAbsent(v, variable -> solver.mkConst(integer, variable.name())));
		solver.assertFormula(cvc5Formula);
		Result result = solver.checkSat();
		System.out.println(result + " " + formula + " encoded: " + cvc5Formula);
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

	public static boolean isTautology(StateFormula<Variable> formula) {
		return !isSatisfiable(formula.not());
	}
}
