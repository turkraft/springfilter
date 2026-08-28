package com.turkraft.springfilter.helper;

import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterJsonNodeTransformer;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Service
public class JsonNodeHelperImpl implements JsonNodeHelper {

  protected final ObjectMapper objectMapper;

  protected final FieldTypeResolver fieldTypeResolver;

  protected final DbRefFieldSupport dbRefFieldSupport;

  public JsonNodeHelperImpl(ObjectMapper objectMapper,
      FieldTypeResolver fieldTypeResolver,
      DbRefFieldSupport dbRefFieldSupport) {
    this.objectMapper = objectMapper;
    this.fieldTypeResolver = fieldTypeResolver;
    this.dbRefFieldSupport = dbRefFieldSupport;
  }

  @Override
  public ObjectNode wrapWithMongoExpression(JsonNode node) {
    return objectMapper
        .createObjectNode()
        .set("$expr", node);
  }

  @Override
  public JsonNode transform(FilterJsonNodeTransformer transformer, InfixOperationNode source,
      String mongoOperator) {

    if (source.getLeft() instanceof FieldNode fieldNode) {
      transformer.registerTargetType(source.getRight(),
          fieldTypeResolver.resolve(transformer.getEntityType(), fieldNode.getName()));
    } else if (source.getRight() instanceof FieldNode fieldNode) {
      transformer.registerTargetType(source.getLeft(),
          fieldTypeResolver.resolve(transformer.getEntityType(), fieldNode.getName()));
    }

    JsonNode leftResult = transformer.transform(source.getLeft());

    if (transformer.getRegisteredTargetType(source.getLeft()) != null) {
      transformer.registerTargetType(source.getRight(),
          transformer.getRegisteredTargetType(source.getLeft()));
    } else if (transformer.getRegisteredTargetType(source.getRight()) != null) {
      transformer.registerTargetType(source.getLeft(),
          transformer.getRegisteredTargetType(source.getRight()));
    }

    JsonNode rightResult = transformer.transform(source.getRight());

    if (isComparisonOperator(mongoOperator)) {

      boolean leftIsCollectionDbRef =
          dbRefFieldSupport.isCollectionDbRefDollarField(transformer, source.getLeft());
      boolean rightIsCollectionDbRef = !leftIsCollectionDbRef
          && dbRefFieldSupport.isCollectionDbRefDollarField(transformer, source.getRight());

      if (leftIsCollectionDbRef || rightIsCollectionDbRef) {

        JsonNode arrayExpression = leftIsCollectionDbRef ? leftResult : rightResult;
        JsonNode valueExpression = leftIsCollectionDbRef ? rightResult : leftResult;

        if ("$eq".equals(mongoOperator) || "$ne".equals(mongoOperator)) {
          return dbRefFieldSupport.membershipComparison(transformer, valueExpression,
              arrayExpression, "$ne".equals(mongoOperator));
        }

        String operator = leftIsCollectionDbRef ? mongoOperator : flipComparison(mongoOperator);
        return dbRefFieldSupport
            .anyElementComparison(transformer, arrayExpression, valueExpression, operator, false);

      }

    }

    return transformer
        .getObjectMapper()
        .createObjectNode()
        .set(mongoOperator,
            transformer
                .getObjectMapper()
                .createArrayNode()
                .add(leftResult)
                .add(rightResult));

  }

  private static boolean isComparisonOperator(String mongoOperator) {
    return "$eq".equals(mongoOperator) || "$ne".equals(mongoOperator)
        || "$gt".equals(mongoOperator) || "$gte".equals(mongoOperator)
        || "$lt".equals(mongoOperator) || "$lte".equals(mongoOperator);
  }

  private static String flipComparison(String mongoOperator) {
    if ("$gt".equals(mongoOperator)) {
      return "$lt";
    }
    if ("$gte".equals(mongoOperator)) {
      return "$lte";
    }
    if ("$lt".equals(mongoOperator)) {
      return "$gt";
    }
    if ("$lte".equals(mongoOperator)) {
      return "$gte";
    }
    return mongoOperator;
  }

}
