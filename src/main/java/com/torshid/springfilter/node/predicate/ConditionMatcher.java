package com.torshid.springfilter.node.predicate;

import java.util.LinkedList;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.exception.InputExpected;
import com.torshid.compiler.exception.OutOfTokenException;
import com.torshid.compiler.node.INode;
import com.torshid.compiler.node.Matcher;
import com.torshid.compiler.token.IToken;
import com.torshid.springfilter.node.IExpression;
import com.torshid.springfilter.node.ExpressionMatcher;
import com.torshid.springfilter.token.Comparator;

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

      tokens.replaceWith(backup);

    }

    return null;

  }

}
