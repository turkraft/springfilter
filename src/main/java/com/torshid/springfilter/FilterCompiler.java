package com.torshid.springfilter;

import java.util.function.Function;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.exception.TokenizerException;
import com.torshid.compiler.node.INode;
import com.torshid.springfilter.node.Filter;

public class FilterCompiler {

  private FilterCompiler() {}

  public static String generate(Filter ast) {
    return ast.generate();
  }

  public static <T> T generate(Filter ast, Function<INode, T> func) {
    return ast.generate(func);
  }

  public static Predicate generate(Filter ast, Root<?> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    return ast.generate(root, query, criteriaBuilder);
  }

  public static String compile(String input) throws ParserException, TokenizerException {
    return generate(FilterParser.parse(input));
  }

  public static <T> T compile(String input, Function<INode, T> func) throws ParserException, TokenizerException {
    return generate(FilterParser.parse(input), func);
  }

  public static Predicate compile(String input, Root<?> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder)
      throws ParserException, TokenizerException {
    return generate(FilterParser.parse(input), root, query, criteriaBuilder);
  }

}
