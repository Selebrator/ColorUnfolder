package de.lukaspanneke.masterthesis.unfolding;

import de.lukaspanneke.masterthesis.components.Variable;
import de.lukaspanneke.masterthesis.logic.Formula;
import io.github.cvc5.Result;
import io.github.cvc5.Solver;
import io.github.cvc5.Sort;
import io.github.cvc5.Term;

import java.util.HashMap;
import java.util.Map;

public class Predicate {

	private static Result checkSat(Formula<Variable> formula) {
		Solver solver = new Solver();
		solver.setOption("produce-models", "true");
		Sort integer = solver.getIntegerSort();
		Map<Variable, Term> atoms = new HashMap<>();
		Term cvc5Formula = formula.toCvc5(solver, v -> atoms.computeIfAbsent(v, variable -> solver.mkConst(integer, variable.name())));
		solver.assertFormula(cvc5Formula);
		Result result = solver.checkSat();
		if (result.isNull() || result.isUnknown()) {
			throw new AssertionError("SAT result is " + result + ". formula was " + formula + ", encoded as " + cvc5Formula);
		}
		return result;
	}

	public static boolean isSatisfiable(Formula<Variable> formula) {
		Result result = checkSat(formula);
		String answer = result.isSat() ? "SAT" : result.isUnsat() ? "UNSAT" : result.toString();
		System.out.println("      " + answer + " " + formula);
		return result.isSat();
	}

	public static boolean isTautology(Formula<Variable> formula) {
		Result result = checkSat(formula.not());
		String answer = result.isUnsat() ? "TAUTOLOGY" : result.isSat() ? "NOT A TAUTOLOGY" : result.toString();
		System.out.println("    " + answer + " " + formula);
		return result.isUnsat();
	}
}
