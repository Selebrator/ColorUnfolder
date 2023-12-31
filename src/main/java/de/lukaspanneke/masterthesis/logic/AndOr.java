package de.lukaspanneke.masterthesis.logic;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* package-private */ final class AndOr extends Formula {

	/* invariant: formulas.size() >= 2 */
	private final List<Formula> formulas;
	private final Operator operator;

	private AndOr(Formula f1, Operator operator, Formula f2) {
		this.formulas = List.of(f1, f2);
		this.operator = operator;
	}

	private AndOr(Operator operator, Collection<? extends Formula> formulas) {
		if (formulas.size() < 2) {
			throw new IllegalArgumentException(operator.name() + " has to be applied to at least 2 arguments");
		}
		this.formulas = List.copyOf(formulas);
		this.operator = operator;
	}

	public static AndOr of(Formula f1, Operator operator, Formula f2) {
		assert f1 != null && f2 != null;
		if (f1 instanceof AndOr c1 && f2 instanceof AndOr c2) {
			if (c1.operator == operator && c2.operator == operator) {
				List<Formula> compose = Stream.concat(c1.formulas.stream(), c2.formulas.stream()).toList();
				return new AndOr(operator, compose);
			} else if (c1.operator == operator || c2.operator == operator) {
				AndOr same;
				Formula different;
				boolean sameFirst;
				if (c1.operator == operator) {
					same = c1;
					different = c2;
					sameFirst = true;
				} else {
					different = c1;
					same = c2;
					sameFirst = false;
				}
				List<Formula> compose = (sameFirst
						? Stream.concat(same.formulas.stream(), Stream.of(different))
						: Stream.concat(Stream.of(different), same.formulas.stream()))
						.toList();
				return new AndOr(operator, compose);
			} else {
				return new AndOr(f1, operator, f2);
			}
		} else if (f1 instanceof AndOr || f2 instanceof AndOr) {
			AndOr c;
			Formula f;
			boolean compositionFirst;
			if (f1 instanceof AndOr) {
				c = (AndOr) f1;
				f = f2;
				compositionFirst = true;
			} else {
				f = f1;
				c = (AndOr) f2;
				compositionFirst = false;
			}
			if (c.operator == operator) {
				List<Formula> compose = (compositionFirst
						? Stream.concat(c.formulas.stream(), Stream.of(f))
						: Stream.concat(Stream.of(f), c.formulas.stream()))
						.toList();
				return new AndOr(operator, compose);
			} else {
				return new AndOr(f1, operator, f2);
			}
		} else {
			return new AndOr(f1, operator, f2);
		}
	}

	public static Formula of(Operator operator, Collection<? extends Formula> formulas) {
		Formula dominating = switch (operator) {
			case AND -> bottom();
			case OR -> top();
		};
		var vanishing = dominating.not();
		Set<Formula> terms = formulas.stream()
				.filter(formula -> formula != vanishing)
				.collect(Collectors.toSet());
		if (terms.isEmpty()) {
			return vanishing;
		}
		if (terms.size() == 1) {
			return terms.iterator().next();
		}
		if (terms.stream().anyMatch(formula -> formula == dominating)) {
			return dominating;
		}
		return new AndOr(operator, terms);
	}

	public List<Formula> formulas() {
		return this.formulas;
	}

	public Operator operator() {
		return this.operator;
	}

	@Override
	public boolean evaluate(Map<Variable, Integer> assignment) {
		return switch (this.operator) {
			case AND -> this.formulas.stream().allMatch(formula -> formula.evaluate(assignment));
			case OR -> this.formulas.stream().anyMatch(formula -> formula.evaluate(assignment));
		};
	}

	@Override
	protected void collectSupport(Set<Variable> accumulator) {
		for (Formula formula : formulas) {
			formula.collectSupport(accumulator);
		}
	}

	@Override
	public Formula substitute(Map<Variable, Variable> map) {
		var collector = switch (operator) {
			case AND -> Formula.and();
			case OR -> Formula.or();
		};
		return formulas.stream()
				.map(f -> f.substitute(map))
				.collect(collector);
	}

	@Override
	public String toString() {
		return this.formulas.stream()
				.map(Formula::toString)
				.collect(Collectors.joining(" " + this.operator.symbol() + " ", "(", ")"));
	}

	public enum Operator {

		AND("∧"),
		OR("∨");

		private final String symbol;

		Operator(String symbol) {
			this.symbol = symbol;
		}

		public String symbol() {
			return this.symbol;
		}
	}
}
