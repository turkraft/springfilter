package com.turkraft.springfilter.helper;

import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

@Component
public class DbRefFieldSupport {

  protected final FieldTypeResolver fieldTypeResolver;

  public DbRefFieldSupport(FieldTypeResolver fieldTypeResolver) {
    this.fieldTypeResolver = fieldTypeResolver;
  }

  public boolean isCollectionDbRefDollarField(FilterJsonNodeTransformer transformer,
      FilterNode node) {
    if (!(node instanceof FieldNode fieldNode)) {
      return false;
    }
    return fieldTypeResolver.isCollectionDbRefField(transformer.getEntityType(),
        fieldNode.getName());
  }

  public boolean isReferenceDollarField(FilterJsonNodeTransformer transformer, FilterNode node) {
    if (!(node instanceof FieldNode fieldNode)) {
      return false;
    }
    return fieldTypeResolver.isReferenceDollarField(transformer.getEntityType(),
        fieldNode.getName());
  }

  public ObjectNode regexInput(FilterJsonNodeTransformer transformer, JsonNode expression) {
    ObjectNode convert = transformer
        .getObjectMapper()
        .createObjectNode();
    convert.set("input", expression);
    convert.set("to", transformer.getObjectMapper().getNodeFactory().stringNode("string"));
    convert.set("onError", transformer.getObjectMapper().getNodeFactory().stringNode(""));
    convert.set("onNull", transformer.getObjectMapper().getNodeFactory().stringNode(""));
    return transformer
        .getObjectMapper()
        .createObjectNode()
        .set("$convert", convert);
  }

  public ObjectNode membershipComparison(FilterJsonNodeTransformer transformer, JsonNode needle,
      JsonNode haystack, boolean negate) {

    ObjectNode result = transformer
        .getObjectMapper()
        .createObjectNode()
        .set("$in", transformer
            .getObjectMapper()
            .createArrayNode()
            .add(needle)
            .add(nullSafeArray(transformer, haystack)));

    return negate ? not(transformer, result) : result;

  }

  public ObjectNode anyElementIn(FilterJsonNodeTransformer transformer, JsonNode mapped,
      JsonNode values, boolean negate) {

    JsonNode predicate = transformer
        .getObjectMapper()
        .createObjectNode()
        .set("$in", transformer
            .getObjectMapper()
            .createArrayNode()
            .add(thisReference(transformer))
            .add(values));

    return anyElement(transformer, mapped, predicate, negate);

  }

  public ObjectNode anyElementComparison(FilterJsonNodeTransformer transformer, JsonNode mapped,
      JsonNode value, String operator, boolean negate) {

    JsonNode predicate = transformer
        .getObjectMapper()
        .createObjectNode()
        .set(operator, transformer
            .getObjectMapper()
            .createArrayNode()
            .add(thisReference(transformer))
            .add(value));

    return anyElement(transformer, mapped, predicate, negate);

  }

  public ObjectNode anyElement(FilterJsonNodeTransformer transformer, JsonNode mapped,
      JsonNode predicateOnThis, boolean negate) {

    ObjectNode map = transformer
        .getObjectMapper()
        .createObjectNode();
    map.set("input", nullSafeArray(transformer, mapped));
    map.set("in", predicateOnThis);

    ObjectNode result = transformer
        .getObjectMapper()
        .createObjectNode()
        .set("$anyElementTrue", transformer
            .getObjectMapper()
            .createArrayNode()
            .add(transformer
                .getObjectMapper()
                .createObjectNode()
                .set("$map", map)));

    return negate ? not(transformer, result) : result;

  }

  private ObjectNode nullSafeArray(FilterJsonNodeTransformer transformer, JsonNode mapped) {
    return transformer
        .getObjectMapper()
        .createObjectNode()
        .set("$ifNull", transformer
            .getObjectMapper()
            .createArrayNode()
            .add(mapped)
            .add(transformer.getObjectMapper().createArrayNode()));
  }

  private JsonNode thisReference(FilterJsonNodeTransformer transformer) {
    return transformer
        .getObjectMapper()
        .getNodeFactory()
        .stringNode("$$this");
  }

  private ObjectNode not(FilterJsonNodeTransformer transformer, JsonNode node) {
    return transformer
        .getObjectMapper()
        .createObjectNode()
        .set("$not", node);
  }

}
