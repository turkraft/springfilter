package com.turkraft.springfilter;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.helper.FieldTypeResolver;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import com.turkraft.springfilter.transformer.processor.factory.FilterNodeProcessorFactories;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
public class LikeJsonNodeFunctionBodyTest {

  @Configuration
  @ComponentScan("com.turkraft.springfilter")
  static class Config {

  }

  @Autowired
  private ConversionService conversionService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private FilterBuilder fb;

  @Autowired
  private FilterNodeProcessorFactories filterNodeProcessorFactories;

  @Autowired
  private FieldTypeResolver fieldTypeResolver;

  private FilterJsonNodeTransformer transformer;

  @BeforeEach
  void init() {
    transformer = new FilterJsonNodeTransformer(conversionService, objectMapper,
        filterNodeProcessorFactories, fieldTypeResolver, TestEntity.class);
  }

  private String functionBody(String pattern) {

    FilterNode filter = fb
        .field("id")
        .like(fb.input(pattern))
        .get();

    return transformer
        .transform(filter)
        .get("$function")
        .get("body")
        .stringValue();
  }

  private String decodedPattern(String pattern) {

    String source = functionBody(pattern);
    int index = source.indexOf("RegExp('") + "RegExp('".length();
    StringBuilder decoded = new StringBuilder();

    while (index < source.length()) {

      char c = source.charAt(index);

      if (c == '\'') {
        Assertions.assertTrue(source
            .substring(index)
            .startsWith("', '').test(id) }"));
        return decoded.toString();
      }

      Assertions.assertNotEquals('\n', c);
      Assertions.assertNotEquals('\r', c);

      if (c == '\\') {
        char next = source.charAt(index + 1);
        decoded.append(switch (next) {
          case 'n' -> '\n';
          case 'r' -> '\r';
          default -> next;
        });
        index += 2;
      } else {
        decoded.append(c);
        index++;
      }

    }

    throw new AssertionError("unterminated JavaScript string literal in: " + source);
  }

  @Test
  void aPlainPatternProducesTheExpectedFunctionBody() {

    Assertions.assertEquals("function(id) { return new RegExp('.*abc.*', '').test(id) }",
        functionBody("abc"));

  }

  @Test
  void aLineBreakInTheValueDoesNotSplitTheJavaScriptStringLiteral() {

    String body = functionBody("ab\ncd");

    Assertions.assertFalse(body.contains("\n"));
    Assertions.assertFalse(body.contains("\r"));
    Assertions.assertTrue(body.contains("\\n"));

    Assertions.assertEquals("function(id) { return new RegExp('.*ab\\ncd.*', '').test(id) }", body);

    Assertions.assertEquals("function(id) { return new RegExp('.*ab\\rcd.*', '').test(id) }",
        functionBody("ab\rcd"));

  }

  @Test
  void quotesAndBackslashesStayEscaped() {

    Assertions.assertEquals("function(id) { return new RegExp('.*ab\\'cd.*', '').test(id) }",
        functionBody("ab'cd"));

    Assertions.assertEquals("function(id) { return new RegExp('.*ab\\\\\\\\cd.*', '').test(id) }",
        functionBody("ab\\cd"));

  }

  @Test
  void theLiteralDecodesBackToTheIntendedPattern() {

    Assertions.assertEquals(".*abc.*", decodedPattern("abc"));
    Assertions.assertEquals(".*ab'cd.*", decodedPattern("ab'cd"));
    Assertions.assertEquals(".*ab\ncd.*", decodedPattern("ab\ncd"));
    Assertions.assertEquals(".*ab\r\ncd.*", decodedPattern("ab\r\ncd"));
    Assertions.assertEquals(".*ab\\\\cd.*", decodedPattern("ab\\cd"));
    Assertions.assertEquals(".*'\\); return true; //.*", decodedPattern("'); return true; //"));
    Assertions.assertEquals(".*ab'c.*", decodedPattern("*ab'c*"));

  }

}
