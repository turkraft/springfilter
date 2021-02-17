package com.turkraft.springfilter.token.input;

import com.turkraft.springfilter.FilterConfig;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@ToString(callSuper = true)
public class Numeral implements IInput {

  private Number value;

  @Override
  public boolean canBe(Class<?> klass) {
    return Number.class.isAssignableFrom(klass);
  }

  @Override
  public Object getValueAs(Class<?> klass) {
    return value;
  }

  @Override
  public String generate() {
    if (value == null)
      return "";
    return FilterConfig.NUMBER_FORMAT.format(value);
  }

}
