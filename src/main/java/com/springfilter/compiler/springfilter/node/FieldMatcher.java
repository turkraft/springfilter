package com.springfilter.compiler.springfilter.node;

import java.util.LinkedList;

import com.springfilter.compiler.compiler.Extensions;
import com.springfilter.compiler.compiler.exception.ParserException;
import com.springfilter.compiler.compiler.node.INode;
import com.springfilter.compiler.compiler.node.Matcher;
import com.springfilter.compiler.compiler.token.IToken;
import com.springfilter.compiler.springfilter.token.Dot;
import com.springfilter.compiler.springfilter.token.Word;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class FieldMatcher extends Matcher<Field> {

  public static final FieldMatcher INSTANCE = new FieldMatcher();

  @Override
  public Field match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws ParserException {

    if (tokens.indexIs(Word.class)) {

      String name = ((Word) tokens.take()).getValue();

      while (tokens.indexIs(Dot.class, Word.class)) {

        tokens.take();

        name += "." + ((Word) tokens.take()).getValue();

      }

      return Field.builder().name(name).build();

    }

    return null;

  }


}
