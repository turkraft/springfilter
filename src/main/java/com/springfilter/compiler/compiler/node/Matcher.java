package com.springfilter.compiler.compiler.node;

import java.util.LinkedList;

import com.springfilter.compiler.compiler.exception.ParserException;
import com.springfilter.compiler.compiler.token.IToken;

public abstract class Matcher<N extends INode> {

  public abstract N match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws ParserException;

}
