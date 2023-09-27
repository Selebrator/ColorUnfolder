package de.lukaspanneke.masterthesis.logic;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public record Variable(String name, Domain domain) implements ArithmeticExpression, Comparable<Variable> {

	public Variable(String name) {
		this(name, variable -> Formula.top());
	}

	public Variable local(String discriminator) {
		return new Variable(name + "_" + discriminator, domain);
	}

	public Formula domainConstraint() {
		return this.domain.constraint(this);
	}

	@Override
	public int evaluate(Map<Variable, Integer> assignment, Function<Stream<Variable>, Stream<Map<Variable, Integer>>> quantifierAssignments) {
		try {
			return assignment.get(this);
		} catch (NullPointerException e) {
			throw new NoSuchElementException("No value for " + this + " in assignment " + assignment, e);
		}
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
