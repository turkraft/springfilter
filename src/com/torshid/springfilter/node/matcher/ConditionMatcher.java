package com.torshid.springfilter.node.matcher;

import java.util.LinkedList;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.exception.InputExpected;
import com.torshid.compiler.node.Node;
import com.torshid.compiler.node.matcher.Matcher;
import com.torshid.compiler.token.Token;
import com.torshid.springfilter.node.Condition;
import com.torshid.springfilter.node.ConditionNoInput;
import com.torshid.springfilter.node.ConditionWithInput;
import com.torshid.springfilter.token.Comparator;
import com.torshid.springfilter.token.Field;
import com.torshid.springfilter.token.input.Input;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class ConditionMatcher extends Matcher<Condition> {

  public static final ConditionMatcher INSTANCE = new ConditionMatcher();

  @Override
  public Condition match(LinkedList<Token> tokens, LinkedList<Node> nodes) throws InputExpected {

    if (tokens.indexIs(Field.class, Comparator.class)) {

      // a comparator may need an input, for example ' >= '

      if (((Comparator) tokens.get(1)).getType().needsInput()) {

        if (tokens.indexIs(null, null, Input.class)) {

          return ConditionWithInput.builder().field(((Field) tokens.take()).getName())
              .comparator(((Comparator) tokens.take()).getType()).input(((Input<?>) tokens.take()).getValue()).build();

        } else {
          throw new InputExpected(
              "Comparator " + ((Comparator) tokens.get(1)).getType().getLiteral() + " expects an input");
        }

      }

      return ConditionNoInput.builder().field(((Field) tokens.take()).getName())
          .comparator(((Comparator) tokens.take()).getType()).build();

    }

    return null;

  }

}
