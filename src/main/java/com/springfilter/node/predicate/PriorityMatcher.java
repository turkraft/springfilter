package com.springfilter.node.predicate;

import java.util.LinkedList;

import com.springfilter.compiler.Extensions;
import com.springfilter.compiler.exception.OutOfTokenException;
import com.springfilter.compiler.exception.ParserException;
import com.springfilter.compiler.node.INode;
import com.springfilter.compiler.node.Matcher;
import com.springfilter.compiler.token.IToken;
import com.springfilter.node.IPredicate;
import com.springfilter.node.MatcherUtils;
import com.springfilter.token.Parenthesis;
import com.springfilter.token.Parenthesis.Type;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class PriorityMatcher extends Matcher<Priority> {

  public static final PriorityMatcher INSTANCE = new PriorityMatcher();

  @Override
  public Priority match(LinkedList<Matcher<?>> matchers, LinkedList<IToken> tokens, LinkedList<INode> nodes)
      throws ParserException {

    if (tokens.indexIs(Parenthesis.class) && ((Parenthesis) tokens.index()).getType() == Type.OPEN) {

      tokens.take();

      Priority priority =
          Priority.builder().body((IPredicate) MatcherUtils.getNextExpression("Expression expected inside parentheses",
              n -> tokens.size() > 0, matchers, tokens)).build();

      if (tokens.size() == 0) {
        throw new OutOfTokenException("Closing parenthesis not found");
      }

      tokens.take();

      return priority;

    }

    return null;

  }

}
