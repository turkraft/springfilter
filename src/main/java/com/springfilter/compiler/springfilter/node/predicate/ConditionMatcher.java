package com.springfilter.compiler.springfilter.node.predicate;

import java.util.LinkedList;

import com.springfilter.compiler.compiler.Extensions;
import com.springfilter.compiler.compiler.exception.InputExpected;
import com.springfilter.compiler.compiler.exception.OutOfTokenException;
import com.springfilter.compiler.compiler.node.INode;
import com.springfilter.compiler.compiler.node.Matcher;
import com.springfilter.compiler.compiler.token.IToken;
import com.springfilter.compiler.springfilter.node.ExpressionMatcher;
import com.springfilter.compiler.springfilter.node.IExpression;
import com.springfilter.compiler.springfilter.token.Comparator;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class ConditionMatcher extends Matcher<Condition> {

  public static final ConditionMatcher INSTANCE = new ConditionMatcher();

  @Override
  public Condition match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws InputExpected {

    LinkedList<IToken> backup = tokens.copy();

    IExpression<?> left = ExpressionMatcher.INSTANCE.match(tokens, nodes);

    if (left != null) {

      if (tokens.indexIs(Comparator.class)) {

        Comparator comparator = (Comparator) tokens.take();

        if (comparator.needsInput()) {

          IExpression<?> right = ExpressionMatcher.INSTANCE.match(tokens, nodes);

          if (right != null) {
            return ConditionInfix.builder().left(left).comparator(comparator).right(right).build();
          }

          throw new OutOfTokenException("The comparator " + comparator.getLiteral() + " expects an expression");

        }

        return ConditionPostfix.builder().left(left).comparator(comparator).build();

      }

    }

    tokens.replaceWith(backup);

    return null;

  }

}
