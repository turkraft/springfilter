package com.turkraft.springfilter.builder;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiFunction;

class Utils {

  static <T> T merge(BiFunction<T, T, T> func, Collection<T> inputs) {
    if (inputs.size() == 0) {
      throw new IllegalArgumentException("Inputs should not be empty");
    }
    Iterator<T> i = inputs.iterator();
    if (inputs.size() == 1) {
      return i.next();
    }
    T result = i.next();
    while (i.hasNext()) {
      result = func.apply(result, i.next());
    }
    return result;
  }
  
}
