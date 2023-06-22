package org.example.logic.generic.formula;

import com.google.common.base.Preconditions;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;
import org.example.logic.generic.BinaryLogicOperator;
import org.example.logic.generic.expression.Atom;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompositionFormula<A> extends StateFormula<A> {

	/* invariant: formulas.size() >= 2 */
	private final List<StateFormula<A>> formulas;
	private final BinaryLogicOperator operator;

	protected CompositionFormula(StateFormula<A> f1, BinaryLogicOperator operator, StateFormula<A> f2) {
		this.formulas = List.of(f1, f2);
		this.operator = operator;
	}

	protected CompositionFormula(BinaryLogicOperator operator, Collection<? extends StateFormula<A>> formulas) {
		Preconditions.checkArgument(formulas.size() >= 2, operator.name() + " has to be applied to at least 2 arguments");
		this.formulas = List.copyOf(formulas);
		this.operator = operator;
	}

	public static <A> CompositionFormula<A> of(StateFormula<A> f1, BinaryLogicOperator operator, StateFormula<A> f2) {
		assert f1 != null && f2 != null;
		if (f1 instanceof CompositionFormula<A> c1 && f2 instanceof CompositionFormula<A> c2) {
			if (c1.operator == operator && c2.operator == operator) {
				List<StateFormula<A>> compose = Stream.concat(c1.formulas.stream(), c2.formulas.stream()).toList();
				return new CompositionFormula<>(operator, compose);
			} else if (c1.operator == operator || c2.operator == operator) {
				CompositionFormula<A> same;
				StateFormula<A> different;
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
				List<StateFormula<A>> compose = (sameFirst
						? Stream.concat(same.formulas.stream(), Stream.of(different))
						: Stream.concat(Stream.of(different), same.formulas.stream()))
						.toList();
				return new CompositionFormula<>(operator, compose);
			} else {
				return new CompositionFormula<>(f1, operator, f2);
			}
		} else if (f1 instanceof CompositionFormula || f2 instanceof CompositionFormula) {
			CompositionFormula<A> c;
			StateFormula<A> f;
			boolean compositionFirst;
			if (f1 instanceof CompositionFormula) {
				c = (CompositionFormula<A>) f1;
				f = f2;
				compositionFirst = true;
			} else {
				f = f1;
				c = (CompositionFormula<A>) f2;
				compositionFirst = false;
			}
			if (c.operator == operator) {
				List<StateFormula<A>> compose = (compositionFirst
						? Stream.concat(c.formulas.stream(), Stream.of(f))
						: Stream.concat(Stream.of(f), c.formulas.stream()))
						.toList();
				return new CompositionFormula<>(operator, compose);
			} else {
				return new CompositionFormula<>(f1, operator, f2);
			}
		} else {
			return new CompositionFormula<>(f1, operator, f2);
		}
	}

	public List<StateFormula<A>> formulas() {
		return this.formulas;
	}

	public BinaryLogicOperator operator() {
		return this.operator;
	}

	@Override
	protected void collectSupport(Set<A> accumulator) {
		for (StateFormula<A> formula : this.formulas) {
			formula.collectSupport(accumulator);
		}
	}

	@Override
	public StateFormula<A> local(String discriminator) {
		return new CompositionFormula<>(operator,
				formulas.stream()
						.map(f -> f.local(discriminator))
						.toList()
		);
	}

	@Override
	public StateFormula<A> substitute(Map<? extends Atom<A>, ? extends Atom<A>> map) {
		return new CompositionFormula<>(operator,
				formulas.stream()
						.map(f -> f.substitute(map))
						.toList()
		);
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
				.map(StateFormula::toString)
				.collect(Collectors.joining(" " + this.operator.symbol() + " ", "(", ")"));
	}
}
