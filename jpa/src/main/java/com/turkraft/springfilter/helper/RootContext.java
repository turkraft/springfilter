package com.turkraft.springfilter.helper;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.util.HashMap;
import java.util.Map;

public class RootContext {

  private final Root<?> root;

  private final Map<String, Path<?>> paths = new HashMap<>();

  public RootContext(Root<?> root) {
    this.root = root;
  }
  
  public Root<?> getRoot() {
    return root;
  }

  public Map<String, Path<?>> getPaths() {
    return paths;
  }

}
