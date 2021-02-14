package com.springfilter.node.predicate;

import java.util.LinkedList;

import com.springfilter.FilterParser;
import com.springfilter.compiler.Extensions;
import com.springfilter.compiler.node.INode;
import com.springfilter.compiler.node.Matcher;
import com.springfilter.compiler.token.IToken;
import com.springfilter.node.IPredicate;
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

      while (tokens.size() > 0
          && (!tokens.indexIs(Parenthesis.class) || ((Parenthesis) tokens.index()).getType() != Type.CLOSE)) {
        INode node = FilterParser.walk(IPredicate.class, tokens, subNodes, false);
        if (node == null) {
          break;
        }
        subNodes.add(node);
      }

      //      if (!tokens.indexIs(Parenthesis.class) || ((Parenthesis) tokens.index()).getType() != Type.CLOSE) {
      //        throw new OutOfTokenException("Closing parenthesis not found");
      //      }

      if (tokens.size() == 0) {
        throw new RuntimeException("blabla");
      }

      tokens.take();

      if (subNodes.size() != 1) {
        throw new RuntimeException("aaa");
      }

      return Priority.builder().body((IPredicate) subNodes.take()).build();

    }

    return null;

  }

}
