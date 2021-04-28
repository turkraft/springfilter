package com.turkraft.springfilter.compiler.node;

import java.util.LinkedList;
import com.turkraft.springfilter.compiler.Extensions;
import com.turkraft.springfilter.compiler.token.IToken;
import com.turkraft.springfilter.compiler.token.Word;
import com.turkraft.springfilter.exception.ParserException;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class FunctionMatcher extends Matcher<Function> {

  public static final FunctionMatcher INSTANCE = new FunctionMatcher();

  @Override
  public Function match(LinkedList<IToken> tokens, LinkedList<IExpression> nodes)
      throws ParserException {

    if (tokens.indexIs(Word.class)) {

      String name = ((Word) tokens.take()).getValue();

      Arguments arguments = ArgumentsMatcher.INSTANCE.match(tokens, nodes);

      if (arguments != null) {
        return Function.builder().name(name).arguments(arguments).build();
      }

    }

    return null;

  }

}
