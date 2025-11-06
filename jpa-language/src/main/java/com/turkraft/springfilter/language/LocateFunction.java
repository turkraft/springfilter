package com.turkraft.springfilter.language;

import com.turkraft.springfilter.definition.FilterFunction;
import org.springframework.stereotype.Component;

@Component
public class LocateFunction extends FilterFunction {

  public LocateFunction() {
    super("locate");
  }

  @Override
  public String getDescription() {
    return "Position of substring in string";
  }

  @Override
  public String getExample() {
    return "locate(name, 'John') > 0";
  }

}
