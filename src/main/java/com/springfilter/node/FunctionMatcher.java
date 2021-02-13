package com.springfilter.node;

import java.util.LinkedList;
import java.util.List;

import com.springfilter.compiler.Extensions;
import com.springfilter.compiler.exception.ParserException;
import com.springfilter.compiler.node.INode;
import com.springfilter.compiler.node.Matcher;
import com.springfilter.compiler.token.IToken;
import com.springfilter.token.Comma;
import com.springfilter.token.Parenthesis;
import com.springfilter.token.Parenthesis.Type;
import com.springfilter.token.Word;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class FunctionMatcher extends Matcher<Function> {

  public static final FunctionMatcher INSTANCE = new FunctionMatcher();

  @Override
  public Function match(LinkedList<Matcher<?>> matchers, LinkedList<IToken> tokens, LinkedList<INode> nodes)
      throws ParserException {

    if (tokens.indexIs(Word.class, Parenthesis.class)) {

      String name = ((Word) tokens.take()).getValue();

      tokens.take();

      List<IExpression> arguments = new LinkedList<>();

      while (tokens.size() > 0
          && (!tokens.indexIs(Parenthesis.class) || ((Parenthesis) tokens.index()).getType() != Type.CLOSE)) {

        arguments.add(MatcherUtils.getNextExpression("Expression(s) expected as the function argument(s)",
            n -> tokens.size() > 0
                && (!tokens.indexIs(Parenthesis.class) || ((Parenthesis) tokens.index()).getType() != Type.CLOSE)
                && !tokens.indexIs(Comma.class),
            matchers, tokens));

        if (tokens.indexIs(Comma.class)) {
          tokens.take();
        }

      }

      tokens.take();

      return Function.builder().name(name).arguments(arguments).build();

    }

    return null;

  }


}
