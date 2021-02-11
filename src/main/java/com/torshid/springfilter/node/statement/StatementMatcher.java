package com.torshid.springfilter.node.statement;

import java.util.LinkedList;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.node.INode;
import com.torshid.compiler.node.Matcher;
import com.torshid.compiler.token.IToken;
import com.torshid.springfilter.node.predicate.PriorityMatcher;
import com.torshid.springfilter.token.IExpression;
import com.torshid.springfilter.token.statement.Field;
import com.torshid.springfilter.token.statement.input.Input;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class StatementMatcher extends Matcher<IExpression<?>> {

  public static final StatementMatcher INSTANCE = new StatementMatcher();

  @Override
  public IExpression<?> match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws ParserException {

    if (tokens.indexIs(Input.class)) {
      return ((Input<?>) tokens.take());
    }

    if (tokens.indexIs(Field.class)) {
      return ((Field) tokens.take());
    }

    return PriorityMatcher.INSTANCE.match(tokens, nodes);

  }

}
