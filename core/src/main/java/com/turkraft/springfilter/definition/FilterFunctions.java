package com.turkraft.springfilter.definition;

import java.util.List;

public interface FilterFunctions {

  List<FilterFunction> getFunctions();

  void setFunctions(List<FilterFunction> functions);

  FilterFunction getFunction(String name);

}
