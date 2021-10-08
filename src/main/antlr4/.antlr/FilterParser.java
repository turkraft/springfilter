// Generated from c:\Users\yusuf.aksoy\Desktop\antlr\src\main\antlr4\Filter.g4 by ANTLR 4.8
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class FilterParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		MUL=1, DIV=2, ADD=3, SUB=4, EXP=5, MOD=6, EQ=7, NEQ=8, GT=9, GTE=10, LT=11, 
		LTE=12, AND=13, OR=14, NOT=15, TRUE=16, FALSE=17, DOT=18, COMMA=19, LPAREN=20, 
		RPAREN=21, ID=22, NUMBER=23, STRING=24, WS=25;
	public static final int
		RULE_filter = 0, RULE_predicate = 1, RULE_field = 2, RULE_input = 3;
	private static String[] makeRuleNames() {
		return new String[] {
			"filter", "predicate", "field", "input"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'*'", "'/'", "'+'", "'-'", "'^'", "'MOD'", "':'", "'!'", "'>'", 
			"'>='", "'<'", "'<='", null, null, null, null, null, "'.'", "','", "'('", 
			"')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "MUL", "DIV", "ADD", "SUB", "EXP", "MOD", "EQ", "NEQ", "GT", "GTE", 
			"LT", "LTE", "AND", "OR", "NOT", "TRUE", "FALSE", "DOT", "COMMA", "LPAREN", 
			"RPAREN", "ID", "NUMBER", "STRING", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Filter.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public FilterParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class FilterContext extends ParserRuleContext {
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public TerminalNode EOF() { return getToken(FilterParser.EOF, 0); }
		public FilterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_filter; }
	}

	public final FilterContext filter() throws RecognitionException {
		FilterContext _localctx = new FilterContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_filter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(8);
			predicate(0);
			setState(9);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PredicateContext extends ParserRuleContext {
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
	 
		public PredicateContext() { }
		public void copyFrom(PredicateContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class NotContext extends PredicateContext {
		public Token operator;
		public PredicateContext right;
		public TerminalNode NOT() { return getToken(FilterParser.NOT, 0); }
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public NotContext(PredicateContext ctx) { copyFrom(ctx); }
	}
	public static class ComparisonContext extends PredicateContext {
		public PredicateContext left;
		public Token comparator;
		public PredicateContext right;
		public List<PredicateContext> predicate() {
			return getRuleContexts(PredicateContext.class);
		}
		public PredicateContext predicate(int i) {
			return getRuleContext(PredicateContext.class,i);
		}
		public TerminalNode EQ() { return getToken(FilterParser.EQ, 0); }
		public TerminalNode NEQ() { return getToken(FilterParser.NEQ, 0); }
		public TerminalNode GT() { return getToken(FilterParser.GT, 0); }
		public TerminalNode GTE() { return getToken(FilterParser.GTE, 0); }
		public TerminalNode LT() { return getToken(FilterParser.LT, 0); }
		public TerminalNode LTE() { return getToken(FilterParser.LTE, 0); }
		public ComparisonContext(PredicateContext ctx) { copyFrom(ctx); }
	}
	public static class OrContext extends PredicateContext {
		public PredicateContext left;
		public Token operator;
		public PredicateContext right;
		public List<PredicateContext> predicate() {
			return getRuleContexts(PredicateContext.class);
		}
		public PredicateContext predicate(int i) {
			return getRuleContext(PredicateContext.class,i);
		}
		public TerminalNode OR() { return getToken(FilterParser.OR, 0); }
		public OrContext(PredicateContext ctx) { copyFrom(ctx); }
	}
	public static class AndContext extends PredicateContext {
		public PredicateContext left;
		public Token operator;
		public PredicateContext right;
		public List<PredicateContext> predicate() {
			return getRuleContexts(PredicateContext.class);
		}
		public PredicateContext predicate(int i) {
			return getRuleContext(PredicateContext.class,i);
		}
		public TerminalNode AND() { return getToken(FilterParser.AND, 0); }
		public AndContext(PredicateContext ctx) { copyFrom(ctx); }
	}
	public static class PriorityContext extends PredicateContext {
		public TerminalNode LPAREN() { return getToken(FilterParser.LPAREN, 0); }
		public PredicateContext predicate() {
			return getRuleContext(PredicateContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(FilterParser.RPAREN, 0); }
		public PriorityContext(PredicateContext ctx) { copyFrom(ctx); }
	}
	public static class ValueContext extends PredicateContext {
		public FieldContext field() {
			return getRuleContext(FieldContext.class,0);
		}
		public InputContext input() {
			return getRuleContext(InputContext.class,0);
		}
		public ValueContext(PredicateContext ctx) { copyFrom(ctx); }
	}

	public final PredicateContext predicate() throws RecognitionException {
		return predicate(0);
	}

	private PredicateContext predicate(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		PredicateContext _localctx = new PredicateContext(_ctx, _parentState);
		PredicateContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_predicate, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(20);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LPAREN:
				{
				_localctx = new PriorityContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(12);
				match(LPAREN);
				setState(13);
				predicate(0);
				setState(14);
				match(RPAREN);
				}
				break;
			case NOT:
				{
				_localctx = new NotContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(16);
				((NotContext)_localctx).operator = match(NOT);
				setState(17);
				((NotContext)_localctx).right = predicate(5);
				}
				break;
			case ID:
				{
				_localctx = new ValueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(18);
				field();
				}
				break;
			case TRUE:
			case FALSE:
			case NUMBER:
			case STRING:
				{
				_localctx = new ValueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(19);
				input();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(33);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(31);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						_localctx = new ComparisonContext(new PredicateContext(_parentctx, _parentState));
						((ComparisonContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(22);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(23);
						((ComparisonContext)_localctx).comparator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQ) | (1L << NEQ) | (1L << GT) | (1L << GTE) | (1L << LT) | (1L << LTE))) != 0)) ) {
							((ComparisonContext)_localctx).comparator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(24);
						((ComparisonContext)_localctx).right = predicate(7);
						}
						break;
					case 2:
						{
						_localctx = new AndContext(new PredicateContext(_parentctx, _parentState));
						((AndContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(25);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(26);
						((AndContext)_localctx).operator = match(AND);
						setState(27);
						((AndContext)_localctx).right = predicate(5);
						}
						break;
					case 3:
						{
						_localctx = new OrContext(new PredicateContext(_parentctx, _parentState));
						((OrContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_predicate);
						setState(28);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(29);
						((OrContext)_localctx).operator = match(OR);
						setState(30);
						((OrContext)_localctx).right = predicate(4);
						}
						break;
					}
					} 
				}
				setState(35);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class FieldContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(FilterParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(FilterParser.ID, i);
		}
		public List<TerminalNode> DOT() { return getTokens(FilterParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(FilterParser.DOT, i);
		}
		public FieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field; }
	}

	public final FieldContext field() throws RecognitionException {
		FieldContext _localctx = new FieldContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_field);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			match(ID);
			setState(41);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(37);
					match(DOT);
					setState(38);
					match(ID);
					}
					} 
				}
				setState(43);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InputContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(FilterParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(FilterParser.NUMBER, 0); }
		public TerminalNode TRUE() { return getToken(FilterParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(FilterParser.FALSE, 0); }
		public InputContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_input; }
	}

	public final InputContext input() throws RecognitionException {
		InputContext _localctx = new InputContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_input);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TRUE) | (1L << FALSE) | (1L << NUMBER) | (1L << STRING))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return predicate_sempred((PredicateContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean predicate_sempred(PredicateContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 6);
		case 1:
			return precpred(_ctx, 4);
		case 2:
			return precpred(_ctx, 3);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\33\61\4\2\t\2\4\3"+
		"\t\3\4\4\t\4\4\5\t\5\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5"+
		"\3\27\n\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3\"\n\3\f\3\16\3%\13\3"+
		"\3\4\3\4\3\4\7\4*\n\4\f\4\16\4-\13\4\3\5\3\5\3\5\2\3\4\6\2\4\6\b\2\4\3"+
		"\2\t\16\4\2\22\23\31\32\2\63\2\n\3\2\2\2\4\26\3\2\2\2\6&\3\2\2\2\b.\3"+
		"\2\2\2\n\13\5\4\3\2\13\f\7\2\2\3\f\3\3\2\2\2\r\16\b\3\1\2\16\17\7\26\2"+
		"\2\17\20\5\4\3\2\20\21\7\27\2\2\21\27\3\2\2\2\22\23\7\21\2\2\23\27\5\4"+
		"\3\7\24\27\5\6\4\2\25\27\5\b\5\2\26\r\3\2\2\2\26\22\3\2\2\2\26\24\3\2"+
		"\2\2\26\25\3\2\2\2\27#\3\2\2\2\30\31\f\b\2\2\31\32\t\2\2\2\32\"\5\4\3"+
		"\t\33\34\f\6\2\2\34\35\7\17\2\2\35\"\5\4\3\7\36\37\f\5\2\2\37 \7\20\2"+
		"\2 \"\5\4\3\6!\30\3\2\2\2!\33\3\2\2\2!\36\3\2\2\2\"%\3\2\2\2#!\3\2\2\2"+
		"#$\3\2\2\2$\5\3\2\2\2%#\3\2\2\2&+\7\30\2\2\'(\7\24\2\2(*\7\30\2\2)\'\3"+
		"\2\2\2*-\3\2\2\2+)\3\2\2\2+,\3\2\2\2,\7\3\2\2\2-+\3\2\2\2./\t\3\2\2/\t"+
		"\3\2\2\2\6\26!#+";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}