package com.springfilter.node.predicate;

import java.util.LinkedList;

import com.springfilter.compiler.Extensions;
import com.springfilter.compiler.exception.ExpressionExpectedException;
import com.springfilter.compiler.exception.ParserException;
import com.springfilter.compiler.node.INode;
import com.springfilter.compiler.node.Matcher;
import com.springfilter.compiler.token.IToken;
import com.springfilter.node.IPredicate;
import com.springfilter.token.Operator;
import com.springfilter.token.Operator.Position;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class OperationMatcher extends Matcher<Operation> {

  public static final OperationMatcher INSTANCE = new OperationMatcher();

  @Override
  public Operation match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws ParserException {

    if (tokens.indexIs(Operator.class)) {

      Operator operator = ((Operator) tokens.take());

      if (operator.getPosition() == Position.PREFIX) {

        // regular prefix operation, such as 'NOT x'

        try {

          return OperationPrefix.builder().type(operator).right(PredicateMatcher.INSTANCE.match(tokens, nodes)).build();

        } catch (ParserException ex) {
          throw new ExpressionExpectedException(
              "An expression is expected after the prefix operator " + operator.getLiteral(), ex);
        }

      }

      if (nodes.peekLast() == null) {
        throw new ExpressionExpectedException(
            "An expression is expected before the infix operator " + operator.getLiteral());
      }

      if (nodes.lastIs(OperationInfix.class)) {

        OperationInfix previousOperation = ((OperationInfix) nodes.getLast());

        if (operator.getPriority() > previousOperation.getType().getPriority()) {

          // if the previous node is an infix operation which has lower priority than the current one, then a swap should be done
          // example: 'x OR y AND z' => 'x OR (y AND z)'

          nodes.pollLast(); // remove previous operation, it will be reinserted later

          try {

            OperationInfix and = OperationInfix.builder().type(operator).left(previousOperation.getRight())
                .right(PredicateMatcher.INSTANCE.match(tokens, nodes)).build();

            previousOperation.setRight(and);

            return previousOperation; // reinsert previous op, this is done otherwise we would get an exception if we returned null

          } catch (ParserException ex) {
            throw new ExpressionExpectedException(
                "An expression is expected after the infix operator " + operator.getLiteral(), ex);
          }

        }

      }

      try {

        // regular infix operation such as 'x OR y'

        return OperationInfix.builder().left((IPredicate) nodes.pollLast()).type(operator)
            .right(PredicateMatcher.INSTANCE.match(tokens, nodes)).build();

      } catch (ParserException ex) {
        throw new ExpressionExpectedException(
            "An expression is expected after the infix operator " + operator.getLiteral(), ex);
      }
    }

    return null;

  }

}
