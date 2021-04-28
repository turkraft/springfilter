package com.turkraft.springfilter.compiler.node.predicate;

import java.util.LinkedList;
import com.turkraft.springfilter.compiler.Parser;
import com.turkraft.springfilter.compiler.Extensions;
import com.turkraft.springfilter.compiler.node.ArgumentsMatcher;
import com.turkraft.springfilter.compiler.node.IExpression;
import com.turkraft.springfilter.compiler.node.Matcher;
import com.turkraft.springfilter.compiler.token.Comparator;
import com.turkraft.springfilter.compiler.token.IToken;
import com.turkraft.springfilter.exception.InputExpected;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class ConditionMatcher extends Matcher<IExpression> {

  // a condition might also return an operation, that's why the generic parameter is IExpression

  public static final ConditionMatcher INSTANCE = new ConditionMatcher();

  @Override
  public IExpression match(LinkedList<IToken> tokens, LinkedList<IExpression> nodes)
      throws InputExpected {

    if (!nodes.lastIs(IExpression.class)) {
      return null;
    }

    IExpression left = nodes.pollLast();

    if (tokens.indexIs(Comparator.class)) {

      Comparator comparator = (Comparator) tokens.take();

      if (comparator.needsInput()) {

        IExpression right =
            comparator == Comparator.IN ? ArgumentsMatcher.INSTANCE.match(tokens, nodes)
                : Parser.run(tokens, nodes);

        if (right == null) {
          return null;
        }

        ConditionInfix conditionInfix =
            ConditionInfix.builder().left(left).comparator(comparator).right(right).build();

        // if the right node is an infix operation, a swap should be done since a condition has
        // higher priority

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
