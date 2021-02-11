package com.torshid.springfilter.token;

import com.torshid.compiler.token.IToken;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Field implements IToken {

  private String name;

}
