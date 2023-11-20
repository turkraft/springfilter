grammar AntlrFilter;

@header {
    import java.util.*;
    import com.turkraft.springfilter.definition.*;
}

@lexer::members {

    private FilterOperators operators;
   
    public AntlrFilterLexer(CharStream input, FilterOperators operators) {
      this(input);
      this.operators = operators;
    }

    boolean tryOperator(Class<? extends FilterOperator> targetClass) {
      for (Pair<FilterOperator, String> operatorWithToken : operators.getSortedOperators()) {
        boolean matched = true;
        for (int i = 0; i < operatorWithToken.b.length(); i++) {
          if (Character.toLowerCase((char)_input.LA(i + 1)) != operatorWithToken.b.charAt(i)) {
            matched = false;
            break;
          }
        }
        if (matched) {
          if (!targetClass.isAssignableFrom(operatorWithToken.a.getClass())) {
            return false;
          }
          _input.seek(_input.index() + operatorWithToken.b.length() - 1);
          return true;
        }
      }
      return false;
    }

    private Deque<Token> deque = new LinkedList<Token>();
   
    private Token previousToken;
    private Token nextToken;
   
    @Override
    public Token nextToken() {
      if (!deque.isEmpty()) {
        return previousToken = deque.pollFirst();
      }
      Token next = super.nextToken();
      if (next.getType() != SYMBOL) {
        return previousToken = next;
      }
      StringBuilder builder = new StringBuilder();
      if (next.getType() == SYMBOL) {
        builder.append(next.getText());
        next = super.nextToken();
      }
      deque.addLast(nextToken = next);
      List<Token> tokens = findOperatorCombination(builder.toString(), getOperatorType());
      for (int i = tokens.size() - 1; i >= 0; i--) {
        deque.addFirst(tokens.get(i));
      }
      return deque.pollFirst();
    }
   
    private List<Token> findOperatorCombination(String sequence, OperatorType type) {
      switch (type) {
      case POSTFIX_OPERATOR:
        return getPostfixCombination(sequence);
      case PREFIX_OPERATOR:
        return getPrefixCombination(sequence);
      case INFIX_OPERATOR:
        return getInfixCombination(sequence);
      default:
        return null;
      }
    }
   
    private List<Token> getPrefixCombination(String sequence) {
      if (isPrefixOperator(sequence)) {
        List<Token> seq = new ArrayList<Token> (1);
        seq.add(0, new CommonToken(PREFIX_OPERATOR, sequence));
        return seq;
      }
      if (sequence.length() <= 1) {
        return null;
      }
      for (int i = 1; i < sequence.length(); i++) {
        List<Token> seq1 = getPrefixCombination(sequence.substring(0, i));
        List<Token> seq2 = getPrefixCombination(sequence.substring(i, sequence.length()));
        if (seq1 != null & seq2 != null) {
          seq1.addAll(seq2);
          return seq1;
        }
      }
      return null;
    }
   
    private List<Token> getPostfixCombination(String sequence) {
      if (isPostfixOperator(sequence)) {
        List<Token> seq = new ArrayList<Token>(1);
        seq.add(0, new CommonToken(POSTFIX_OPERATOR, sequence));
        return seq;
      }
      if (sequence.length() <= 1) {
        return null;
      }
      for (int i = 1; i < sequence.length(); i++) {
        List<Token> seq1 = getPostfixCombination(sequence.substring(0, i));
        List<Token> seq2 = getPostfixCombination(sequence.substring(i, sequence.length()));
        if (seq1 != null && seq2 != null) {
          seq1.addAll(seq2);
          return seq1;
        }
      }
      return null;
    }
   
    private List<Token> getInfixCombination(String sequence) {
      for (int i = 0; i < sequence.length(); i++) {
        for (int j = 0; j < sequence.length() - i; j++) {
          String seqPost = sequence.substring(0, i);
          List<Token> post = getPostfixCombination(seqPost);
          String seqPre = sequence.substring(sequence.length() - j, sequence.length());
          List<Token> pre = getPrefixCombination(seqPre);
          String seqIn = sequence.substring(i, sequence.length() - j);
          if ((post != null || seqPost.isEmpty()) &&
            (pre != null || seqPre.isEmpty()) &&
            isInfixOperator(seqIn)) {
            List<Token> res = new ArrayList<Token> ();
            if (post != null)
              res.addAll(post);
            res.add(new CommonToken(INFIX_OPERATOR, seqIn));
            if (pre != null)
              res.addAll(pre);
            return res;
          }
        }
      }
      return null;
    }
   
    private OperatorType getOperatorType() {
      if (isAfterAtom()) {
        if (isBeforeAtom()) {
          return OperatorType.INFIX_OPERATOR;
        }
        return OperatorType.POSTFIX_OPERATOR;
      }
      return OperatorType.PREFIX_OPERATOR;
    }
   
    private enum OperatorType {
      PREFIX_OPERATOR,
      INFIX_OPERATOR,
      POSTFIX_OPERATOR
    };
   
    private boolean isBeforeAtom() {
      int type = nextToken.getType();
      return
           type == AntlrFilterParser.NUMBER || type == AntlrFilterParser.ID
        || type == AntlrFilterParser.STRING || type == AntlrFilterParser.TRUE
        || type == AntlrFilterParser.FALSE || type == AntlrFilterParser.LPAREN;
    }
   
    private boolean isAfterAtom() {
      int type = previousToken.getType();
      return
           type == AntlrFilterParser.NUMBER || type == AntlrFilterParser.ID
        || type == AntlrFilterParser.STRING || type == AntlrFilterParser.TRUE
        || type == AntlrFilterParser.FALSE || type == AntlrFilterParser.RPAREN;
    }
   
    private boolean isPrefixOperator(String operator) {
      try {
        operators.getPrefixOperator(operator);
        return true;
      } catch (Exception ignore) {
        return false;
      }
    }
   
    private boolean isInfixOperator(String operator) {
      try {
        operators.getInfixOperator(operator);
        return true;
      } catch (Exception ignore) {
        return false;
      }
    }
    
    private boolean isPostfixOperator(String operator) {
      try {
        operators.getPostfixOperator(operator);
        return true;
      } catch (Exception ignore) {
        return false;
      }
    }

}

