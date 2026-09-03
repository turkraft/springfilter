package com.turkraft.springfilter;

import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.InvalidSyntaxException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.ResponseStatus;

@ExtendWith(SpringExtension.class)
public class ParserHardeningTest {

  @Configuration
  @ComponentScan("com.turkraft.springfilter")
  static class Config {

  }

  @Autowired
  private FilterParser parser;

  @Autowired
  private FilterStringConverter filterStringConverter;

  private String canonical(String filter) {
    return filterStringConverter.convert(parser.parse(filter));
  }

  private String chain(int terms) {
    StringBuilder filter = new StringBuilder("f0 : 0");
    for (int i = 1; i < terms; i++) {
      filter
          .append(" and f")
          .append(i)
          .append(" : ")
          .append(i);
    }
    return filter.toString();
  }

  @Test
  void deeplyNestedParenthesesAreRejectedInsteadOfExhaustingTheStack() {

    String filter = "(".repeat(5000) + "a : 1" + ")".repeat(5000);

    Assertions.assertThrows(InvalidSyntaxException.class, () -> parser.parse(filter));

  }

  @Test
  void deeplyNestedPrefixOperatorsAreRejectedInsteadOfExhaustingTheStack() {

    String filter = "not ".repeat(5000) + "a : 1";

    Assertions.assertThrows(InvalidSyntaxException.class, () -> parser.parse(filter));

  }

  @Test
  void veryLongInfixChainsNeverEscapeAsAnError() {

    String filter = chain(5000);

    try {
      parser.parse(filter);
    } catch (InvalidSyntaxException accepted) {
      return;
    } catch (Throwable other) {
      Assertions.fail("parsing a long flat chain must not escape as " + other
          .getClass()
          .getName(), other);
    }

  }

  @Test
  void nestingPastTheCapIsRejectedBeforeTheStackIsEvenStrained() {

    String filter = "(".repeat(300) + "a : 1" + ")".repeat(300);

    InvalidSyntaxException exception = Assertions.assertThrows(InvalidSyntaxException.class,
        () -> parser.parse(filter));

    Assertions.assertTrue(exception
        .getMessage()
        .contains("nested too deeply"));

  }

  @Test
  void realisticExpressionsStayWellWithinTheNestingLimit() {

    Assertions.assertDoesNotThrow(
        () -> parser.parse("(".repeat(200) + "a : 1" + ")".repeat(200)));

    Assertions.assertDoesNotThrow(() -> parser.parse("not ".repeat(200) + "a : 1"));

    Assertions.assertDoesNotThrow(() -> parser.parse(chain(200)));

    StringBuilder collection = new StringBuilder("status in ['v0'");
    for (int i = 1; i < 2000; i++) {
      collection
          .append(", 'v")
          .append(i)
          .append("'");
    }
    collection.append("]");

    Assertions.assertDoesNotThrow(() -> parser.parse(collection.toString()));

  }

  @Test
  void invalidSyntaxIsReportedAsABadRequest() {

    ResponseStatus responseStatus = InvalidSyntaxException.class.getAnnotation(ResponseStatus.class);

    Assertions.assertNotNull(responseStatus);
    Assertions.assertEquals(400, responseStatus
        .value()
        .value());

  }

  @Test
  void lineBreaksBetweenTokensAreTreatedAsWhitespace() {

    String expected = canonical("name : 'a' and age > 18 and city : 'Paris'");

    Assertions.assertEquals(expected,
        canonical("name : 'a'\nand age > 18\nand city : 'Paris'"));

    Assertions.assertEquals(expected,
        canonical("name : 'a'\r\n  and age > 18\r\n  and city : 'Paris'"));

    Assertions.assertEquals(expected,
        canonical("\n  name : 'a' and age > 18\n  and city : 'Paris'  \n"));

  }

  @Test
  void lineBreaksInsideAStringRemainPartOfItsValue() {

    Assertions.assertEquals("name : 'line1\nline2'", canonical("name : 'line1\nline2'"));

  }

  @Test
  void multiWordOperatorsStillRequireASingleLiteralSpace() {

    Assertions.assertDoesNotThrow(() -> parser.parse("a is not null"));
    Assertions.assertDoesNotThrow(() -> parser.parse("a not in [1]"));

    for (String separator : new String[]{"  ", "\t", "\n"}) {
      Assertions.assertThrows(InvalidSyntaxException.class,
          () -> parser.parse("a is" + separator + "null"));
    }

    Assertions.assertDoesNotThrow(() -> parser.parse("a\nis not null\nand b : 1"));
    Assertions.assertDoesNotThrow(() -> parser.parse("a between 1\nand 5"));

  }

}
