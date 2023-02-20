package com.turkraft.springfilter.definition;

import java.util.List;

public interface FilterPlaceholders {

  List<FilterPlaceholder> getPlaceholders();

  void setPlaceholders(List<FilterPlaceholder> placeholders);

  FilterPlaceholder getPlaceholder(String name);

}
