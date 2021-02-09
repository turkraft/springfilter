package com.torshid.compiler.node.matcher;

import java.util.LinkedList;

import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.node.Node;
import com.torshid.compiler.token.Token;

public abstract class Matcher<N extends Node> {

  public abstract N match(LinkedList<Token> tokens, LinkedList<Node> nodes) throws ParserException;

}
