package com.turkraft.springfilter.node.predicate;

import java.util.LinkedList;

import com.turkraft.springfilter.FilterParser;
import com.turkraft.springfilter.compiler.Extensions;
import com.turkraft.springfilter.compiler.exception.InputExpected;
import com.turkraft.springfilter.compiler.node.INode;
import com.turkraft.springfilter.compiler.node.Matcher;
import com.turkraft.springfilter.compiler.token.IToken;
import com.turkraft.springfilter.node.IExpression;
import com.turkraft.springfilter.token.Comparator;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class ConditionMatcher extends Matcher<IExpression> {

  // a condition might also return an operation, that's why the generic parameter is IExpression

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

        IExpression right = FilterParser.run(tokens, nodes);

        if (right == null) {
          return null;
        }

        ConditionInfix conditionInfix = ConditionInfix.builder().left(left).comparator(comparator).right(right).build();

        // if the right node is an infix operation, a swap should be done since a condition has higher priority

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
