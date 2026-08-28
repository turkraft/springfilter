package com.turkraft.springfilter.helper;

import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import java.util.ArrayList;
import java.util.Arrays;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public class DbRefBsonSupport {

  protected final FieldTypeResolver fieldTypeResolver;

  public DbRefBsonSupport(FieldTypeResolver fieldTypeResolver) {
    this.fieldTypeResolver = fieldTypeResolver;
  }

  public boolean isCollectionDbRefDollarField(FilterBsonTransformer transformer,
      FilterNode node) {
    if (!(node instanceof FieldNode fieldNode)) {
      return false;
    }
    return fieldTypeResolver.isCollectionDbRefField(transformer.getEntityType(),
        fieldNode.getName());
  }

  public boolean isReferenceDollarField(FilterBsonTransformer transformer, FilterNode node) {
    if (!(node instanceof FieldNode fieldNode)) {
      return false;
    }
    return fieldTypeResolver.isReferenceDollarField(transformer.getEntityType(),
        fieldNode.getName());
  }

  public Document regexInput(Object expression) {
    return new Document("$convert", new Document("input", expression)
        .append("to", "string")
        .append("onError", "")
        .append("onNull", ""));
  }

  public Document membershipComparison(Object needle, Object haystack, boolean negate) {
    Document result = new Document("$in", Arrays.asList(needle, nullSafeArray(haystack)));
    return negate ? not(result) : result;
  }

  public Document anyElementIn(Object mapped, Object values, boolean negate) {
    Document predicate = new Document("$in", Arrays.asList(thisReference(), values));
    return anyElement(mapped, predicate, negate);
  }

  public Document anyElementComparison(Object mapped, Object value, String operator,
      boolean negate) {
    Document predicate = new Document(operator, Arrays.asList(thisReference(), value));
    return anyElement(mapped, predicate, negate);
  }

  public Document anyElement(Object mapped, Object predicateOnThis, boolean negate) {
    Document map = new Document("input", nullSafeArray(mapped)).append("in", predicateOnThis);
    Document result = new Document("$anyElementTrue",
        Arrays.asList(new Document("$map", map)));
    return negate ? not(result) : result;
  }

  private Document nullSafeArray(Object mapped) {
    return new Document("$ifNull", Arrays.asList(mapped, new ArrayList<>()));
  }

  private String thisReference() {
    return "$$this";
  }

  private Document not(Object node) {
    return new Document("$not", node);
  }

}
