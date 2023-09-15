package com.turkraft.springfilter;

import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.language.EqualOperator;
import com.turkraft.springfilter.language.NotEqualOperator;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.ParseContextImpl;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class NodeMapperTest {

  @Configuration
  @ComponentScan("com.turkraft.springfilter")
  static class Config {

  }

  @Autowired
  private FilterParser filterParser;

  @Autowired
  private FilterStringConverter filterStringConverter;

  @Autowired
  private EqualOperator equalOperator;

  @Autowired
  private NotEqualOperator notEqualOperator;

  @Test
  void test() {

    ParseContextImpl ctx = new ParseContextImpl();
    ctx.setNodeMapper(
        (FilterNode node) -> {
          if (node instanceof InfixOperationNode op) {
            if (op.getOperator() == equalOperator) {
              return new InfixOperationNode(op.getLeft(), notEqualOperator, op.getRight());
            } else if (op.getOperator() == notEqualOperator) {
              return new InfixOperationNode(op.getLeft(), equalOperator, op.getRight());
            }
          }
          return null;
        });

    FilterNode node = filterParser.parse("x : y ! z", ctx);

    Assertions.assertEquals("x ! y : z", filterStringConverter.convert(node));

  }

}
