package com.torshid.springfilter.node.predicate;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.torshid.compiler.node.INode;
import com.torshid.springfilter.node.Filter;
import com.torshid.springfilter.token.predicate.IPredicate;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Priority implements IPredicate {

  private IPredicate body;

  @Override
  public INode transform(INode parent) {

    if (parent instanceof Filter || parent instanceof Priority) {
      return body.transform(parent); // no need for priority if parent is root or we have nested priorities
    }

    body = (IPredicate) body.transform(this);
    return this;

  }

  @Override
  public String generate() {
    return "(" + body.generate() + ")";
  }

  @Override
  public Predicate generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {
    return (criteriaBuilder.isFalse(body.generate(root, criteriaQuery, criteriaBuilder, joins)));
    //return body.generate(root, criteriaQuery, criteriaBuilder, joins);
  }

}
