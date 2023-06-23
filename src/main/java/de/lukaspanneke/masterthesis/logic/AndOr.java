package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Kind;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* package-private */ final class AndOr<A> extends Formula<A> {

	/* invariant: formulas.size() >= 2 */
	private final List<Formula<A>> formulas;
	private final Operator operator;

	private AndOr(Formula<A> f1, Operator operator, Formula<A> f2) {
		this.formulas = List.of(f1, f2);
		this.operator = operator;
	}

	private AndOr(Operator operator, Collection<? extends Formula<A>> formulas) {
		if (formulas.size() < 2) {
			throw new IllegalArgumentException(operator.name() + " has to be applied to at least 2 arguments");
		}
		this.formulas = List.copyOf(formulas);
		this.operator = operator;
	}

	public static <A> AndOr<A> of(Formula<A> f1, Operator operator, Formula<A> f2) {
		assert f1 != null && f2 != null;
		if (f1 instanceof AndOr<A> c1 && f2 instanceof AndOr<A> c2) {
			if (c1.operator == operator && c2.operator == operator) {
				List<Formula<A>> compose = Stream.concat(c1.formulas.stream(), c2.formulas.stream()).toList();
				return new AndOr<>(operator, compose);
			} else if (c1.operator == operator || c2.operator == operator) {
				AndOr<A> same;
				Formula<A> different;
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
				List<Formula<A>> compose = (sameFirst
						? Stream.concat(same.formulas.stream(), Stream.of(different))
						: Stream.concat(Stream.of(different), same.formulas.stream()))
						.toList();
				return new AndOr<>(operator, compose);
			} else {
				return new AndOr<>(f1, operator, f2);
			}
		} else if (f1 instanceof AndOr || f2 instanceof AndOr) {
			AndOr<A> c;
			Formula<A> f;
			boolean compositionFirst;
			if (f1 instanceof AndOr) {
				c = (AndOr<A>) f1;
				f = f2;
				compositionFirst = true;
			} else {
				f = f1;
				c = (AndOr<A>) f2;
				compositionFirst = false;
			}
			if (c.operator == operator) {
				List<Formula<A>> compose = (compositionFirst
						? Stream.concat(c.formulas.stream(), Stream.of(f))
						: Stream.concat(Stream.of(f), c.formulas.stream()))
						.toList();
				return new AndOr<>(operator, compose);
			} else {
				return new AndOr<>(f1, operator, f2);
			}
		} else {
			return new AndOr<>(f1, operator, f2);
		}
	}

	public static <A> Formula<A> of(Operator operator, Collection<? extends Formula<A>> formulas) {
		Formula<A> dominating = switch (operator) {
			case AND -> bottom();
			case OR -> top();
		};
		var vanishing = dominating.not();
		Set<Formula<A>> terms = formulas.stream()
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
		return new AndOr<>(operator, terms);
	}

	@Override
	protected void collectSupport(Set<A> accumulator) {
		for (Formula<A> formula : formulas) {
			formula.collectSupport(accumulator);
		}
	}

	@Override
	public Formula<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map) {
		var collector = switch (operator) {
			case AND -> Formula.<A>and();
			case OR -> Formula.<A>or();
		};
		return formulas.stream()
				.map(f -> f.substitute(map))
				.collect(collector);
	}

	@Override
	public Term toCvc5(Solver solver, Function<A, Term> atoms) {
		return solver.mkTerm(operator.toCvc5(), formulas.stream()
				.map(formula -> formula.toCvc5(solver, atoms))
				.toArray(Term[]::new));
	}

	@Override
	public String toString() {
		return this.formulas.stream()
				.map(Formula::toString)
				.collect(Collectors.joining(" " + this.operator.symbol() + " ", "(", ")"));
	}

	public enum Operator {

		AND("∧", Kind.AND),
		OR("∨", Kind.OR);

		private final String symbol;
		private final Kind cvc5;

		Operator(String symbol, Kind cvc5) {
			this.symbol = symbol;
			this.cvc5 = cvc5;
		}

		public String symbol() {
			return this.symbol;
		}

		public Kind toCvc5() {
			return cvc5;
		}
	}
}
