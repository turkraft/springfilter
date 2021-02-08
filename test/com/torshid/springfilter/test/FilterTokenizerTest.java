package com.torshid.springfilter.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.torshid.compiler.exception.TokenizerException;
import com.torshid.compiler.token.Token;
import com.torshid.springfilter.FilterTokenizer;
import com.torshid.springfilter.token.Comparator;
import com.torshid.springfilter.token.Field;
import com.torshid.springfilter.token.Operator;
import com.torshid.springfilter.token.Parenthesis;
import com.torshid.springfilter.token.Parenthesis.Type;
import com.torshid.springfilter.token.input.Bool;
import com.torshid.springfilter.token.input.Numeral;
import com.torshid.springfilter.token.input.Text;
import com.torshid.springfilter.token.input.Time;


class FilterTokenizerTest {

  private void validateSingleToken(List<Token> tokens, Class<? extends Token> klass) {
    assertEquals("Only one token should be present", 1, tokens.size());
    assertThat("The token should be a " + klass.getSimpleName() + ", is a " + tokens.get(0).getClass().getSimpleName(),
        tokens.get(0).getClass().equals(klass));
  }

  @ParameterizedTest
  @ValueSource(strings = {"A", "a", "abcA123", "a.b.c", "a.b2.cC0943a.l"})
  void validateFields(String input) throws TokenizerException {
    validateSingleToken(FilterTokenizer.tokenize(input), Field.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"''", "'hello'", "' hello !!! world' "})
  void validateTexts(String input) throws TokenizerException {
    validateSingleToken(FilterTokenizer.tokenize(input), Text.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {" ", "       "})
  void ignoreSpaces(String input) throws TokenizerException {
    assertThat("No token should be present", FilterTokenizer.tokenize(input).isEmpty());
  }

  @Test
  void validateParenthesisStart() throws TokenizerException {
    List<Token> tokens = FilterTokenizer.tokenize("(");
    validateSingleToken(tokens, Parenthesis.class);
    assertThat("Parenthesis is of open type", ((Parenthesis) tokens.get(0)).getType() == Type.OPEN);
  }

  @Test
  void validateParenthesisEnd() throws TokenizerException {
    List<Token> tokens = FilterTokenizer.tokenize(")");
    validateSingleToken(tokens, Parenthesis.class);
    assertThat("Parenthesis is of close type", ((Parenthesis) tokens.get(0)).getType() == Type.CLOSE);
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "1", "123456", "1.4324", "4.43243", ".6399"})
  void validateNumerals(String input) throws TokenizerException {
    validateSingleToken(FilterTokenizer.tokenize(input), Numeral.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"07/10/2000"})
  void validateTimes(String input) throws TokenizerException {
    validateSingleToken(FilterTokenizer.tokenize(input), Time.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"true", "True", "TRUE"})
  void validateBoolTrue(String input) throws TokenizerException {
    List<Token> tokens = FilterTokenizer.tokenize(input);
    validateSingleToken(tokens, Bool.class);
    assertThat("Bool is true", ((Bool) tokens.get(0)).getValue());
  }

  @ParameterizedTest
  @ValueSource(strings = {"false", "False", "FALSE"})
  void validateBoolFalse(String input) throws TokenizerException {
    List<Token> tokens = FilterTokenizer.tokenize(input);
    validateSingleToken(tokens, Bool.class);
    assertThat("Bool is false", !((Bool) tokens.get(0)).getValue());
  }

  @ParameterizedTest
  @ValueSource(strings = {"=", "!", "~", "<", "<=", ">", ">="})
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
    List<Token> tokens = FilterTokenizer.tokenize("entity.id = 55 OR (status ! 'active' And hello.world = nULL) ");
    System.out.println(tokens);
  }

}
