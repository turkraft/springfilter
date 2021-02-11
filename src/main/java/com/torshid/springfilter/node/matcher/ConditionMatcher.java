package com.torshid.springfilter.node.matcher;

import java.util.LinkedList;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.exception.InputExpected;
import com.torshid.compiler.node.INode;
import com.torshid.compiler.node.matcher.Matcher;
import com.torshid.compiler.token.IToken;
import com.torshid.springfilter.node.Condition;
import com.torshid.springfilter.node.ConditionNoInput;
import com.torshid.springfilter.node.ConditionWithInput;
import com.torshid.springfilter.token.Comparator;
import com.torshid.springfilter.token.Field;
import com.torshid.springfilter.token.input.Input;
import com.torshid.springfilter.token.input.Text;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class ConditionMatcher extends Matcher<Condition> {

  public static final ConditionMatcher INSTANCE = new ConditionMatcher();

  @Override
  public Condition match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws InputExpected {

    if (tokens.indexIs(Field.class, Comparator.class)) {

      // a comparator may need an input, for example ' >: '

      if (((Comparator) tokens.get(1)).needsInput()) {

        if (tokens.indexIs(null, null, Input.class)) {

          return ConditionWithInput.builder().field(((Field) tokens.take()).getName())
              .comparator(((Comparator) tokens.take())).input((Input<?>) tokens.take()).build();

        } else if (tokens.indexIs(null, null, Field.class)) {

          // if the input token was interpreted as a field token, we may also use it as a text node
          // TODO: think of a better way for these collisions

          return ConditionWithInput.builder().field(((Field) tokens.take()).getName())
              .comparator(((Comparator) tokens.take()))
              .input(Text.builder().value(((Field) tokens.take()).getName()).build()).build();

        } else {
          throw new InputExpected("Comparator " + ((Comparator) tokens.get(1)).getLiteral() + " expects an input");
        }

      }

      return ConditionNoInput.builder().field(((Field) tokens.take()).getName())
          .comparator(((Comparator) tokens.take())).build();

    }

    return null;

  }

}
