package de.lukaspanneke.masterthesis.components;

import io.github.cvc5.Solver;
import io.github.cvc5.Term;
import de.lukaspanneke.masterthesis.logic.Atom;

import java.util.function.Function;

public record Variable(String name) implements Atom<Variable>, Comparable<Variable> {

	public Variable local(String discriminator) {
		return new Variable(name + "_" + discriminator);
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

	@Override
	public Variable value() {
		return this;
	}
}
