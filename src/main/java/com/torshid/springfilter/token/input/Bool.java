package com.torshid.springfilter.token.input;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Bool extends Input<Boolean> {

  @Override
  public Boolean getValueAs(Class<?> klass) {
    return getValue();
  }

  @Override
  public String generate() {
    return getValue().toString().toLowerCase();
  }

}
