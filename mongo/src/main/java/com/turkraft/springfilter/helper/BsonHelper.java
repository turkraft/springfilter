package com.turkraft.springfilter.helper;

import com.turkraft.springfilter.parser.node.InfixOperationNode;
import com.turkraft.springfilter.transformer.FilterBsonTransformer;
import org.bson.Document;

public interface BsonHelper {

  Document wrapWithMongoExpression(Object expression);

  Object transform(FilterBsonTransformer transformer, InfixOperationNode source,
      String mongoOperator);

}
