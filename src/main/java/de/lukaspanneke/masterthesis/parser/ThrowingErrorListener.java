package de.lukaspanneke.masterthesis.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.BitSet;

class ThrowingErrorListener extends BaseErrorListener {

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
		throw new ParserException("line " + line + ":" + charPositionInLine + " " + msg);
	}

	@Override
	public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
		throw new AssertionError("recognizer = " + recognizer +
				", dfa = " + dfa +
				", startIndex = " + startIndex +
				", stopIndex = " + stopIndex +
				", exact = " + exact +
				", ambigAlts = " + ambigAlts +
				", configs = " + configs);
	}
}
