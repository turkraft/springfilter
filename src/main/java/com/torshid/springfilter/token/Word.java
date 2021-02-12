package com.torshid.springfilter.token;

import com.torshid.compiler.token.IToken;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Word implements IToken {

  private String value;

}
