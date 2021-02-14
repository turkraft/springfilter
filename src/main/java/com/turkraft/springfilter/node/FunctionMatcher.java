package com.turkraft.springfilter.node;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.turkraft.springfilter.FilterParser;
import com.turkraft.springfilter.compiler.Extensions;
import com.turkraft.springfilter.compiler.exception.ParserException;
import com.turkraft.springfilter.compiler.node.INode;
import com.turkraft.springfilter.compiler.node.Matcher;
import com.turkraft.springfilter.compiler.token.IToken;
import com.turkraft.springfilter.token.Comma;
import com.turkraft.springfilter.token.Parenthesis;
import com.turkraft.springfilter.token.Parenthesis.Type;
import com.turkraft.springfilter.token.Word;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class FunctionMatcher extends Matcher<Function> {

  public static final FunctionMatcher INSTANCE = new FunctionMatcher();

  @Override
  public Function match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws ParserException {

    if (tokens.indexIs(Word.class, Parenthesis.class) && ((Parenthesis) tokens.get(1)).getType() == Type.OPEN) {

      String name = ((Word) tokens.take()).getValue();

      tokens.take();

      List<IExpression> arguments = new LinkedList<>();

      while (tokens.size() > 0
          && (!tokens.indexIs(Parenthesis.class) || ((Parenthesis) tokens.index()).getType() != Type.CLOSE)) {

        LinkedList<INode> subNodes = new LinkedList<INode>();

        while (tokens.size() > 0 && !tokens.indexIs(Comma.class)) {

          INode node = FilterParser.walk(tokens, subNodes);

          if (node == null) {
            break;
          }

          subNodes.add(node);

        }

        if (tokens.indexIs(Comma.class)) {
          tokens.take();
        }

        if (subNodes.size() != 1) {
          throw new RuntimeException("aaaaa");
        }

        arguments.add((IExpression) subNodes.take());

      }

      tokens.take();

      Collections.reverse(arguments);

      return Function.builder().name(name).arguments(arguments).build();

    }

    return null;

  }


}
