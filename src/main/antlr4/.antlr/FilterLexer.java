// Generated from c:\Users\yusuf.aksoy\Desktop\antlr\src\main\antlr4\Filter.g4 by ANTLR 4.8
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class FilterLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		MUL=1, DIV=2, ADD=3, SUB=4, EXP=5, MOD=6, EQ=7, NEQ=8, GT=9, GTE=10, LT=11, 
		LTE=12, AND=13, OR=14, NOT=15, TRUE=16, FALSE=17, DOT=18, COMMA=19, LPAREN=20, 
		RPAREN=21, ID=22, NUMBER=23, STRING=24, WS=25;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"MUL", "DIV", "ADD", "SUB", "EXP", "MOD", "EQ", "NEQ", "GT", "GTE", "LT", 
			"LTE", "AND", "OR", "NOT", "TRUE", "FALSE", "DOT", "COMMA", "LPAREN", 
			"RPAREN", "ID", "NUMBER", "STRING", "WS"
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


	public FilterLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Filter.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\33\u00a9\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3"+
		"\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\f\3\f\3\r\3\r\3\r\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\5\16X\n\16\3\17\3\17\3\17\3\17\5\17^\n\17\3\20\3"+
		"\20\3\20\3\20\3\20\3\20\5\20f\n\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\5\21p\n\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\5\22"+
		"|\n\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\6\27\u0087\n\27\r"+
		"\27\16\27\u0088\3\30\6\30\u008c\n\30\r\30\16\30\u008d\3\30\3\30\6\30\u0092"+
		"\n\30\r\30\16\30\u0093\5\30\u0096\n\30\3\31\3\31\3\31\3\31\7\31\u009c"+
		"\n\31\f\31\16\31\u009f\13\31\3\31\3\31\3\32\6\32\u00a4\n\32\r\32\16\32"+
		"\u00a5\3\32\3\32\2\2\33\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f"+
		"\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63"+
		"\33\3\2\6\4\2C\\c|\3\2\62;\4\2))^^\4\2\13\13\"\"\2\u00b4\2\3\3\2\2\2\2"+
		"\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2"+
		"\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2"+
		"\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2"+
		"\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2"+
		"\2\63\3\2\2\2\3\65\3\2\2\2\5\67\3\2\2\2\79\3\2\2\2\t;\3\2\2\2\13=\3\2"+
		"\2\2\r?\3\2\2\2\17C\3\2\2\2\21E\3\2\2\2\23G\3\2\2\2\25I\3\2\2\2\27L\3"+
		"\2\2\2\31N\3\2\2\2\33W\3\2\2\2\35]\3\2\2\2\37e\3\2\2\2!o\3\2\2\2#{\3\2"+
		"\2\2%}\3\2\2\2\'\177\3\2\2\2)\u0081\3\2\2\2+\u0083\3\2\2\2-\u0086\3\2"+
		"\2\2/\u008b\3\2\2\2\61\u0097\3\2\2\2\63\u00a3\3\2\2\2\65\66\7,\2\2\66"+
		"\4\3\2\2\2\678\7\61\2\28\6\3\2\2\29:\7-\2\2:\b\3\2\2\2;<\7/\2\2<\n\3\2"+
		"\2\2=>\7`\2\2>\f\3\2\2\2?@\7O\2\2@A\7Q\2\2AB\7F\2\2B\16\3\2\2\2CD\7<\2"+
		"\2D\20\3\2\2\2EF\7#\2\2F\22\3\2\2\2GH\7@\2\2H\24\3\2\2\2IJ\7@\2\2JK\7"+
		"?\2\2K\26\3\2\2\2LM\7>\2\2M\30\3\2\2\2NO\7>\2\2OP\7?\2\2P\32\3\2\2\2Q"+
		"R\7C\2\2RS\7P\2\2SX\7F\2\2TU\7c\2\2UV\7p\2\2VX\7f\2\2WQ\3\2\2\2WT\3\2"+
		"\2\2X\34\3\2\2\2YZ\7Q\2\2Z^\7T\2\2[\\\7q\2\2\\^\7t\2\2]Y\3\2\2\2][\3\2"+
		"\2\2^\36\3\2\2\2_`\7P\2\2`a\7Q\2\2af\7V\2\2bc\7p\2\2cd\7q\2\2df\7v\2\2"+
		"e_\3\2\2\2eb\3\2\2\2f \3\2\2\2gh\7V\2\2hi\7T\2\2ij\7W\2\2jp\7G\2\2kl\7"+
		"v\2\2lm\7t\2\2mn\7w\2\2np\7g\2\2og\3\2\2\2ok\3\2\2\2p\"\3\2\2\2qr\7H\2"+
		"\2rs\7C\2\2st\7N\2\2tu\7U\2\2u|\7G\2\2vw\7h\2\2wx\7c\2\2xy\7n\2\2yz\7"+
		"u\2\2z|\7g\2\2{q\3\2\2\2{v\3\2\2\2|$\3\2\2\2}~\7\60\2\2~&\3\2\2\2\177"+
		"\u0080\7.\2\2\u0080(\3\2\2\2\u0081\u0082\7*\2\2\u0082*\3\2\2\2\u0083\u0084"+
		"\7+\2\2\u0084,\3\2\2\2\u0085\u0087\t\2\2\2\u0086\u0085\3\2\2\2\u0087\u0088"+
		"\3\2\2\2\u0088\u0086\3\2\2\2\u0088\u0089\3\2\2\2\u0089.\3\2\2\2\u008a"+
		"\u008c\t\3\2\2\u008b\u008a\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008b\3\2"+
		"\2\2\u008d\u008e\3\2\2\2\u008e\u0095\3\2\2\2\u008f\u0091\7\60\2\2\u0090"+
		"\u0092\t\3\2\2\u0091\u0090\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u0091\3\2"+
		"\2\2\u0093\u0094\3\2\2\2\u0094\u0096\3\2\2\2\u0095\u008f\3\2\2\2\u0095"+
		"\u0096\3\2\2\2\u0096\60\3\2\2\2\u0097\u009d\7)\2\2\u0098\u009c\n\4\2\2"+
		"\u0099\u009a\7^\2\2\u009a\u009c\t\4\2\2\u009b\u0098\3\2\2\2\u009b\u0099"+
		"\3\2\2\2\u009c\u009f\3\2\2\2\u009d\u009b\3\2\2\2\u009d\u009e\3\2\2\2\u009e"+
		"\u00a0\3\2\2\2\u009f\u009d\3\2\2\2\u00a0\u00a1\7)\2\2\u00a1\62\3\2\2\2"+
		"\u00a2\u00a4\t\5\2\2\u00a3\u00a2\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a3"+
		"\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00a8\b\32\2\2"+
		"\u00a8\64\3\2\2\2\17\2W]eo{\u0088\u008d\u0093\u0095\u009b\u009d\u00a5"+
		"\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}