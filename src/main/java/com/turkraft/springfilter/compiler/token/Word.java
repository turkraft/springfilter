package com.turkraft.springfilter.compiler.token;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Word implements IToken {

  private String value;

}
