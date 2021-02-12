package com.springfilter.compiler.springfilter.node;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import com.springfilter.compiler.compiler.node.INode;

public interface IExpression<T> extends INode {

  javax.persistence.criteria.Expression<T> generate(Root<?> root, CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder, Map<String, Join<Object, Object>> joins);

}
