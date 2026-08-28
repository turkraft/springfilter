package com.turkraft.springfilter.converter;

import com.turkraft.springfilter.parser.node.FilterNode;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Query;

public interface FilterQueryConverter {

  Query convert(String filter, Class<?> entityClass);

  Query convert(FilterNode filter, Class<?> entityClass);

  Document convertToDocument(String filter, Class<?> entityClass);

  Document convertToDocument(FilterNode filter, Class<?> entityClass);

}
