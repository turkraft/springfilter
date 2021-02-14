package com.turkraft.springfilter.node;

import java.util.LinkedList;

import com.turkraft.springfilter.token.IToken;

public abstract class Matcher<N extends IExpression> {

  public abstract N match(LinkedList<IToken> tokens, LinkedList<IExpression> nodes);

}
