package com.turkraft.springfilter.helper;

import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import java.util.Arrays;
import org.bson.Document;
import org.springframework.stereotype.Service;

@Service
public class BsonHelperImpl implements BsonHelper {

  protected final FieldTypeResolver fieldTypeResolver;

  protected final DbRefBsonSupport dbRefBsonSupport;

  public BsonHelperImpl(FieldTypeResolver fieldTypeResolver,
      DbRefBsonSupport dbRefBsonSupport) {
    this.fieldTypeResolver = fieldTypeResolver;
    this.dbRefBsonSupport = dbRefBsonSupport;
  }

  @Override
  public Document wrapWithMongoExpression(Object expression) {
    return new Document("$expr", expression);
  }

  @Override
  public Object transform(FilterBsonTransformer transformer, InfixOperationNode source,
      String mongoOperator) {

    if (source.getLeft() instanceof FieldNode fieldNode) {
      transformer.registerTargetType(source.getRight(),
          fieldTypeResolver.resolve(transformer.getEntityType(), fieldNode.getName()));
    } else if (source.getRight() instanceof FieldNode fieldNode) {
      transformer.registerTargetType(source.getLeft(),
          fieldTypeResolver.resolve(transformer.getEntityType(), fieldNode.getName()));
    }

    Object leftResult = transformer.transform(source.getLeft());

    if (transformer.getRegisteredTargetType(source.getLeft()) != null) {
      transformer.registerTargetType(source.getRight(),
          transformer.getRegisteredTargetType(source.getLeft()));
    } else if (transformer.getRegisteredTargetType(source.getRight()) != null) {
      transformer.registerTargetType(source.getLeft(),
          transformer.getRegisteredTargetType(source.getRight()));
    }

    Object rightResult = transformer.transform(source.getRight());

    if (isComparisonOperator(mongoOperator)) {
      boolean leftIsCollectionDbRef =
          dbRefBsonSupport.isCollectionDbRefDollarField(transformer, source.getLeft());
      boolean rightIsCollectionDbRef = !leftIsCollectionDbRef
          && dbRefBsonSupport.isCollectionDbRefDollarField(transformer, source.getRight());
      if (leftIsCollectionDbRef || rightIsCollectionDbRef) {
        Object arrayExpression = leftIsCollectionDbRef ? leftResult : rightResult;
        Object valueExpression = leftIsCollectionDbRef ? rightResult : leftResult;
        if ("$eq".equals(mongoOperator) || "$ne".equals(mongoOperator)) {
          return dbRefBsonSupport.membershipComparison(valueExpression, arrayExpression,
              "$ne".equals(mongoOperator));
        }
        String operator = leftIsCollectionDbRef ? mongoOperator : flipComparison(mongoOperator);
        return dbRefBsonSupport.anyElementComparison(arrayExpression, valueExpression, operator,
            false);
      }
    }

    return new Document(mongoOperator, Arrays.asList(leftResult, rightResult));

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
