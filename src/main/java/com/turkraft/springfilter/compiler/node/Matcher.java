package com.turkraft.springfilter.compiler.node;

import java.util.LinkedList;
import com.turkraft.springfilter.compiler.token.IToken;

public abstract class Matcher<N extends IExpression> {

  public abstract N match(LinkedList<IToken> tokens, LinkedList<IExpression> nodes);

}
