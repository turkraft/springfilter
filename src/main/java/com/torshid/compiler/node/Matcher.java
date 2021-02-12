package com.torshid.compiler.node;

import java.util.LinkedList;

import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.token.IToken;

public abstract class Matcher<N extends INode> {

  public abstract N match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws ParserException;

}
