package com.turkraft.springfilter.node;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.turkraft.springfilter.Extensions;
import com.turkraft.springfilter.FilterParser;
import com.turkraft.springfilter.exception.ExpressionExpectedException;
import com.turkraft.springfilter.exception.ParserException;
import com.turkraft.springfilter.token.Comma;
import com.turkraft.springfilter.token.IToken;
import com.turkraft.springfilter.token.Parenthesis;
import com.turkraft.springfilter.token.Parenthesis.Type;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class ArgumentsMatcher extends Matcher<Arguments> {

  public static final ArgumentsMatcher INSTANCE = new ArgumentsMatcher();

  @Override
  public Arguments match(LinkedList<IToken> tokens, LinkedList<IExpression> nodes) throws ParserException {

    if (tokens.indexIs(Parenthesis.class) && ((Parenthesis) tokens.index()).getType() == Type.OPEN) {

      tokens.take();

      List<IExpression> arguments = new LinkedList<>();

      // TODO: refactor in order to use parser.run()

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
          throw new ExpressionExpectedException("Arguments are expected to be expressions");
        }

        arguments.add(subNodes.take());

      }

      tokens.take();

      Collections.reverse(arguments);

      return Arguments.builder().values(arguments).build();

    }

    return null;

  }


}
