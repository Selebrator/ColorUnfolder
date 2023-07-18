package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public record Variable(String name) implements ArithmeticExpression, Comparable<Variable> {

	public Variable local(String discriminator) {
		return new Variable(name + "_" + discriminator);
	}

	@Override
	public void collectSupport(Set<Variable> accumulator) {
		accumulator.add(this);
	}

	@Override
	public ArithmeticExpression substitute(Map<Variable, Variable> map) {
		Variable ans = map.get(this);
		return ans != null ? ans : this;
	}

	@Override
	public Term toCvc5(Solver solver, Function<Variable, Term> atoms) {
		return atoms.apply(this);
	}

	@Override
	public int compareTo(Variable that) {
		return this.name.compareTo(that.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
