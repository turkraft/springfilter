package com.torshid.springfilter.token.statement.input;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Bool extends Input<Boolean> {

  // bool should maybe implement IPredicate too

  @Override
  public Boolean getValueAs(Class<?> klass) {
    return getValue();
  }

  @Override
  public String generate() {
    return getValue().toString().toLowerCase();
  }

}
