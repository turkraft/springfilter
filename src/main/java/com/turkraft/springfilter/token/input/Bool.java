package com.turkraft.springfilter.token.input;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@ToString(callSuper = true)
public class Bool implements IInput {

  // bool should maybe implement IPredicate too

  private Boolean value;

  @Override
  public boolean canBe(Class<?> klass) {
    return Boolean.class.isAssignableFrom(klass);
  }

  @Override
  public Object getValueAs(Class<?> klass) {
    return value;
  }

  @Override
  public String toStringAs(Class<?> targetClass) {
    return value.toString().toLowerCase();
  }

}
