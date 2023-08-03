package de.lukaspanneke.masterthesis.parser;

import de.lukaspanneke.masterthesis.logic.ArithmeticExpression;
import de.lukaspanneke.masterthesis.logic.Constant;
import de.lukaspanneke.masterthesis.logic.Formula;
import de.lukaspanneke.masterthesis.logic.Variable;

import java.util.Map;

public class FormulaVisitor extends HlLoLABaseVisitor<FormulaVisitor.FormulaOrExpression> {

	private final Map<String, Variable> variables;

	public FormulaVisitor(Map<String, Variable> variables) {
		this.variables = variables;
	}

	private static FormulaOrExpression formula(Formula formula) {
		return new FormulaOrExpression(formula, null);
	}

	private static FormulaOrExpression expression(ArithmeticExpression expression) {
		return new FormulaOrExpression(null, expression);
	}

	record FormulaOrExpression(Formula formula, ArithmeticExpression expression) {
	}

	@Override
	public FormulaOrExpression visitFormula(HlLoLAParser.FormulaContext ctx) {
		return formula(ctx.andFormula().stream()
				.map(this::visit)
				.map(FormulaOrExpression::formula)
				.collect(Formula.or()));
	}

	@Override
	public FormulaOrExpression visitAndFormula(HlLoLAParser.AndFormulaContext ctx) {
		return formula(ctx.implFormula().stream()
				.map(this::visit)
				.map(FormulaOrExpression::formula)
				.collect(Formula.and()));
	}

	@Override
	public FormulaOrExpression visitImplFormula(HlLoLAParser.ImplFormulaContext ctx) {
		return switch (ctx.negFormula().size()) {
			case 1 -> visit(ctx.negFormula(0));
			case 2 -> formula(visit(ctx.negFormula(0)).formula.implies(visit(ctx.negFormula(1)).formula));
			default -> throw new RuntimeException();
		};
	}

	@Override
	public FormulaOrExpression visitNegFormula(HlLoLAParser.NegFormulaContext ctx) {
		if (ctx.getStart().getText().startsWith("NOT")) {
			return formula(visit(ctx.atomFormula()).formula);
		} else {
			return visit(ctx.atomFormula());
		}
	}

	@Override
	public FormulaOrExpression visitTop(HlLoLAParser.TopContext ctx) {
		return formula(Formula.top());
	}

	@Override
	public FormulaOrExpression visitBottom(HlLoLAParser.BottomContext ctx) {
		return formula(Formula.bottom());
	}

	@Override
	public FormulaOrExpression visitComparison(HlLoLAParser.ComparisonContext ctx) {
		ArithmeticExpression lhs = visit(ctx.lhs).expression;
		ArithmeticExpression rhs = visit(ctx.rhs).expression;
		return formula(switch (ctx.operator.getText()) {
			case "<" -> lhs.lt(rhs);
			case "<=" -> lhs.leq(rhs);
			case "!=" -> lhs.neq(rhs);
			case ">=" -> lhs.geq(rhs);
			case ">" -> lhs.gt(rhs);
			default -> throw new ParserException("illegal operator " + ctx.operator);
		});
	}

	@Override
	public FormulaOrExpression visitEquality(HlLoLAParser.EqualityContext ctx) {
		return formula(ctx.expression().stream()
				.map(this::visit)
				.map(FormulaOrExpression::expression)
				.collect(Formula.eq()));
	}

	@Override
	public FormulaOrExpression visitQuantification(HlLoLAParser.QuantificationContext ctx) {
		throw new UnsupportedOperationException(); // TODO implement when needed
	}

	@Override
	public FormulaOrExpression visitExpression(HlLoLAParser.ExpressionContext ctx) {
		ArithmeticExpression lhs = visit(ctx.multiplyExpression(0)).expression;
		for (int i = 1; i < ctx.multiplyExpression().size(); i++) {
			switch (ctx.operator.getText()) {
				case "+" -> lhs = lhs.plus(visit(ctx.multiplyExpression(i)).expression);
				case "-" -> lhs = lhs.minus(visit(ctx.multiplyExpression(i)).expression);
			}
		}
		return expression(lhs);
	}

	@Override
	public FormulaOrExpression visitMultiplyExpression(HlLoLAParser.MultiplyExpressionContext ctx) {
		return expression(ctx.signedAtom().stream()
				.map(this::visit)
				.map(FormulaOrExpression::expression)
				.reduce(ArithmeticExpression::times)
				.orElseThrow());
	}

	@Override
	public FormulaOrExpression visitSignedAtom(HlLoLAParser.SignedAtomContext ctx) {
		var ans = visit(ctx.atom()).expression;
		if (ctx.getStart().getText().startsWith("-")) {
			if (ans instanceof Constant c) {
				ans = Constant.of(-c.value());
			} else {
				ans = Constant.of(0).minus(ans);
			}
		}
		return expression(ans);
	}

	@Override
	public FormulaOrExpression visitVariableExpression(HlLoLAParser.VariableExpressionContext ctx) {
		Variable var = variables.get(ctx.variable().getText());
		if (var == null) {
			var invalid = ctx.variable().getStart();
			throw new ParserException("used undefined variable " + invalid.getText() + " at line " + invalid.getLine() + " char " + invalid.getCharPositionInLine());
		}
		return expression(var);
	}

	@Override
	public FormulaOrExpression visitConstantExpression(HlLoLAParser.ConstantExpressionContext ctx) {
		return expression(Constant.of(Integer.parseInt(ctx.constant().getText())));
	}

	@Override
	public FormulaOrExpression visitParExpression(HlLoLAParser.ParExpressionContext ctx) {
		return visit(ctx.expression());
	}
}
