package com.turkraft.springfilter.node;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import com.turkraft.springfilter.compiler.node.INode;

public interface IExpression extends INode {

  javax.persistence.criteria.Expression<?> generate(Root<?> root, CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder, Map<String, Join<Object, Object>> joins);

}
