package com.springfilter.compiler.springfilter.node.predicate;

import java.util.LinkedList;

import com.springfilter.compiler.compiler.Extensions;
import com.springfilter.compiler.compiler.Parser;
import com.springfilter.compiler.compiler.exception.ParserException;
import com.springfilter.compiler.compiler.node.INode;
import com.springfilter.compiler.compiler.node.Matcher;
import com.springfilter.compiler.compiler.token.IToken;
import com.springfilter.compiler.springfilter.node.IPredicate;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class PredicateMatcher extends Matcher<IPredicate> {

  public static final PredicateMatcher INSTANCE = new PredicateMatcher();

  private static Matcher<?>[] matchers = new Matcher<?>[] {

      PriorityMatcher.INSTANCE, ConditionMatcher.INSTANCE, OperationMatcher.INSTANCE,

  };

  @Override
  public IPredicate match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws ParserException {
    return (IPredicate) Parser.walk(matchers, tokens, nodes);
  }

}
