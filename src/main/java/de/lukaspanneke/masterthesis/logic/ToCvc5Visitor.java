package de.lukaspanneke.masterthesis.logic;


import io.github.cvc5.Kind;
import io.github.cvc5.Solver;
import io.github.cvc5.Term;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public record ToCvc5Visitor(
		Solver solver,
		Function<Variable, Term> atoms
) implements FormulaVisitor<Term, Term> {

	@Override
	public Term visit(AndOr it) {
		return solver.mkTerm(
				switch (it.operator()) {
					case AND -> Kind.AND;
					case OR -> Kind.OR;
				},
				it.formulas().stream()
						.map(this::visit)
						.toArray(Term[]::new)
		);
	}

	@Override
	public Term visit(ArithmeticBoolean it) {
		return solver.mkTerm(Kind.ITE, this.visit(it.formula()), solver.mkInteger(1), solver.mkInteger(0));
	}

	@Override
	public Term visit(Bottom it) {
		return solver.mkFalse();
	}

	@Override
	public Term visit(Calculation it) {
		return solver.mkTerm(
				switch (it.operator()) {
					case PLUS -> Kind.ADD;
					case MINUS -> Kind.SUB;
					case TIMES -> Kind.MULT;
				},
				this.visit(it.lhs()),
				this.visit(it.rhs())
		);
	}

	@Override
	public Term visit(Comparison it) {
		return solver.mkTerm(
				switch (it.operator()) {
					case LESS_THEN -> Kind.LT;
					case LESS_EQUALS -> Kind.LEQ;
					case NOT_EQUALS -> Kind.DISTINCT;
					case GREATER_EQUALS -> Kind.GEQ;
					case GREATER_THEN -> Kind.GT;
				},
				this.visit(it.lhs()),
				this.visit(it.rhs())
		);
	}

	@Override
	public Term visit(Constant it) {
		return solver.mkInteger(it.value());
	}

	@Override
	public Term visit(Equality it) {
		return solver.mkTerm(Kind.EQUAL, it.terms().stream()
				.map(this::visit)
				.toArray(Term[]::new));
	}

	@Override
	public Term visit(Implication it) {
		return solver.mkTerm(Kind.IMPLIES, this.visit(it.lhs()), this.visit(it.rhs()));
	}

	@Override
	public Term visit(Negation it) {
		return solver.mkTerm(Kind.NOT, visit(it.term()));
	}

	@Override
	public Term visit(QuantifiedFormula it) {
		try {
			Map<Variable, Term> freeAtoms = new HashMap<>();
			Function<Variable, Term> newAtoms = atom -> it.variables().contains(atom)
					? freeAtoms.computeIfAbsent(atom, a -> solver.mkVar(solver.getIntegerSort(), a.toString()))
					: atoms.apply(atom);
			ToCvc5Visitor newVisitor = new ToCvc5Visitor(solver, newAtoms);
			Term formula = newVisitor.visit(it.body());
			Term quantifiedVariables = solver.mkTerm(Kind.VARIABLE_LIST, it.variables().stream()
					.map(newVisitor::visit)
					.toArray(Term[]::new));
			return solver.mkTerm(
					switch (it.quantifier()) {
						case EXISTS -> Kind.EXISTS;
						case FORALL -> Kind.FORALL;
					},
					quantifiedVariables,
					formula
			);
		} catch (Exception e) {
			System.err.println("could not encode " + it);
			throw e;
		}
	}

	@Override
	public Term visit(Top it) {
		return solver.mkTrue();
	}

	@Override
	public Term visit(Variable it) {
		return atoms.apply(it);
	}
}
