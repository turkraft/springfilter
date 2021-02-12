package com.springfilter.compiler.springfilter.node.predicate;

import java.util.LinkedList;

import com.springfilter.compiler.compiler.Extensions;
import com.springfilter.compiler.compiler.exception.ExpressionExpectedException;
import com.springfilter.compiler.compiler.exception.OutOfTokenException;
import com.springfilter.compiler.compiler.exception.ParserException;
import com.springfilter.compiler.compiler.node.INode;
import com.springfilter.compiler.compiler.node.Matcher;
import com.springfilter.compiler.compiler.token.IToken;
import com.springfilter.compiler.springfilter.node.IPredicate;
import com.springfilter.compiler.springfilter.token.Parenthesis;
import com.springfilter.compiler.springfilter.token.Parenthesis.Type;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class PriorityMatcher extends Matcher<Priority> {

  public static final PriorityMatcher INSTANCE = new PriorityMatcher();

  @Override
  public Priority match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws ParserException {

    if (tokens.indexIs(Parenthesis.class) && ((Parenthesis) tokens.index()).getType() == Type.OPEN) {

      tokens.take();

      Priority priority = Priority.builder().build();

      //      LinkedList<INode> subNodes = new LinkedList<INode>();

      while (tokens.size() > 0
          && (!tokens.indexIs(Parenthesis.class) || ((Parenthesis) tokens.index()).getType() != Type.CLOSE)) {
        nodes.add(PredicateMatcher.INSTANCE.match(tokens, nodes));
      }

      if (tokens.size() == 0) {
        throw new OutOfTokenException("Closing parenthesis not found");
      }

      if (nodes.size() != 1 || nodes.index() == null) {
        throw new ExpressionExpectedException("Expression expected inside parentheses");
      }

      priority.setBody((IPredicate) nodes.take());

      tokens.take();

      return priority;

    }

    return null;

  }

}
