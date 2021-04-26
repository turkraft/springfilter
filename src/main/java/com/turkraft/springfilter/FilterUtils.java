package com.turkraft.springfilter;

import java.util.ArrayList;
import java.util.Collection;
import com.turkraft.springfilter.node.IExpression;

public class FilterUtils {

  private FilterUtils() {}

  public static IExpression getFilterFromInputs(String[] inputs) {

    if (inputs != null && inputs.length > 0) {

      Collection<IExpression> filters = new ArrayList<IExpression>();

      for (String input : inputs) {
        if (input.trim().length() > 0) {
          filters.add(FilterParser.parse(input.trim()));
        }
      }

      return FilterQueryBuilder.and(filters);

    }

    return null;

  }

}
