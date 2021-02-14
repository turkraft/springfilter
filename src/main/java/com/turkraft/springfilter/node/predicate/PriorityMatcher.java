package com.turkraft.springfilter.node.predicate;

import java.util.LinkedList;

import com.turkraft.springfilter.FilterParser;
import com.turkraft.springfilter.compiler.Extensions;
import com.turkraft.springfilter.compiler.exception.OutOfTokenException;
import com.turkraft.springfilter.compiler.node.INode;
import com.turkraft.springfilter.compiler.node.Matcher;
import com.turkraft.springfilter.compiler.token.IToken;
import com.turkraft.springfilter.node.IExpression;
import com.turkraft.springfilter.token.Parenthesis;
import com.turkraft.springfilter.token.Parenthesis.Type;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class PriorityMatcher extends Matcher<Priority> {

  public static final PriorityMatcher INSTANCE = new PriorityMatcher();

  @Override
  public Priority match(LinkedList<IToken> tokens, LinkedList<INode> nodes) {

    if (tokens.indexIs(Parenthesis.class) && ((Parenthesis) tokens.index()).getType() == Type.OPEN) {

      tokens.take();

      LinkedList<INode> subNodes = new LinkedList<>();

      IExpression body = FilterParser.run(tokens, subNodes);

      if (body == null) {
        return null;
      }

      if (!tokens.indexIs(Parenthesis.class) || ((Parenthesis) tokens.index()).getType() != Type.CLOSE) {
        throw new OutOfTokenException("Closing parenthesis not found");
      }

      tokens.take();

      return Priority.builder().body(body).build();

    }

    return null;

  }

}
