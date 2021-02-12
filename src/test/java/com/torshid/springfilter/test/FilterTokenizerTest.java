package com.torshid.springfilter.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.springfilter.compiler.compiler.token.IToken;
import com.springfilter.compiler.springfilter.FilterTokenizer;
import com.springfilter.compiler.springfilter.token.Comparator;
import com.springfilter.compiler.springfilter.token.Dot;
import com.springfilter.compiler.springfilter.token.Operator;
import com.springfilter.compiler.springfilter.token.Parenthesis;
import com.springfilter.compiler.springfilter.token.Word;
import com.springfilter.compiler.springfilter.token.Parenthesis.Type;
import com.springfilter.compiler.springfilter.token.input.Bool;
import com.springfilter.compiler.springfilter.token.input.Numeral;
import com.springfilter.compiler.springfilter.token.input.Text;


class FilterTokenizerTest {

  private void validateSingleToken(List<IToken> tokens, Class<? extends IToken> klass) {
    assertEquals(1, tokens.size());
    assertEquals(klass, tokens.get(0).getClass());
  }

  @ParameterizedTest
  @ValueSource(strings = {"''", "'hello'", "' hello !!! world' "})
  void validateTexts(String input) {
    validateSingleToken(FilterTokenizer.tokenize(input), Text.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {" ", "       "})
  void ignoreSpaces(String input) {
    assertTrue(FilterTokenizer.tokenize(input).isEmpty());
  }

  @Test
  void validateParenthesisOpen() {
    List<IToken> tokens = FilterTokenizer.tokenize("(");
    validateSingleToken(tokens, Parenthesis.class);
    assertTrue(((Parenthesis) tokens.get(0)).getType() == Type.OPEN);
  }

  @Test
  void validateParenthesisClose() {
    List<IToken> tokens = FilterTokenizer.tokenize(")");
    validateSingleToken(tokens, Parenthesis.class);
    assertTrue(((Parenthesis) tokens.get(0)).getType() == Type.CLOSE);
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "1", "123456", "1.4324", "4.43243"})
  void validateNumerals(String input) {
    validateSingleToken(FilterTokenizer.tokenize(input), Numeral.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"true", "True", "TRUE"})
  void validateBoolTrue(String input) {
    List<IToken> tokens = FilterTokenizer.tokenize(input);
    validateSingleToken(tokens, Bool.class);
    assertTrue(((Bool) tokens.get(0)).getValue());
  }

  @ParameterizedTest
  @ValueSource(strings = {"false", "False", "FALSE"})
  void validateBoolFalse(String input) {
    List<IToken> tokens = FilterTokenizer.tokenize(input);
    validateSingleToken(tokens, Bool.class);
    assertTrue(!((Bool) tokens.get(0)).getValue());
  }

  @ParameterizedTest
  @ValueSource(strings = {"."})
  void validateDot(String input) {
    validateSingleToken(FilterTokenizer.tokenize(input), Dot.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"hello", "world", "doing", "some", "tests"})
  void validateWords(String input) {
    validateSingleToken(FilterTokenizer.tokenize(input), Word.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {":", "!", "~", "<", "<:", ">", ">:", "is null", "is not null", "is empty", "is not empty"})
  void validateComparators(String input) {
    validateSingleToken(FilterTokenizer.tokenize(input), Comparator.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"and ", "AND ", "or ", "OR "})
  void validateLogics(String input) {
    validateSingleToken(FilterTokenizer.tokenize(input), Operator.class);
  }

}
