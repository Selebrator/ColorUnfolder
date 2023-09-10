package de.lukaspanneke.masterthesis.parser;

import de.lukaspanneke.masterthesis.logic.Domain;
import de.lukaspanneke.masterthesis.logic.FiniteDomain;
import de.lukaspanneke.masterthesis.logic.Formula;
import de.lukaspanneke.masterthesis.logic.Variable;
import de.lukaspanneke.masterthesis.net.Marking;
import de.lukaspanneke.masterthesis.net.Net;
import de.lukaspanneke.masterthesis.net.Place;
import de.lukaspanneke.masterthesis.net.Transition;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

public class HlLolaParser {

	public Net parse(InputStream is) throws IOException {
		var input = CharStreams.fromStream(is);
		var lexer = new HlLoLALexer(input);
		lexer.removeErrorListeners(); // don't spam on stderr
		lexer.addErrorListener(new ThrowingErrorListener());
		var tokens = new CommonTokenStream(lexer);
		HlLoLAParser parser = new HlLoLAParser(tokens);
		parser.removeErrorListeners(); // don't spam on stderr
		parser.addErrorListener(new ThrowingErrorListener());
		parser.setBuildParseTree(true);
		ParseTree tree = parser.net();
		ParseTreeWalker walker = new ParseTreeWalker();
		Listener listener = new Listener();
		walker.walk(listener, tree);

		return new Net(new Marking(listener.marking));
	}

	private static class Listener extends HlLoLABaseListener {

		private final Map<String, Domain> domains = new HashMap<>(Map.of("Int", var -> Formula.top()));
		private final Map<String, Place> places = new HashMap<>();
		private final Map<String, Domain> placeToDomain = new HashMap<>();
		private final Map<Place, Integer> marking = new HashMap<>();
		private int PLACE_ID = 1;
		private int TRANS_ID = 1;
		private static final Variable TEST_VARIABLE = new Variable("TEST_VARIABLE");

		@Override
		public void exitSort(HlLoLAParser.SortContext ctx) {
			String domainName = ctx.type().getText();
			Set<Integer> elements = new HashSet<>();
			for (var valueString : ctx.constant()) {
				int value;
				try {
					value = Integer.parseInt(valueString.getText());
				} catch (NumberFormatException e) {
					Token inlaidSymbol = valueString.getStart();
					int line = inlaidSymbol.getLine();
					int shar = inlaidSymbol.getCharPositionInLine();
					throw new ParserException("non-integer domains are not implemented yet. not an integer in declaration at " + line + " char " + shar, e);
				}
				boolean isNew = elements.add(value);
				if (!isNew) {
					Token inlaidSymbol = valueString.getStart();
					int line = inlaidSymbol.getLine();
					int shar = inlaidSymbol.getCharPositionInLine();
					throw new ParserException("domain declaration must not contain duplicates. found duplicate " + value + " at line " + line + " char " + shar);
				}
			}
			List<Integer> sortedElements = new ArrayList<>(elements);
			Collections.sort(sortedElements);
			int min = sortedElements.get(0);
			int max = sortedElements.get(sortedElements.size() - 1);
			boolean cont;
			{
				int i = min;
				for (Integer sortedElement : sortedElements) {
					if (i++ != sortedElement) {
						break;
					}
				}
				if (i == max + 1) {
					//System.out.println("HlLoLA Parser: Found continuous domain " + domainName + " = " + min + "..=" + max);
					cont = true;
				} else {
					//System.out.println("HlLoLA Parser: Found non-continuous domain " + domainName);
					cont = false;
				}
			}
			Domain domain;
			if (cont) {
				domain = FiniteDomain.fullRange(min, max);
			} else {
				domain = new FiniteDomain(var -> sortedElements.stream()
						.map(var::eq)
						.collect(Formula.or()), min, max);
			}
			var overwritten = domains.put(domainName, domain);
			if (overwritten != null) {
				Token inlaidSymbol = ctx.type().getStart();
				int line = inlaidSymbol.getLine();
				int shar = inlaidSymbol.getCharPositionInLine();
				throw new ParserException("duplicate domain declaration " + domainName + " at line " + line + " char " + shar + " already defined.");
			}
		}

		@Override
		public void exitPlaceList(HlLoLAParser.PlaceListContext ctx) {
			int size = ctx.place().size();
			for (int i = 0; i < size; i++) {
				String name = ctx.place(i).getText();
				String domainIdent = ctx.type(i).getText();
				Domain domain = domains.get(domainIdent);
				if (domain == null) {
					Token inlaidSymbol = ctx.type(i).getStart();
					int line = inlaidSymbol.getLine();
					int shar = inlaidSymbol.getCharPositionInLine();
					throw new ParserException("reference to undefined type at line " + line + " char " + shar);
				}
				placeToDomain.put(name, domain);
				var overwritten = places.put(name, new Place(PLACE_ID++, name));
				if (overwritten != null) {
					Token inlaidSymbol = ctx.place(i).getStart();
					int line = inlaidSymbol.getLine();
					int shar = inlaidSymbol.getCharPositionInLine();
					throw new ParserException("place with name " + name + " at line " + line + " char " + shar + " already defined.");
				}
			}
		}

