package com.turkraft.springfilter.node.predicate;

import java.util.LinkedList;

import com.turkraft.springfilter.FilterParser;
import com.turkraft.springfilter.compiler.Extensions;
import com.turkraft.springfilter.compiler.node.INode;
import com.turkraft.springfilter.compiler.node.Matcher;
import com.turkraft.springfilter.compiler.token.IToken;
import com.turkraft.springfilter.node.IExpression;
import com.turkraft.springfilter.token.Operator;
import com.turkraft.springfilter.token.Operator.Position;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class OperationMatcher extends Matcher<Operation> {

  public static final OperationMatcher INSTANCE = new OperationMatcher();

  @Override
  public Operation match(LinkedList<IToken> tokens, LinkedList<INode> nodes) {

    if (!tokens.indexIs(Operator.class)) {
      return null;
    }

    Operator operator = ((Operator) tokens.take());

    IExpression right = FilterParser.run(tokens, nodes);

    if (right == null || !(right instanceof IExpression)) {
      return null;
    }

    if (operator.getPosition() == Position.PREFIX) {

      // regular prefix operation, such as 'NOT x'

      if (right != null && right instanceof IExpression) {
        return OperationPrefix.builder().type(operator).right(right).build();
      }

    }

    // infix

    if (!nodes.lastIs(IExpression.class)) {
      return null;
    }

    IExpression left = (IExpression) nodes.pollLast();

    // if the previous node is an infix operation which has lower priority than the current one, then a swap should be done
    // example: 'x OR y AND z' => 'x OR (y AND z)'

    if (left instanceof OperationInfix) {

      OperationInfix previousOperation = ((OperationInfix) left);

      if (operator.getPriority() > previousOperation.getType().getPriority()) {

        OperationInfix and =
            OperationInfix.builder().type(operator).left(previousOperation.getRight()).right(right).build();
        previousOperation.setRight(and);
        return previousOperation;

      }

    }

    return OperationInfix.builder().left(left).type(operator).right(right).build();

  }

}
