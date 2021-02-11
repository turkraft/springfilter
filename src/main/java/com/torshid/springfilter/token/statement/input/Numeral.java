package com.torshid.springfilter.token.statement.input;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Numeral extends Input<Number> {

  @Override
  public Number getValueAs(Class<?> klass) {
    return getValue();
  }

  @Override
  public String generate() {
    return getValue().toString();
  }

}
