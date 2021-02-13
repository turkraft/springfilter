package com.springfilter.compiler.node;

import java.util.LinkedList;

import com.springfilter.compiler.exception.ParserException;
import com.springfilter.compiler.token.IToken;

public abstract class Matcher<N extends INode> {

  public abstract N match(LinkedList<Matcher<?>> matchers, LinkedList<IToken> tokens, LinkedList<INode> nodes)
      throws ParserException;

}
