package com.torshid.springfilter.node.predicate;

import java.util.LinkedList;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.Parser;
import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.node.INode;
import com.torshid.compiler.node.Matcher;
import com.torshid.compiler.token.IToken;
import com.torshid.springfilter.token.predicate.IPredicate;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class PredicateMatcher extends Matcher<IPredicate> {

  public static final PredicateMatcher INSTANCE = new PredicateMatcher();

  private static Matcher<?>[] matchers = new Matcher<?>[] {

      ConditionMatcher.INSTANCE, OperationMatcher.INSTANCE, PriorityMatcher.INSTANCE

  };

  @Override
  public IPredicate match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws ParserException {
    return (IPredicate) Parser.walk(matchers, tokens, nodes);
  }

}
