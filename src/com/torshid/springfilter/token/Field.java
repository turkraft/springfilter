package com.torshid.springfilter.token;

import com.torshid.compiler.token.Token;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Field extends Token {

  private String name;

}
