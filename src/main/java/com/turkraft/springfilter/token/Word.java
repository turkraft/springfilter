package com.turkraft.springfilter.token;

import com.turkraft.springfilter.compiler.token.IToken;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Word implements IToken {

  private String value;

}
