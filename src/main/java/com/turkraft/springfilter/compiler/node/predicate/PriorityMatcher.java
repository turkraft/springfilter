package com.turkraft.springfilter.compiler.node.predicate;

import java.util.LinkedList;
import com.turkraft.springfilter.compiler.Parser;
import com.turkraft.springfilter.compiler.Extensions;
import com.turkraft.springfilter.compiler.node.IExpression;
import com.turkraft.springfilter.compiler.node.Matcher;
import com.turkraft.springfilter.compiler.token.IToken;
import com.turkraft.springfilter.compiler.token.Parenthesis;
import com.turkraft.springfilter.compiler.token.Parenthesis.Type;
import com.turkraft.springfilter.exception.OutOfTokenException;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class PriorityMatcher extends Matcher<Priority> {

  public static final PriorityMatcher INSTANCE = new PriorityMatcher();

  @Override
  public Priority match(LinkedList<IToken> tokens, LinkedList<IExpression> nodes) {

    if (tokens.indexIs(Parenthesis.class)
        && ((Parenthesis) tokens.index()).getType() == Type.OPEN) {

      tokens.take();

      LinkedList<IExpression> subNodes = new LinkedList<>();

      IExpression body = Parser.run(tokens, subNodes);

      if (body == null) {
        return null;
      }

      if (!tokens.indexIs(Parenthesis.class)
          || ((Parenthesis) tokens.index()).getType() != Type.CLOSE) {
        throw new OutOfTokenException("Closing parenthesis not found");
      }

      tokens.take();

      return Priority.builder().body(body).build();

    }

    return null;

  }

}
