package de.lukaspanneke.masterthesis.logic;

import com.microsoft.z3.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public record ToZ3Visitor(
		Context solver,
		Function<Variable, IntExpr> atoms
) implements FormulaVisitor<BoolExpr, ArithExpr<IntSort>> {

	@Override
	public BoolExpr visit(AndOr it) {
		BoolExpr[] terms = it.formulas().stream()
				.map(this::visit)
				.toArray(BoolExpr[]::new);
		solver.mkAnd();
		return switch (it.operator()) {
			case AND -> solver.mkAnd(terms);
			case OR -> solver.mkOr(terms);
		};
	}

	@Override
	public ArithExpr<IntSort> visit(ArithmeticBoolean it) {
		// cast should be fine since both output expressions are ints.
		return (IntExpr) solver.mkITE(this.visit(it.formula()), solver.mkInt(1), solver.mkInt(0));
	}

	@Override
	public BoolExpr visit(Bottom it) {
		return solver.mkFalse();
	}

	@Override
	public ArithExpr<IntSort> visit(Calculation it) {
		ArithExpr<IntSort> lhs = this.visit(it.lhs());
		ArithExpr<IntSort> rhs = this.visit(it.rhs());
		return switch (it.operator()) {
			case PLUS -> solver.mkAdd(lhs, rhs);
			case MINUS -> solver.mkSub(lhs, rhs);
			case TIMES -> solver.mkMul(lhs, rhs);
			case INT_DIV -> solver.mkDiv(lhs, rhs);
			case MOD -> solver.mkMod(lhs, rhs);
		};
	}

	@Override
	public BoolExpr visit(Comparison it) {
		ArithExpr<IntSort> lhs = this.visit(it.lhs());
		ArithExpr<IntSort> rhs = this.visit(it.rhs());
		return switch (it.operator()) {
			case LESS_THEN -> solver.mkLt(lhs, rhs);
			case LESS_EQUALS -> solver.mkLe(lhs, rhs);
			case NOT_EQUALS -> solver.mkDistinct(lhs, rhs);
			case GREATER_EQUALS -> solver.mkGe(lhs, rhs);
			case GREATER_THEN -> solver.mkGt(lhs, rhs);
		};
	}

	@Override
	public IntExpr visit(Constant it) {
		return solver.mkInt(it.value());
	}

	@Override
	public BoolExpr visit(Equality it) {
		List<ArithmeticExpression> terms = List.copyOf(it.terms());
		BoolExpr ans = solver.mkTrue();
		for (int i = 1; i < terms.size(); i++) {
			ans = solver.mkAnd(ans, solver.mkEq(this.visit(terms.get(i - 1)), this.visit(terms.get(i))));
		}
		return ans;
	}

	@Override
	public BoolExpr visit(Implication it) {
		return solver.mkImplies(this.visit(it.lhs()), this.visit(it.rhs()));
	}

	@Override
	public BoolExpr visit(Negation it) {
		return solver.mkNot(this.visit(it.term()));
	}

	@Override
	public BoolExpr visit(QuantifiedFormula it) {
		try {
			Map<Variable, IntExpr> freeAtoms = new HashMap<>();
			Function<Variable, IntExpr> newAtoms = atom -> it.variables().contains(atom)
					? freeAtoms.computeIfAbsent(atom, a -> /* cast seems fine */ (IntExpr) solver.mkConst(a.toString(), solver.getIntSort()))
					: atoms.apply(atom);
			ToZ3Visitor newVisitor = new ToZ3Visitor(solver, newAtoms);
			BoolExpr body = newVisitor.visit(it.body());
			IntExpr[] quantifiedVariables = it.variables().stream()
					.map(newVisitor::visit)
					.toArray(IntExpr[]::new);
			return switch (it.quantifier()) {
				case EXISTS -> solver.mkExists(quantifiedVariables, body, 0, null, null, null, null);
				case FORALL -> solver.mkForall(quantifiedVariables, body, 0, null, null, null, null);
			};
		} catch (Exception e) {
			System.err.println("could not encode " + it);
			throw e;
		}
	}

	@Override
	public BoolExpr visit(Top it) {
		return solver.mkTrue();
	}

	@Override
	public IntExpr visit(Variable it) {
		return atoms.apply(it);
	}
}
