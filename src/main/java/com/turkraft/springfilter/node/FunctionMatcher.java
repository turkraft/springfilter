package com.turkraft.springfilter.node;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.turkraft.springfilter.Extensions;
import com.turkraft.springfilter.FilterParser;
import com.turkraft.springfilter.exception.ParserException;
import com.turkraft.springfilter.token.Comma;
import com.turkraft.springfilter.token.IToken;
import com.turkraft.springfilter.token.Parenthesis;
import com.turkraft.springfilter.token.Parenthesis.Type;
import com.turkraft.springfilter.token.Word;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class FunctionMatcher extends Matcher<Function> {

  public static final FunctionMatcher INSTANCE = new FunctionMatcher();

  @Override
  public Function match(LinkedList<IToken> tokens, LinkedList<IExpression> nodes) throws ParserException {

    if (tokens.indexIs(Word.class, Parenthesis.class) && ((Parenthesis) tokens.get(1)).getType() == Type.OPEN) {

      String name = ((Word) tokens.take()).getValue();

      tokens.take();

      List<IExpression> arguments = new LinkedList<>();

      while (tokens.size() > 0
          && (!tokens.indexIs(Parenthesis.class) || ((Parenthesis) tokens.index()).getType() != Type.CLOSE)) {

        LinkedList<IExpression> subNodes = new LinkedList<IExpression>();

        while (tokens.size() > 0 && !tokens.indexIs(Comma.class)) {

          IExpression node = FilterParser.walk(tokens, subNodes);

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

        arguments.add(subNodes.take());

      }

      tokens.take();

      Collections.reverse(arguments);

      return Function.builder().name(name).arguments(arguments).build();

    }

    return null;

  }


}