		@Override
		public void exitMarking(HlLoLAParser.MarkingContext ctx) {
			int size = ctx.place().size();
			for (int i = 0; i < size; i++) {
				String placeName = ctx.place(i).getText();
				Place place = places.get(placeName);
				if (place == null) {
					Token inlaidSymbol = ctx.place(i).getStart();
					int line = inlaidSymbol.getLine();
					int shar = inlaidSymbol.getCharPositionInLine();
					throw new ParserException("place with name " + placeName + " at line " + line + " char " + shar + " not defined.");
				}
				String valueString = ctx.constant(i).getText();
				int value;
				try {
					value = Integer.parseInt(valueString);
				} catch (NumberFormatException e) {
					Token inlaidSymbol = ctx.constant(i).getStart();
					int line = inlaidSymbol.getLine();
					int shar = inlaidSymbol.getCharPositionInLine();
					throw new ParserException("non-integer domains are not implemented yet. not an integer in initial marking at " + line + " char " + shar, e);
				}
				var overwritten = marking.put(place, value);
				if (overwritten != null) {
					Token inlaidSymbol = ctx.place(i).getStart();
					int line = inlaidSymbol.getLine();
					int shar = inlaidSymbol.getCharPositionInLine();
					throw new ParserException("place with name " + placeName + " at line " + line + " char " + shar + " already has a token. we only allow safe nets!.");
				}
				Domain domain = placeToDomain.get(placeName);
				if (!domain.constraint(TEST_VARIABLE).evaluate(Map.of(TEST_VARIABLE, value), unused -> Stream.of(Map.of()))) {
					Token inlaidSymbol = ctx.constant(i).getStart();
					int line = inlaidSymbol.getLine();
					int shar = inlaidSymbol.getCharPositionInLine();
					throw new ParserException("invalid initial token at line " + line + " char " + shar + ". token not allowed by type.");
				}
			}
		}

		@Override
		public void exitTransition(HlLoLAParser.TransitionContext ctx) {
			String transitionName = ctx.transitionName().getText();
			Map<String, Variable> variables = new HashMap<>();
			for (var var : ctx.variable_declaration()) {
				int size = var.variable().size();
				for (int i = 0; i < size; i++) {
					String name = var.variable(i).getText();
					String domainIdent = var.type(i).getText();
					var overwritten = variables.put(name, switch (domainIdent) {
						case "Int", "Token" -> new Variable(name);
						default -> {
							Domain domain = domains.get(domainIdent);
							if (domain == null) {
								Token inlaidSymbol = var.type(i).getStart();
								int line = inlaidSymbol.getLine();
								int shar = inlaidSymbol.getCharPositionInLine();
								throw new ParserException("reference to undefined type at line " + line + " char " + shar);
							}
							yield new Variable(name, domain);
						}
					});
					if (overwritten != null) {
						Token inlaidSymbol = var.variable(i).getStart();
						int line = inlaidSymbol.getLine();
						int shar = inlaidSymbol.getCharPositionInLine();
						throw new ParserException("variable with name " + name + " at line " + line + " char " + shar + " already defined.");
					}
				}
			}
			Map<Place, Variable> preset = new HashMap<>();
			for (var pre : ctx.transition_preset()) {
				int size = pre.place().size();
				for (int i = 0; i < size; i++) {
					String placeName = pre.place(i).getText();
					Place place = places.get(placeName);
					if (place == null) {
						Token inlaidSymbol = pre.place(i).getStart();
						int line = inlaidSymbol.getLine();
						int shar = inlaidSymbol.getCharPositionInLine();
						throw new ParserException("reference to undefined place " + placeName + " at line " + line + " char " + shar);
					}
					String variableName = pre.variable(i).getText();
					Variable variable = variables.get(variableName);
					if (variable == null) {
						Token inlaidSymbol = pre.variable(i).getStart();
						int line = inlaidSymbol.getLine();
						int shar = inlaidSymbol.getCharPositionInLine();
						throw new ParserException("reference to undefined variable " + variableName + " at line " + line + " char " + shar);
					}
					var overwritten = preset.put(place, variable);
					if (overwritten != null) {
						Token inlaidSymbol = pre.place(i).getStart();
						int line = inlaidSymbol.getLine();
						int shar = inlaidSymbol.getCharPositionInLine();
						throw new ParserException("edge from " + placeName + "to " + transitionName + " at line " + line + " char " + shar + " already defined.");
					}
				}
			}
			Map<Place, Variable> postset = new HashMap<>();
			for (var post : ctx.transition_postset()) {
				int size = post.place().size();
				for (int i = 0; i < size; i++) {
					String placeName = post.place(i).getText();
					Place place = places.get(placeName);
					if (place == null) {
						Token inlaidSymbol = post.place(i).getStart();
						int line = inlaidSymbol.getLine();
						int shar = inlaidSymbol.getCharPositionInLine();
						throw new ParserException("reference to undefined place " + placeName + " at line " + line + " char " + shar);
					}
					String variableName = post.variable(i).getText();
					Variable variable = variables.get(variableName);
					if (variable == null) {
						Token inlaidSymbol = post.variable(i).getStart();
						int line = inlaidSymbol.getLine();
						int shar = inlaidSymbol.getCharPositionInLine();
						throw new ParserException("reference to undefined variable " + variableName + " at line " + line + " char " + shar);
					}
					var overwritten = postset.put(place, variable);
					if (overwritten != null) {
						Token inlaidSymbol = post.place(i).getStart();
						int line = inlaidSymbol.getLine();
						int shar = inlaidSymbol.getCharPositionInLine();
						throw new ParserException("edge from " + transitionName + "to " + placeName + " at line " + line + " char " + shar + " already defined.");
					}
				}
			}
			Formula guard;
			if (ctx.guard() != null) {
				guard = new FormulaVisitor(variables).visit(ctx.guard().formula()).formula();
			} else {
				guard = Formula.top();
			}
			Transition transition = new Transition(TRANS_ID++, transitionName, preset, postset, guard);
			preset.keySet().forEach(place -> place.postSet().add(transition));
			postset.keySet().forEach(place -> place.preSet().add(transition));
		}
	}
}
