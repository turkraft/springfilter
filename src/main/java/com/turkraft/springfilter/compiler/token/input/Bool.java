package com.turkraft.springfilter.compiler.token.input;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@ToString(callSuper = true)
public class Bool implements IInput {

  private Boolean value;

  @Override
  public boolean canBe(Class<?> klass) {
    return String.class.isAssignableFrom(klass) || Boolean.class.isAssignableFrom(klass);
  }

  @Override
  public Object getValueAs(Class<?> klass) {
    if (String.class.isAssignableFrom(klass)) {
      return value.toString().toLowerCase();
    }
    return value;
  }

  @Override
  public String generate() {
    if (value == null)
      return "";
    return value.toString().toLowerCase();
  }

}
