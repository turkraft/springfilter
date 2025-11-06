package com.turkraft.springfilter;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.InvalidSyntaxException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    Exception exception = assertThrows(Exception.class, () -> {
      filterParser.parse(input);
    });
    assertInstanceOf(InvalidSyntaxException.class, exception, "Expected InvalidSyntaxException but got " + exception
        .getClass()
        .getName() + ": " + exception.getMessage());
  }

  @Test
  void testUnterminatedStringSimple() {
    String input = "firstName ~ 'Bob";
    Exception exception = assertThrows(Exception.class, () -> {
      filterParser.parse(input);
    });
    assertInstanceOf(InvalidSyntaxException.class, exception, "Expected InvalidSyntaxException but got " + exception
        .getClass()
        .getName() + ": " + exception.getMessage());
  }

  @Test
  void testUnterminatedStringInCollection() {
    String input = "status in ['Submitted', 'Pending]";
    Exception exception = assertThrows(Exception.class, () -> {
      filterParser.parse(input);
    });
    assertInstanceOf(InvalidSyntaxException.class, exception, "Expected InvalidSyntaxException but got " + exception
        .getClass()
        .getName() + ": " + exception.getMessage());
  }

}
