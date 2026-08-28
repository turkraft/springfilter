package com.turkraft.springfilter.transformer;

import com.turkraft.springfilter.helper.BsonValues;
import com.turkraft.springfilter.helper.FieldTypeResolver;
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
import com.turkraft.springfilter.transformer.processor.factory.FilterNodeProcessorFactories;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;

public class FilterBsonTransformer implements FilterNodeTransformer<Object> {

  protected final ConversionService conversionService;

  protected final FilterNodeProcessorFactories filterNodeProcessorFactories;

  protected final FieldTypeResolver fieldTypeResolver;

  private final Class<?> entityType;

  private final Map<FilterNode, Class<?>> targetTypes = new HashMap<>();

  public FilterBsonTransformer(ConversionService conversionService,
      FilterNodeProcessorFactories filterNodeProcessorFactories,
      FieldTypeResolver fieldTypeResolver,
      Class<?> entityType) {
    this.conversionService = conversionService;
    this.filterNodeProcessorFactories = filterNodeProcessorFactories;
    this.fieldTypeResolver = fieldTypeResolver;
    this.entityType = entityType;
  }

  @Override
  public Class<Object> getTargetType() {
    return Object.class;
  }

  @Override
  public Object transformField(FieldNode node) {
    Field field = fieldTypeResolver.getField(getEntityType(), node.getName());
    if (field != null && field.isAnnotationPresent(Id.class) && !node.getName().contains(".")) {
      return "$_id";
    }
    String name = fieldTypeResolver.storedFieldPath(getEntityType(), node.getName());
    if (fieldTypeResolver.hasDollarSegment(name)) {
      return transformFieldWithDollarSegments(name);
    }
    return "$" + name;
  }

  private Object transformFieldWithDollarSegments(String name) {
    if (!name.startsWith("$")) {
      return "$" + name;
    }
    Object expression = "$$ROOT";
    for (String segment : name.split("\\.", -1)) {
      Document getField = new Document();
      if (segment.startsWith("$")) {
        getField.append("field", new Document("$literal", segment));
      } else {
        getField.append("field", segment);
      }
      getField.append("input", expression);
      expression = new Document("$getField", getField);
    }
    return expression;
  }

  @Override
  @Nullable
  public Object transformInput(InputNode node) {
    return BsonValues.toBson(castIfNeeded(node.getValue(), targetTypes.get(node)));
  }

  @Override
  public Object transformPriority(PriorityNode node) {
    return transform(node.getNode());
  }

  @Override
  public Object transformPlaceholder(PlaceholderNode node) {
    return filterNodeProcessorFactories
        .getPlaceholderProcessorFactory()
        .process(this, node);
  }

  @Override
  public Object transformFunction(FunctionNode node) {
    return filterNodeProcessorFactories
        .getFunctionProcessorFactory()
        .process(this, node);
  }

  @Override
  public Object transformCollectionLike(CollectionLikeNode node) {
    Object field = transform(node.getLeft());
    String options = node.getOperator() instanceof InsensitiveLikeOperator ? "i" : "";
    List<Object> alternatives = new ArrayList<>();
    for (FilterNode pattern : node.getPatterns()) {
      Object regex = pattern instanceof InputNode inputNode
          ? createRegex(String.valueOf(inputNode.getValue()))
          : transform(pattern);
      alternatives.add(new Document("$regexMatch", new Document("input", field)
          .append("regex", regex)
          .append("options", options)));
    }
    return alternatives.size() == 1 ? alternatives.get(0) : new Document("$or", alternatives);
  }

  @Override
  public Object transformCollection(CollectionNode node) {
    if (targetTypes.containsKey(node)) {
      node
          .getItems()
          .forEach(i -> registerTargetType(i, targetTypes.get(node)));
    }
    List<Object> items = new ArrayList<>(node.getItems().size());
    for (FilterNode item : node.getItems()) {
      items.add(transform(item));
    }
    return items;
  }

  @Override
  public Object transformPrefixOperation(PrefixOperationNode node) {
    return filterNodeProcessorFactories
        .getOperationProcessorFactory()
        .process(this, node);
  }

  @Override
  public Object transformInfixOperation(InfixOperationNode node) {
    return filterNodeProcessorFactories
        .getOperationProcessorFactory()
        .process(this, node);
  }

  @Override
  public Object transformPostfixOperation(PostfixOperationNode node) {
    return filterNodeProcessorFactories
        .getOperationProcessorFactory()
        .process(this, node);
  }

  public FilterBsonTransformer registerTargetType(FilterNode node,
      @Nullable Class<?> targetType) {
    targetTypes.put(node, targetType);
    return this;
  }

  @Nullable
  public Class<?> getRegisteredTargetType(FilterNode node) {
    return targetTypes.get(node);
  }

  @Nullable
  private Object castIfNeeded(@Nullable Object value, @Nullable Class<?> targetType) {
    if (value != null && targetType != null && !targetType.isAssignableFrom(value.getClass())) {
      return conversionService.convert(value, targetType);
    }
    return value;
  }

  public Class<?> getEntityType() {
    return entityType;
  }

  private String createRegex(String input) {
    if (!input.contains("*")) {
      return ".*" + sanitizeRegexInput(input) + ".*";
    }
    return sanitizeRegexInput(input.replace("*", "X_WILDCARD_X")).replace("X_WILDCARD_X", ".*");
  }

  private String sanitizeRegexInput(String input) {
    return input.replaceAll("[-.\\+*?\\[^\\]$(){}=!<>|:\\\\]", "\\\\$0");
  }

}
