package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Result;
import io.github.cvc5.Solver;
import io.github.cvc5.Sort;
import io.github.cvc5.Term;

import java.util.HashMap;
import java.util.Map;

import static de.lukaspanneke.masterthesis.Options.*;

public class SatSolver {

	private static Result checkSat(Formula formula) {
		Solver solver = new Solver();
		if (SHOW_MODEL) {
			solver.setOption("produce-models", "true");
		}
		Sort integer = solver.getIntegerSort();
		Map<Variable, Term> atoms = new HashMap<>();
		Term cvc5Formula = formula.toCvc5(solver, v -> atoms.computeIfAbsent(v, variable -> solver.mkConst(integer, variable.name())));
		solver.assertFormula(cvc5Formula);
		Result result = solver.checkSat();
		if (result.isNull() || result.isUnknown()) {
			throw new AssertionError("SAT result is " + result + ". formula was " + formula + ", encoded as " + cvc5Formula);
		}
		if (SHOW_MODEL && result.isSat()) {
			for (Term atom : atoms.values()) {
				System.err.println(atom + " = " + solver.getValue(atom));
			}
		}
		return result;
	}

	public static boolean isSatisfiable(Formula formula) {
		if (SHOW_FORMULAS) {
			System.err.println("      " + formula);
		}
		Result result = checkSat(formula);
		if (PRINT_COLOR_CONFLICT_INFO) {
			String answer = result.isSat() ? "SAT" : result.isUnsat() ? "UNSAT" : result.toString();
			System.err.println("      " + answer);
		}
		return result.isSat();
	}

	public static boolean isTautology(Formula formula) {
		if (SHOW_FORMULAS) {
			System.err.println("    " + formula);
		}
		Result result = checkSat(formula.not());
		if (PRINT_COLOR_CUTOFF_INFO) {
			String answer = result.isUnsat() ? "TAUTOLOGY" : result.isSat() ? "NOT A TAUTOLOGY" : result.toString();
			System.err.println("    " + answer);
		}
		return result.isUnsat();
	}
}
