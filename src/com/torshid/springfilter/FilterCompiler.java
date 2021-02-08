package com.torshid.springfilter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.query.criteria.internal.path.PluralAttributePath;

import com.torshid.compiler.Compiler;
import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.exception.TokenizerException;
import com.torshid.compiler.node.Node;
import com.torshid.compiler.token.Token;
import com.torshid.springfilter.node.Condition;
import com.torshid.springfilter.node.ConditionWithInput;
import com.torshid.springfilter.node.Filter;
import com.torshid.springfilter.node.Operation;
import com.torshid.springfilter.node.OperationInfix;
import com.torshid.springfilter.node.OperationPrefix;
import com.torshid.springfilter.node.Priority;
import com.torshid.springfilter.node.matcher.FilterMarcher;

public class FilterCompiler {

  private FilterCompiler() {}

  public static LinkedList<Token> tokenize(String input) throws TokenizerException {
    return FilterTokenizer.tokenize(input);
  }

  public static Filter parse(LinkedList<Token> tokens) throws ParserException {
    return Compiler.parse(FilterMarcher.INSTANCE, tokens);
  }

  public static String generate(Filter ast) {
    return Compiler.generate(ast);
  }

  public static <T> T generate(Filter ast, Function<Node, T> func) {
    return Compiler.generate(ast, func);
  }

  public static String compile(String input) throws ParserException, TokenizerException {
    return generate(parse(tokenize(input)));
  }

  public static <T> T compile(String input, Function<Node, T> func) throws ParserException, TokenizerException {
    return generate(parse(tokenize(input)), func);
  }

  /* Below this comment are defined the methods for Predicate generation, which require Hibernate Core */
  /* Hibernate is actually needed for the PluralAttributePath class, otherwise JPA would be enough */

  private static Trio trio; // TODO: find another way to store the trio which would not need 'synchronized'

  public static synchronized Predicate compile(String input, Root<?> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) throws ParserException, TokenizerException {
    trio = new Trio(root, query, criteriaBuilder);
    return compile(input, FilterCompiler::getPredicate);
  }

  private static Predicate getPredicate(Node node) {
    return getPredicate(node, new HashMap<>());
  }

  private static Predicate getPredicate(Node node, Map<String, Join<Object, Object>> joins) {

    if (node instanceof Filter) {
      return getPredicate(((Filter) node).getBody(), joins);
    }

    else if (node instanceof Priority) {
      return getPredicate(((Priority) node).getBody(), joins);
    }

    else if (node instanceof Condition) {
      if (node instanceof ConditionWithInput) {
        return getConditionWithInputPredicate((ConditionWithInput) node, joins);
      } else {
        return getConditionPredicate((Condition) node, joins);
      }
    }

    else if (node instanceof Operation) {
      if (node instanceof OperationInfix) {
        return getOperationInfixPredicate((OperationInfix) node, joins);
      } else {
        return getOperationPrefixPredicate((OperationPrefix) node, joins);
      }
    }

    throw new RuntimeException("Unhandled node");

  }

  private static Predicate getConditionWithInputPredicate(ConditionWithInput conditionWithInput,
      Map<String, Join<Object, Object>> joins) {

    Path<?> path = buildDatabasePath(trio.root, joins, conditionWithInput.getField());

    switch (conditionWithInput.getComparator()) {

      case EQUAL:
        return trio.criteriaBuilder.equal(path, conditionWithInput.getInput());

      case NOT_EQUAL:
        return trio.criteriaBuilder.notEqual(path, conditionWithInput.getInput());

      case GREATER_THAN:
        return trio.criteriaBuilder.greaterThan((Expression) path, (Comparable) conditionWithInput.getInput());

      case GREATER_THAN_EQUAL:
        return trio.criteriaBuilder.greaterThanOrEqualTo((Expression) path, (Comparable) conditionWithInput.getInput());

      case LESS_THAN:
        return trio.criteriaBuilder.lessThan((Expression) path, (Comparable) conditionWithInput.getInput());

      case LESS_THAN_EQUAL:
        return trio.criteriaBuilder.lessThanOrEqualTo((Expression) path, (Comparable) conditionWithInput.getInput());

      case LIKE: {
        return trio.criteriaBuilder.like(trio.criteriaBuilder.upper((Expression) path),
            "%" + conditionWithInput.getInput().toString().trim().toUpperCase() + "%");
      }

      default:
        throw new RuntimeException(
            "Unsupported condition comparator " + conditionWithInput.getComparator().getLiteral());

    }

  }

  private static Predicate getConditionPredicate(Condition condition, Map<String, Join<Object, Object>> joins) {

    Path<?> path = buildDatabasePath(trio.root, joins, condition.getField());

    switch (condition.getComparator()) {

      case EMPTY:
      case NULL:
        if (path instanceof PluralAttributePath) {
          return trio.criteriaBuilder.isEmpty((PluralAttributePath) path);
        } else {
          return trio.criteriaBuilder.isNull(path);
        }

      case NOT_EMPTY:
      case NOT_NULL:
        if (path instanceof PluralAttributePath) {
          return trio.criteriaBuilder.isNotEmpty((PluralAttributePath) path);
        } else {
          return trio.criteriaBuilder.isNotNull(path);
        }

      default:
        throw new RuntimeException("Unsupported condition comparator " + condition.getComparator().getLiteral());

    }

  }

  private static Predicate getOperationInfixPredicate(OperationInfix operationInfix,
      Map<String, Join<Object, Object>> joins) {

    switch (operationInfix.getType()) {

      case AND:
        return trio.criteriaBuilder.and(getPredicate(operationInfix.getLeft(), joins),
            getPredicate(operationInfix.getRight(), joins));

      case OR:
        return trio.criteriaBuilder.or(getPredicate(operationInfix.getLeft(), joins),
            getPredicate(operationInfix.getRight(), joins));

      default:
        throw new RuntimeException("Unsupported infix operator " + operationInfix.getType().getLiteral());

    }

  }

  private static Predicate getOperationPrefixPredicate(OperationPrefix operationPrefix,
      Map<String, Join<Object, Object>> joins) {

    switch (operationPrefix.getType()) {

      case NOT:
        return trio.criteriaBuilder.not(getPredicate(operationPrefix.getRight(), joins));

      default:
        throw new RuntimeException("Unsupported infix operator " + operationPrefix.getType().getLiteral());

    }

  }

  private static Path<?> buildDatabasePath(Root<?> table, Map<String, Join<Object, Object>> joins, String fieldPath) {

    Path<?> path = table;

    String[] fields = fieldPath.contains(".") ? fieldPath.split("\\.") : new String[] {fieldPath};

    String previous = null;

    for (String field : fields) {

      if (path instanceof PluralAttributePath) { // = relation
        if (!joins.containsKey(previous)) {
          joins.put(previous, table.join(previous));
        }
        path = joins.get(previous).get(field);
      } else {
        path = path.get(field);
      }

      if (previous == null) {
        previous = field;
      } else {
        previous += "." + field;
      }

    }

    return path;

  }

  private static class Trio {

    private final Root<?> root;
    private final CriteriaQuery<?> criteriaQuery;
    private final CriteriaBuilder criteriaBuilder;

    public Trio(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
      this.root = root;
      this.criteriaQuery = criteriaQuery;
      this.criteriaBuilder = criteriaBuilder;
    }

  }

}
