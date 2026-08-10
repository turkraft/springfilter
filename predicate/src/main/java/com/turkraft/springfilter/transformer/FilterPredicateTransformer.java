package com.turkraft.springfilter.transformer;

import com.turkraft.springfilter.language.InsensitiveLikeOperator;
import com.turkraft.springfilter.parser.node.CollectionLikeNode;
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
import com.turkraft.springfilter.transformer.processor.ContainerPredicate;
import com.turkraft.springfilter.transformer.processor.FieldAccessPredicate;
import com.turkraft.springfilter.transformer.processor.LikeOperationPredicateProcessor;
import com.turkraft.springfilter.transformer.processor.PredicateValueExtractor;
import com.turkraft.springfilter.transformer.processor.factory.FilterNodeProcessorFactories;
import java.util.function.Predicate;
import org.springframework.core.convert.ConversionService;

public class FilterPredicateTransformer implements FilterNodeTransformer<Predicate<Object>> {

  protected final ConversionService conversionService;

  protected final FilterNodeProcessorFactories filterNodeProcessorFactories;

  private final Class<?> entityType;

  public FilterPredicateTransformer(
      ConversionService conversionService,
      FilterNodeProcessorFactories filterNodeProcessorFactories,
      Class<?> entityType) {
    this.conversionService = conversionService;
    this.filterNodeProcessorFactories = filterNodeProcessorFactories;
    this.entityType = entityType;
  }

  @Override
  public Class<Predicate<Object>> getTargetType() {
    return (Class<Predicate<Object>>) (Class<?>) Predicate.class;
  }

  @Override
  public Predicate<Object> transformField(FieldNode node) {
    return new FieldAccessPredicate(node.getName());
  }

  @Override
  public Predicate<Object> transformInput(InputNode node) {
    return new ContainerPredicate<>(node.getValue());
  }

  @Override
  public Predicate<Object> transformPriority(PriorityNode node) {
    return transform(node.getNode());
  }

  @Override
  public Predicate<Object> transformPlaceholder(PlaceholderNode node) {
    return filterNodeProcessorFactories
        .getPlaceholderProcessorFactory()
        .process(this, node);
  }

  @Override
  public Predicate<Object> transformFunction(FunctionNode node) {
    return filterNodeProcessorFactories
        .getFunctionProcessorFactory()
        .process(this, node);
  }

  @Override
  public Predicate<Object> transformCollectionLike(CollectionLikeNode node) {
    Predicate<?> left = transform(node.getLeft());
    boolean caseInsensitive = node.getOperator() instanceof InsensitiveLikeOperator;
    return entity -> {
      Object leftValue = PredicateValueExtractor.extractValue(left, entity);
      if (leftValue == null) return false;
      String str = leftValue.toString();
      for (FilterNode pattern : node.getPatterns()) {
        Predicate<?> right = transform(pattern);
        Object rightValue = PredicateValueExtractor.extractValue(right, entity);
        if (rightValue == null) continue;
        String patStr = rightValue.toString();
        String regex = LikeOperationPredicateProcessor.likePatternToRegex(patStr);
        boolean matches = caseInsensitive
            ? str.toLowerCase().matches(regex.toLowerCase())
            : str.matches(regex);
        if (matches) return true;
      }
      return false;
    };
  }

  @Override
  public Predicate<Object> transformCollection(CollectionNode node) {
    throw new UnsupportedOperationException("Unsupported node " + node);
  }

  @Override
  public Predicate<Object> transformPrefixOperation(PrefixOperationNode node) {
    return filterNodeProcessorFactories
        .getOperationProcessorFactory()
        .process(this, node);
  }

  @Override
  public Predicate<Object> transformInfixOperation(InfixOperationNode node) {
    return filterNodeProcessorFactories
        .getOperationProcessorFactory()
        .process(this, node);
  }

  @Override
  public Predicate<Object> transformPostfixOperation(PostfixOperationNode node) {
    return filterNodeProcessorFactories
        .getOperationProcessorFactory()
        .process(this, node);
  }

}
