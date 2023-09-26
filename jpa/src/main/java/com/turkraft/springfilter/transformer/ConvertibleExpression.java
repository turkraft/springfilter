package com.turkraft.springfilter.transformer;

import jakarta.persistence.criteria.Expression;

public interface ConvertibleExpression {

  Expression<?> convertTo(Class<?> type);

}
