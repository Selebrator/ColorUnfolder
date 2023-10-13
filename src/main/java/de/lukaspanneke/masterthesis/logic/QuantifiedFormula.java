package de.lukaspanneke.masterthesis.logic;

import de.lukaspanneke.masterthesis.VariableAssignment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class QuantifiedFormula extends Formula {

	private final Quantifier quantifier;
	private final Set<Variable> variables;
	private final Formula f;

	private QuantifiedFormula(Quantifier quantifier, Set<Variable> variables, Formula f) {
		this.quantifier = quantifier;
		this.variables = variables;
		this.f = f;
	}

	public static Formula of(Quantifier quantifier, Set<Variable> variables, Formula f) {
		return new QuantifiedFormula(quantifier, variables, f);
	}

	public Quantifier quantifier() {
		return this.quantifier;
	}

	public Set<Variable> variables() {
		return this.variables;
	}

	public Formula body() {
		return this.f;
	}

	@Override
	public boolean evaluate(
			Map<Variable, Integer> assignment
	) {
		Predicate<Map<Variable, Integer>> pred = map -> {
			Map<Variable, Integer> newAssignment = new HashMap<>(assignment);
			newAssignment.putAll(map);
			return f.evaluate(newAssignment);
		};
		Stream<Map<Variable, Integer>> allAssignments = VariableAssignment.itr(variables.stream());
		return switch (quantifier) {
			case EXISTS -> allAssignments.anyMatch(pred);
			case FORALL -> allAssignments.allMatch(pred);
		};
	}

	@Override
	protected void collectSupport(Set<Variable> accumulator) {
		// free variables
		f.collectSupport(accumulator);
		accumulator.removeAll(variables);
	}

	@Override
	public Formula substitute(Map<Variable, Variable> map) {
		Map<Variable, Variable> newMap = new HashMap<>(map);
		variables.forEach(newMap::remove);
		return QuantifiedFormula.of(quantifier, variables, f.substitute(newMap));
	}

	@Override
	public String toString() {
		return "(" + this.quantifier.symbol() + " " + this.variables + ": " + this.f + ")";
	}

	public enum Quantifier {

		EXISTS("∃"),
		FORALL("∀");

		private final String symbol;

		Quantifier(String symbol) {
			this.symbol = symbol;
		}

		public String symbol() {
			return this.symbol;
		}
	}
}
