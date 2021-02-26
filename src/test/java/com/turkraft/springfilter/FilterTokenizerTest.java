package com.turkraft.springfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.turkraft.springfilter.FilterTokenizer;
import com.turkraft.springfilter.token.Comma;
import com.turkraft.springfilter.token.Comparator;
import com.turkraft.springfilter.token.Dot;
import com.turkraft.springfilter.token.IToken;
import com.turkraft.springfilter.token.Operator;
import com.turkraft.springfilter.token.Parenthesis;
import com.turkraft.springfilter.token.Parenthesis.Type;
import com.turkraft.springfilter.token.Word;
import com.turkraft.springfilter.token.input.Bool;
import com.turkraft.springfilter.token.input.Numeral;
import com.turkraft.springfilter.token.input.Text;


class FilterTokenizerTest {

  void validateSingleToken(List<IToken> tokens, Class<? extends IToken> klass) {
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
  @ValueSource(strings = {","})
  void validateComma(String input) {
    validateSingleToken(FilterTokenizer.tokenize(input), Comma.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"Hello", "world", "doing", "some", "tests", "a_b_1"})
  void validateWords(String input) {
    validateSingleToken(FilterTokenizer.tokenize(input), Word.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {":", "!", "~", "<", "<:", ">", ">:", "is null", "is not null", "is empty",
      "is not empty"}) // TODO: add 'in'
  void validateComparators(String input) {
    validateSingleToken(FilterTokenizer.tokenize(input), Comparator.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"and ", "or ", "not "})
  void validateLogics(String input) {
    validateSingleToken(FilterTokenizer.tokenize(input), Operator.class);
  }

}
