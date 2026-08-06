package com.turkraft.springfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.InvalidSyntaxException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class InvalidSyntaxTest {

  @Configuration
  @ComponentScan("com.turkraft.springfilter")
  static class Config {

  }

  @Autowired
  private FilterParser filterParser;

  @Test
  void testUnterminatedString() {
    String input = "status in ['Submitted', 'Completed', 'Pending'] and firstName ~ 'Bob";
    InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> {
      filterParser.parse(input);
    });
    assertNotNull(exception.getMessage());
    assertEquals(input, exception.getInput());
  }

  @Test
  void testUnterminatedStringSimple() {
    String input = "firstName ~ 'Bob";
    InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> {
      filterParser.parse(input);
    });
    assertNotNull(exception.getMessage());
    assertEquals(input, exception.getInput());
  }

  @Test
  void testUnterminatedStringInCollection() {
    String input = "status in ['Submitted', 'Pending]";
    InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> {
      filterParser.parse(input);
    });
    assertNotNull(exception.getMessage());
    assertEquals(input, exception.getInput());
  }

  @Test
  void testExceptionHasInputSet() {
    String input = "firstName ~ 'Bob";
    InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> {
      filterParser.parse(input);
    });
    assertEquals(input, exception.getInput());
    assertNotNull(exception.getMessage());
  }

  @Test
  void testExceptionHasPositionFields() {
    String input = "firstName ~ 'invalid";
    InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> {
      filterParser.parse(input);
    });
    assertNotNull(exception.getMessage());
    assertEquals(input, exception.getInput());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "name :",
      "x >",
      "(",
      "name : : 'value'",
      "name in [1,",
      ") x : 1"
  })
  void testVariousInvalidSyntaxPreservesInputAndMessage(String input) {
    InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> {
      filterParser.parse(input);
    });
    assertNotNull(exception.getMessage());
    assertEquals(input, exception.getInput());
  }

  @Test
  void testExceptionHasCause() {
    String input = "name :";
    InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> {
      filterParser.parse(input);
    });
    assertNotNull(exception.getCause());
  }

  @Test
  void testLineAndColumnExposed() {
    InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> {
      filterParser.parse("firstName ~ 'Bob");
    });
    assertNotNull(exception.getInput());
    assertNotNull(exception.getMessage());
  }

  @Test
  void testExceptionPreservesInputForIncompleteExpression() {
    String input = "x >";
    InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> {
      filterParser.parse(input);
    });
    assertEquals(input, exception.getInput());
  }

  @Test
  void testParserErrorHasCorrectPosition() {
    String input = "'";
    InvalidSyntaxException exception = assertThrows(InvalidSyntaxException.class, () -> {
      filterParser.parse(input);
    });
    assertEquals(input, exception.getInput());
    assertNotNull(exception.getMessage());
    assertNotNull(exception.getCause());
  }

}