@parser::members {
    private FilterOperators operators;

    public AntlrFilterParser(TokenStream input, FilterOperators operators) {
      this(input);
      this.operators = operators;
    }

    public Integer getPrecedence(Token op) {
      if (op.getType() == PREFIX_OPERATOR) {
        return operators.getPrefixOperator(op.getText()).getPriority();
      } else if (op.getType() == INFIX_OPERATOR) {
        return operators.getInfixOperator(op.getText()).getPriority();
      } else if (op.getType() == POSTFIX_OPERATOR) {
        return operators.getPostfixOperator(op.getText()).getPriority();
      }
      throw new IllegalStateException("Unexpected token `" + op.getText() + "`");
    }

    public Integer getNextPrecedence(Token op) {
      Integer p = getPrecedence(op);
      if (op.getType() == PREFIX_OPERATOR) return p;
      else if (op.getType() == POSTFIX_OPERATOR) return p;
      else if (op.getType() == INFIX_OPERATOR) return p + 1;
      throw new IllegalStateException("Unexpected token `" + op.getText() + "`");
    }
}

options {
	contextSuperClass = AntlrBaseContext;
}

filter: expression[0] EOF;

expression [int _p]
    :   atom
        (   {getPrecedence(_input.LT(1)) >= $_p}? op=INFIX_OPERATOR expression[getNextPrecedence($op)]
        |   {getPrecedence(_input.LT(1)) >= $_p}? POSTFIX_OPERATOR
        )*
    ;

atom
    :   ID (DOT ID)*                                                                          # field
    |   (STRING | NUMBER | TRUE | FALSE)                                                      # input
    |   BTICK ID BTICK                                                                        # placeholder
    |   LPAREN expression[0] RPAREN                                                           # priority
    |   op=PREFIX_OPERATOR expression[getNextPrecedence($op)]                                 # prefixExpression
    |   LBRACK ( items += expression[0] (COMMA items += expression[0])* )? RBRACK             # collection
    |   ID LPAREN ( arguments += expression[0] (COMMA arguments += expression[0])* )? RPAREN  # function
    ;

PREFIX_OPERATOR: {tryOperator(FilterPrefixOperator.class)}? . ;
INFIX_OPERATOR: {tryOperator(FilterInfixOperator.class)}? . ;
POSTFIX_OPERATOR: {tryOperator(FilterPostfixOperator.class)}? . ;

TRUE: 'TRUE' | 'true';
FALSE: 'FALSE' | 'false';

DOT: '.';
COMMA: ',';
LPAREN: '(';
RPAREN: ')';
LBRACK: '[';
RBRACK: ']';
BTICK: '`';

ID: [a-zA-Z_$][a-zA-Z_$0-9]*;
NUMBER: '-'? [0-9]+ ('.' [0-9]+)?;
STRING: '\'' (~('\'' | '\\') | '\\' ('\'' | '\\'))* '\'';
WS: [ \t]+ -> skip;

SYMBOL
    : .
    ;
