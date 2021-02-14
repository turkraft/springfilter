package com.springfilter.node.predicate;

import java.util.LinkedList;

import com.springfilter.compiler.Extensions;
import com.springfilter.compiler.exception.OutOfTokenException;
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
  public Priority match(LinkedList<IToken> tokens, LinkedList<INode> nodes) {

    if (tokens.indexIs(Parenthesis.class) && ((Parenthesis) tokens.index()).getType() == Type.OPEN) {

      tokens.take();

      LinkedList<INode> subNodes = new LinkedList<INode>();

      Priority priority = Priority.builder()
          .body((IPredicate) MatcherUtils.run(tokens, subNodes, "The filter should be made of an expression")).build();

      if (!tokens.indexIs(Parenthesis.class) || ((Parenthesis) tokens.index()).getType() != Type.CLOSE) {
        throw new OutOfTokenException("Closing parenthesis not found");
      }

      if (tokens.size() == 0) {
        throw new RuntimeException("blabla");
      }

      tokens.take();

      return priority;

    }

    return null;

  }

}
