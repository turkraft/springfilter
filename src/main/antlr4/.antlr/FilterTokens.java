// Generated from c:\Users\yusuf.aksoy\Desktop\antlr\src\main\antlr4\FilterTokens.g4 by ANTLR 4.8
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class FilterTokens extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		EQ=1, NEQ=2, GT=3, GTE=4, LT=5, LTE=6, AND=7, OR=8, NOT=9, TRUE=10, FALSE=11, 
		DOT=12, COMMA=13, LPAREN=14, RPAREN=15, ID=16, NUMBER=17, STRING=18, WS=19;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"EQ", "NEQ", "GT", "GTE", "LT", "LTE", "AND", "OR", "NOT", "TRUE", "FALSE", 
			"DOT", "COMMA", "LPAREN", "RPAREN", "ID", "NUMBER", "STRING", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "':'", "'!'", "'>'", "'>='", "'<'", "'<='", null, null, null, null, 
			null, "'.'", "','", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "EQ", "NEQ", "GT", "GTE", "LT", "LTE", "AND", "OR", "NOT", "TRUE", 
			"FALSE", "DOT", "COMMA", "LPAREN", "RPAREN", "ID", "NUMBER", "STRING", 
			"WS"
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


	public FilterTokens(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "FilterTokens.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\25\u008f\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\6\3\6"+
		"\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\5\b>\n\b\3\t\3\t\3\t\3\t\5\tD\n\t"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\5\nL\n\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3"+
		"\13\5\13V\n\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\5\fb\n\f\3\r\3"+
		"\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\6\21m\n\21\r\21\16\21n\3\22\6\22"+
		"r\n\22\r\22\16\22s\3\22\3\22\6\22x\n\22\r\22\16\22y\5\22|\n\22\3\23\3"+
		"\23\3\23\3\23\7\23\u0082\n\23\f\23\16\23\u0085\13\23\3\23\3\23\3\24\6"+
		"\24\u008a\n\24\r\24\16\24\u008b\3\24\3\24\2\2\25\3\3\5\4\7\5\t\6\13\7"+
		"\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25"+
		"\3\2\6\4\2C\\c|\3\2\62;\4\2))^^\4\2\13\13\"\"\2\u009a\2\3\3\2\2\2\2\5"+
		"\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2"+
		"\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33"+
		"\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2"+
		"\'\3\2\2\2\3)\3\2\2\2\5+\3\2\2\2\7-\3\2\2\2\t/\3\2\2\2\13\62\3\2\2\2\r"+
		"\64\3\2\2\2\17=\3\2\2\2\21C\3\2\2\2\23K\3\2\2\2\25U\3\2\2\2\27a\3\2\2"+
		"\2\31c\3\2\2\2\33e\3\2\2\2\35g\3\2\2\2\37i\3\2\2\2!l\3\2\2\2#q\3\2\2\2"+
		"%}\3\2\2\2\'\u0089\3\2\2\2)*\7<\2\2*\4\3\2\2\2+,\7#\2\2,\6\3\2\2\2-.\7"+
		"@\2\2.\b\3\2\2\2/\60\7@\2\2\60\61\7?\2\2\61\n\3\2\2\2\62\63\7>\2\2\63"+
		"\f\3\2\2\2\64\65\7>\2\2\65\66\7?\2\2\66\16\3\2\2\2\678\7C\2\289\7P\2\2"+
		"9>\7F\2\2:;\7c\2\2;<\7p\2\2<>\7f\2\2=\67\3\2\2\2=:\3\2\2\2>\20\3\2\2\2"+
		"?@\7Q\2\2@D\7T\2\2AB\7q\2\2BD\7t\2\2C?\3\2\2\2CA\3\2\2\2D\22\3\2\2\2E"+
		"F\7P\2\2FG\7Q\2\2GL\7V\2\2HI\7p\2\2IJ\7q\2\2JL\7v\2\2KE\3\2\2\2KH\3\2"+
		"\2\2L\24\3\2\2\2MN\7V\2\2NO\7T\2\2OP\7W\2\2PV\7G\2\2QR\7v\2\2RS\7t\2\2"+
		"ST\7w\2\2TV\7g\2\2UM\3\2\2\2UQ\3\2\2\2V\26\3\2\2\2WX\7H\2\2XY\7C\2\2Y"+
		"Z\7N\2\2Z[\7U\2\2[b\7G\2\2\\]\7h\2\2]^\7c\2\2^_\7n\2\2_`\7u\2\2`b\7g\2"+
		"\2aW\3\2\2\2a\\\3\2\2\2b\30\3\2\2\2cd\7\60\2\2d\32\3\2\2\2ef\7.\2\2f\34"+
		"\3\2\2\2gh\7*\2\2h\36\3\2\2\2ij\7+\2\2j \3\2\2\2km\t\2\2\2lk\3\2\2\2m"+
		"n\3\2\2\2nl\3\2\2\2no\3\2\2\2o\"\3\2\2\2pr\t\3\2\2qp\3\2\2\2rs\3\2\2\2"+
		"sq\3\2\2\2st\3\2\2\2t{\3\2\2\2uw\7\60\2\2vx\t\3\2\2wv\3\2\2\2xy\3\2\2"+
		"\2yw\3\2\2\2yz\3\2\2\2z|\3\2\2\2{u\3\2\2\2{|\3\2\2\2|$\3\2\2\2}\u0083"+
		"\7)\2\2~\u0082\n\4\2\2\177\u0080\7^\2\2\u0080\u0082\t\4\2\2\u0081~\3\2"+
		"\2\2\u0081\177\3\2\2\2\u0082\u0085\3\2\2\2\u0083\u0081\3\2\2\2\u0083\u0084"+
		"\3\2\2\2\u0084\u0086\3\2\2\2\u0085\u0083\3\2\2\2\u0086\u0087\7)\2\2\u0087"+
		"&\3\2\2\2\u0088\u008a\t\5\2\2\u0089\u0088\3\2\2\2\u008a\u008b\3\2\2\2"+
		"\u008b\u0089\3\2\2\2\u008b\u008c\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008e"+
		"\b\24\2\2\u008e(\3\2\2\2\17\2=CKUansy{\u0081\u0083\u008b\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}