package com.turkraft.springfilter.compiler.node;

import java.util.LinkedList;
import java.util.List;
import com.turkraft.springfilter.compiler.Parser;
import com.turkraft.springfilter.compiler.Extensions;
import com.turkraft.springfilter.compiler.token.Comma;
import com.turkraft.springfilter.compiler.token.IToken;
import com.turkraft.springfilter.compiler.token.Parenthesis;
import com.turkraft.springfilter.compiler.token.Parenthesis.Type;
import com.turkraft.springfilter.exception.ExpressionExpectedException;
import com.turkraft.springfilter.exception.ParserException;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class ArgumentsMatcher extends Matcher<Arguments> {

  public static final ArgumentsMatcher INSTANCE = new ArgumentsMatcher();

  @Override
  public Arguments match(LinkedList<IToken> tokens, LinkedList<IExpression> nodes)
      throws ParserException {

    if (tokens.indexIs(Parenthesis.class)
        && ((Parenthesis) tokens.index()).getType() == Type.OPEN) {

      tokens.take();

      List<IExpression> arguments = new LinkedList<>();

      while (tokens.size() > 0 && (!tokens.indexIs(Parenthesis.class)
          || ((Parenthesis) tokens.index()).getType() != Type.CLOSE)) {

        IExpression argument = Parser.run(tokens, new LinkedList<>());

        if (argument == null) {
          throw new ExpressionExpectedException("No expression detected when building arguments");
        }

        arguments.add(argument);

        if (tokens.indexIs(Comma.class)) {
          tokens.take();
        }

      }

      tokens.take();

      // Collections.reverse(arguments);

      return Arguments.builder().values(arguments).build();

    }

    return null;

  }


}
