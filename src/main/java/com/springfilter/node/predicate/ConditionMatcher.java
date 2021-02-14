package com.springfilter.node.predicate;

import java.util.LinkedList;

import com.springfilter.compiler.Extensions;
import com.springfilter.compiler.exception.InputExpected;
import com.springfilter.compiler.node.INode;
import com.springfilter.compiler.node.Matcher;
import com.springfilter.compiler.token.IToken;
import com.springfilter.node.IExpression;
import com.springfilter.node.MatcherUtils;
import com.springfilter.token.Comparator;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class ConditionMatcher extends Matcher<IExpression> {

  public static final ConditionMatcher INSTANCE = new ConditionMatcher();

  @Override
  public IExpression match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws InputExpected {

    if (!nodes.lastIs(IExpression.class)) {
      return null;
    }

    IExpression left = (IExpression) nodes.pollLast();

    if (tokens.indexIs(Comparator.class)) {

      Comparator comparator = (Comparator) tokens.take();

      if (comparator.needsInput()) {

        IExpression right = MatcherUtils.run(tokens, nodes);

        if (right == null) {
          return null;
        }

        ConditionInfix conditionInfix = ConditionInfix.builder().left(left).comparator(comparator).right(right).build();

        // if the right node is an infix operation, a swap should be made since a condition has higher priority

        if (right instanceof OperationInfix) {
          OperationInfix rightOperation = (OperationInfix) right;
          conditionInfix.setRight(rightOperation.getLeft());
          rightOperation.setLeft(conditionInfix);
          return rightOperation;
        }

        return conditionInfix;

      }

      return ConditionPostfix.builder().left(left).comparator(comparator).build();

    }

    return null;

  }

}
