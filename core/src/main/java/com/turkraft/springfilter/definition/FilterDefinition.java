package com.turkraft.springfilter.definition;

import org.springframework.lang.Nullable;

public interface FilterDefinition {

  @Nullable
  default String getDescription() {
    return null;
  }

  @Nullable
  default String getExample() {
    return null;
  }

}
