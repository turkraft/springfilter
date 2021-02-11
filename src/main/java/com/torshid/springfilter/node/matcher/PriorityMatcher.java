package com.torshid.springfilter.node.matcher;

import java.util.LinkedList;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.exception.ExpressionExpectedException;
import com.torshid.compiler.exception.OutOfTokenException;
import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.node.INode;
import com.torshid.compiler.node.matcher.Matcher;
import com.torshid.compiler.token.IToken;
import com.torshid.springfilter.node.Priority;
import com.torshid.springfilter.token.Parenthesis;
import com.torshid.springfilter.token.Parenthesis.Type;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class PriorityMatcher extends Matcher<Priority> {

  public static final PriorityMatcher INSTANCE = new PriorityMatcher();

  @Override
  public Priority match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws ParserException {

    if (tokens.indexIs(Parenthesis.class) && ((Parenthesis) tokens.index()).getType() == Type.OPEN) {

      tokens.take();

      Priority priority = Priority.builder().build();

      while (tokens.size() > 0
          && (!tokens.indexIs(Parenthesis.class) || ((Parenthesis) tokens.index()).getType() != Type.CLOSE)) {

        // merging walked expressions to the body until we encounter closing parenthesis

        LinkedList<INode> subNodes = new LinkedList<INode>();

        if (priority.getBody() != null) {
          subNodes.add(priority.getBody());
        }

        try {

          priority.setBody(ExpressionMatcher.INSTANCE.match(tokens, subNodes));

        } catch (ParserException ex) {
          throw new ExpressionExpectedException("Expression is expected inside parentheses", ex);
        }

      }

      if (tokens.size() == 0) {
        throw new OutOfTokenException("Closing parenthesis not found");
      }

      if (priority.getBody() == null) {
        throw new ExpressionExpectedException("Expression expected inside parentheses");
      }

      tokens.take();

      return priority;

    }

    return null;

  }

}
