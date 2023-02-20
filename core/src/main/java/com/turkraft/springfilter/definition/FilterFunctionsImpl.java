package com.turkraft.springfilter.definition;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
class FilterFunctionsImpl implements FilterFunctions {

  private List<FilterFunction> functions;

  public FilterFunctionsImpl(List<FilterFunction> functions) {
    this.functions = functions;
  }

  @Override
  public List<FilterFunction> getFunctions() {
    return functions;
  }

  @Override
  public void setFunctions(List<FilterFunction> functions) {
    this.functions = functions;
  }

  @Override
  public FilterFunction getFunction(String name) {
    for (FilterFunction function : functions) {
      if (function.getName().equals(name)) {
        return function;
      }
    }
    throw new UnsupportedOperationException("Unrecognized function `" + name + "`");
  }

}
