package com.turkraft.springfilter.compiler.node;

import java.util.LinkedList;
import com.turkraft.springfilter.compiler.Extensions;
import com.turkraft.springfilter.compiler.token.Dot;
import com.turkraft.springfilter.compiler.token.IToken;
import com.turkraft.springfilter.compiler.token.Word;
import com.turkraft.springfilter.exception.ParserException;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class FieldMatcher extends Matcher<Field> {

  public static final FieldMatcher INSTANCE = new FieldMatcher();

  @Override
  public Field match(LinkedList<IToken> tokens, LinkedList<IExpression> nodes)
      throws ParserException {

    if (tokens.indexIs(Word.class)) {

      StringBuilder name = new StringBuilder(((Word) tokens.take()).getValue());

      while (tokens.indexIs(Dot.class, Word.class)) {
        tokens.take();
        name.append(".").append(((Word) tokens.take()).getValue());
      }

      return Field.builder().name(name.toString()).build();

    }

    return null;

  }


}
