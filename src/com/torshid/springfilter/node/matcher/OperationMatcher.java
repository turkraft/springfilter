package com.torshid.springfilter.node.matcher;

import java.util.LinkedList;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.exception.ExpressionExpectedException;
import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.node.Node;
import com.torshid.compiler.node.matcher.Matcher;
import com.torshid.compiler.token.Token;
import com.torshid.springfilter.node.Expression;
import com.torshid.springfilter.node.Operation;
import com.torshid.springfilter.node.OperationInfix;
import com.torshid.springfilter.node.OperationPrefix;
import com.torshid.springfilter.token.Operator;
import com.torshid.springfilter.token.Operator.Position;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class OperationMatcher extends Matcher<Operation> {

  public static final OperationMatcher INSTANCE = new OperationMatcher();

  @Override
  public Operation match(LinkedList<Token> tokens, LinkedList<Node> nodes) throws ParserException {

    if (tokens.indexIs(Operator.class)) {

      Operator operator = ((Operator) tokens.take());

      if (operator.getType().getPosition() == Position.PREFIX) {

        // regular prefix operation, such as 'NOT x'

        try {

          return OperationPrefix.builder().type(operator.getType())
              .right(ExpressionMatcher.INSTANCE.match(tokens, nodes)).build();

        } catch (ParserException ex) {
          throw new ExpressionExpectedException(
              "An expression is expected after the prefix operator " + operator.getType().getLiteral(), ex);
        }

      }

      if (nodes.peekLast() == null) {
        throw new ExpressionExpectedException(
            "An expression is expected before the infix operator " + operator.getType().getLiteral());
      }

      if (nodes.lastIs(OperationInfix.class)) {

        OperationInfix previousOperation = ((OperationInfix) nodes.getLast());

        if (operator.getType().getPriority() > previousOperation.getType().getPriority()) {

          // if the previous node is an infix operation which has lower priority than the current one, then a swap should be done
          // example: 'x OR y AND z' => 'x OR (y AND z)'

          nodes.pollLast(); // remove previous operation, it will be reinserted later

          try {

            OperationInfix and = OperationInfix.builder().type(operator.getType()).left(previousOperation.getRight())
                .right(ExpressionMatcher.INSTANCE.match(tokens, nodes)).build();

            previousOperation.setRight(and);

            return previousOperation; // reinsert previous op, this is done otherwise we would get an exception if we returned null

          } catch (ParserException ex) {
            throw new ExpressionExpectedException(
                "An expression is expected after the infix operator " + operator.getType().getLiteral(), ex);
          }

        }

      }

      try {

        // regular infix operation such as 'x OR y'

        return OperationInfix.builder().left((Expression) nodes.pollLast()).type(operator.getType())
            .right(ExpressionMatcher.INSTANCE.match(tokens, nodes)).build();

      } catch (ParserException ex) {
        throw new ExpressionExpectedException(
            "An expression is expected after the infix operator " + operator.getType().getLiteral(), ex);
      }
    }

    return null;

  }

}
