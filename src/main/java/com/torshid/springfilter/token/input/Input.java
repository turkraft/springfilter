package com.torshid.springfilter.token.input;

import com.torshid.compiler.token.Token;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class Input<T> extends Token {

  private T value;

}
