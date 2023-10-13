package de.lukaspanneke.masterthesis.logic;

public interface FormulaVisitor<Bool, Arith> {
	default Bool visit(Formula ctx) {
		if (ctx instanceof AndOr that) return this.visit(that);
		else if (ctx instanceof Bottom that) return this.visit(that);
		else if (ctx instanceof Comparison that) return this.visit(that);
		else if (ctx instanceof Equality that) return this.visit(that);
		else if (ctx instanceof Implication that) return this.visit(that);
		else if (ctx instanceof Negation that) return this.visit(that);
		else if (ctx instanceof QuantifiedFormula that) return this.visit(that);
		else if (ctx instanceof Top that) return this.visit(that);
		else throw new AssertionError("unreachable (please stabilize pattern matching)");
	}

	default Arith visit(ArithmeticExpression ctx) {
		if (ctx instanceof ArithmeticBoolean that) return this.visit(that);
		else if (ctx instanceof Calculation that) return this.visit(that);
		else if (ctx instanceof Constant that) return this.visit(that);
		else if (ctx instanceof Variable that) return this.visit(that);
		else throw new AssertionError("unreachable (please stabilize pattern matching)");
	}

	Bool visit(AndOr it);

	Arith visit(ArithmeticBoolean it);

	Bool visit(Bottom it);

	Arith visit(Calculation it);

	Bool visit(Comparison it);

	Arith visit(Constant it);

	Bool visit(Equality it);

	Bool visit(Implication it);

	Bool visit(Negation it);

	Bool visit(QuantifiedFormula it);

	Bool visit(Top it);

	Arith visit(Variable it);
}
