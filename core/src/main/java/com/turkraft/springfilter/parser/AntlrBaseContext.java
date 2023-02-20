package com.turkraft.springfilter.parser;

import org.antlr.v4.runtime.ParserRuleContext;

abstract class AntlrBaseContext extends ParserRuleContext {

  public AntlrBaseContext() {
  }

  public AntlrBaseContext(ParserRuleContext parent, int invokingStateNumber) {
    super(parent, invokingStateNumber);
  }

}
