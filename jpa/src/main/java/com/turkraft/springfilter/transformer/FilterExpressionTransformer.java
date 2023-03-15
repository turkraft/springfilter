package com.turkraft.springfilter.transformer;

import com.turkraft.springfilter.helper.ExistsExpressionHelper;
import com.turkraft.springfilter.helper.PathExpressionHelper;
import com.turkraft.springfilter.helper.RootContext;
import com.turkraft.springfilter.parser.node.CollectionNode;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.parser.node.InputNode;
import com.turkraft.springfilter.parser.node.PlaceholderNode;
import com.turkraft.springfilter.parser.node.PostfixOperationNode;
import com.turkraft.springfilter.parser.node.PrefixOperationNode;
import com.turkraft.springfilter.parser.node.PriorityNode;
import com.turkraft.springfilter.transformer.processor.factory.FilterNodeProcessorFactories;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;

public class FilterExpressionTransformer implements FilterNodeTransformer<Expression<?>> {

  private final ConversionService conversionService;

  private final PathExpressionHelper pathExpressionHelper;

  private final ExistsExpressionHelper existsExpressionHelper;

  private final RootContext rootContext;

  private final CriteriaQuery<?> criteriaQuery;

  private final CriteriaBuilder criteriaBuilder;

  private final FilterNodeProcessorFactories filterNodeProcessorFactories;

  private boolean insideExists;

  private final Map<FilterNode, Class<?>> targetTypes = new HashMap<>();

  private final Set<FilterNode> ignoreExists = new HashSet<>();

  private final Map<FilterNode, RootContext> rootContexts = new HashMap<>();

  public FilterExpressionTransformer(ConversionService conversionService,
      PathExpressionHelper pathExpressionHelper,
      ExistsExpressionHelper existsExpressionHelper,
      FilterNodeProcessorFactories filterNodeProcessorFactories,
      Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
    this.conversionService = conversionService;
    this.pathExpressionHelper = pathExpressionHelper;
    this.existsExpressionHelper = existsExpressionHelper;
    this.filterNodeProcessorFactories = filterNodeProcessorFactories;
    this.rootContext = new RootContext(root);
    this.criteriaQuery = criteriaQuery;
    this.criteriaBuilder = criteriaBuilder;
    this.insideExists = false;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class<Expression<?>> getTargetType() {
    return (Class<Expression<?>>) (Class<?>) Expression.class;
  }

  @Override
  public Expression<?> transform(FilterNode node) {
    if (!insideExists && !ignoreExists.contains(node) && existsExpressionHelper.requiresExists(this,
        node)) {
      insideExists = true;
      return existsExpressionHelper.wrapWithExists(this, node);
    }
    Expression<?> expression = FilterNodeTransformer.super.transform(node);
    if (targetTypes.containsKey(node)) {
      return castIfNeeded(expression, targetTypes.get(node));
    }
    return expression;
  }

  @Override
  public Expression<?> transformInput(InputNode node) {

    if (targetTypes.containsKey(node)) {
      try {
        return criteriaBuilder.literal(
            conversionService.convert(node.getValue(), targetTypes.get(node)));
      } catch (Exception ignored) {
      }
    }

    return criteriaBuilder.literal(node.getValue());

  }

  @Override
  public Expression<?> transformField(FieldNode node) {
    return pathExpressionHelper.getPath(rootContexts.getOrDefault(node, rootContext), node.getName()
    );
  }

  @Override
  public Expression<?> transformPlaceholder(PlaceholderNode node) {
    return filterNodeProcessorFactories.getPlaceholderProcessorFactory().process(this, node);
  }

  @Override
  public Expression<?> transformFunction(FunctionNode node) {
    return filterNodeProcessorFactories.getFunctionProcessorFactory().process(this, node);
  }

  @Override
  public Expression<?> transformCollection(CollectionNode node) {
    throw new UnsupportedOperationException("Unsupported node " + node);
  }

  @Override
  public Expression<?> transformPriority(PriorityNode node) {
    return transform(node.getNode());
  }

  @Override
  public Expression<?> transformPrefixOperation(PrefixOperationNode node) {
    return filterNodeProcessorFactories.getOperationProcessorFactory().process(this, node);
  }

  @Override
  public Expression<?> transformInfixOperation(InfixOperationNode node) {
    return filterNodeProcessorFactories.getOperationProcessorFactory().process(this, node);
  }

  @Override
  public Expression<?> transformPostfixOperation(PostfixOperationNode node) {
    return filterNodeProcessorFactories.getOperationProcessorFactory().process(this, node);
  }

  private Expression<?> castIfNeeded(Expression<?> expression, @Nullable Class<?> targetType) {
    if (targetType != null && expression.getJavaType() != null
        && !targetType.isAssignableFrom(expression.getJavaType())) {
      return expression.as(targetType);
    }
    return expression;
  }

  public FilterExpressionTransformer registerTargetType(FilterNode node, Class<?> targetType) {
    targetTypes.put(node, targetType);
    return this;
  }

  public FilterExpressionTransformer registerIgnoreExists(FilterNode source) {
    if (ignoreExists.contains(source)) {
      return this;
    }
    ignoreExists.add(source);
    for (FilterNode child : source.getChildren()) {
      registerIgnoreExists(child);
    }
    return this;
  }

  public FilterExpressionTransformer registerRootContext(FilterNode source,
      RootContext rootContext) {
    rootContexts.put(source, rootContext);
    for (FilterNode child : source.getChildren()) {
      registerRootContext(child, rootContext);
    }
    return this;
  }

  public RootContext getRootContext() {
    return rootContext;
  }

  public Root<?> getRoot() {
    return getRootContext().getRoot();
  }

  public CriteriaQuery<?> getCriteriaQuery() {
    return criteriaQuery;
  }

  public CriteriaBuilder getCriteriaBuilder() {
    return criteriaBuilder;
  }

}
