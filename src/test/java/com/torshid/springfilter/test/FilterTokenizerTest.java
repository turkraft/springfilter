package com.torshid.springfilter.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.torshid.compiler.exception.TokenizerException;
import com.torshid.compiler.token.IToken;
import com.torshid.springfilter.FilterTokenizer;
import com.torshid.springfilter.token.Comparator;
import com.torshid.springfilter.token.Operator;
import com.torshid.springfilter.token.Parenthesis;
import com.torshid.springfilter.token.Parenthesis.Type;
import com.torshid.springfilter.token.input.Bool;
import com.torshid.springfilter.token.input.Numeral;
import com.torshid.springfilter.token.input.Text;


class FilterTokenizerTest {

  private void validateSingleToken(List<IToken> tokens, Class<? extends IToken> klass) {
    assertEquals(1, tokens.size(), "Only one token should be present");
    assertEquals(tokens.get(0).getClass(), klass,
        "The token should be a " + klass.getSimpleName() + ", is a " + tokens.get(0).getClass().getSimpleName());
  }

  @ParameterizedTest
  @ValueSource(strings = {"''", "'hello'", "' hello !!! world' "})
  void validateTexts(String input) throws TokenizerException {
    validateSingleToken(FilterTokenizer.tokenize(input), Text.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {" ", "       "})
  void ignoreSpaces(String input) throws TokenizerException {
    assertTrue(FilterTokenizer.tokenize(input).isEmpty(), "No token should be present");
  }

  @Test
  void validateParenthesisStart() throws TokenizerException {
    List<IToken> tokens = FilterTokenizer.tokenize("(");
    validateSingleToken(tokens, Parenthesis.class);
    assertTrue(((Parenthesis) tokens.get(0)).getType() == Type.OPEN, "Parenthesis is of open type");
  }

  @Test
  void validateParenthesisEnd() throws TokenizerException {
    List<IToken> tokens = FilterTokenizer.tokenize(")");
    validateSingleToken(tokens, Parenthesis.class);
    assertTrue(((Parenthesis) tokens.get(0)).getType() == Type.CLOSE, "Parenthesis is of close type");
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "1", "123456", "1.4324", "4.43243", ".6399"})
  void validateNumerals(String input) throws TokenizerException {
    validateSingleToken(FilterTokenizer.tokenize(input), Numeral.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"true", "True", "TRUE"})
  void validateBoolTrue(String input) throws TokenizerException {
    List<IToken> tokens = FilterTokenizer.tokenize(input);
    validateSingleToken(tokens, Bool.class);
    assertTrue(((Bool) tokens.get(0)).getValue(), "Bool is true");
  }

  @ParameterizedTest
  @ValueSource(strings = {"false", "False", "FALSE"})
  void validateBoolFalse(String input) throws TokenizerException {
    List<IToken> tokens = FilterTokenizer.tokenize(input);
    validateSingleToken(tokens, Bool.class);
    assertTrue(!((Bool) tokens.get(0)).getValue(), "Bool is false");
  }

  @ParameterizedTest
  @ValueSource(strings = {":", "!", "~", "<", "<:", ">", ">:", "is null", "is not null", "is empty", "is not empty"})
  void validateComparators(String input) throws TokenizerException {
    validateSingleToken(FilterTokenizer.tokenize(input), Comparator.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"and", "AND", "or", "OR"})
  void validateLogics(String input) throws TokenizerException {
    validateSingleToken(FilterTokenizer.tokenize(input), Operator.class);
  }

  @Test
  void test() throws TokenizerException {
    List<IToken> tokens = FilterTokenizer.tokenize("entity.id : 55 OR (status ! 'active' And hello.world : nULL) ");
  }

}
