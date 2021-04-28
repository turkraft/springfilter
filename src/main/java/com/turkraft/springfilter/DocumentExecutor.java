package com.turkraft.springfilter;

import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;

@NoRepositoryBean
public interface DocumentExecutor<T, I> {

  default Optional<T> findOne(@Nullable Document doc) {
    return Optional.ofNullable(getMongoOperations().findOne(DocumentUtils.getQueryFromDocument(doc),
        getMetadata().getJavaType()));
  }

  default List<T> findAll(@Nullable Document doc) {
    return getMongoOperations().find(DocumentUtils.getQueryFromDocument(doc),
        getMetadata().getJavaType());
  }

  default Page<T> findAll(@Nullable Document doc, Pageable pageable) {
    return new PageImpl<>(getMongoOperations()
        .find(DocumentUtils.getQueryFromDocument(doc).with(pageable), getMetadata().getJavaType()));
  }

  default List<T> findAll(@Nullable Document doc, Sort sort) {
    return getMongoOperations().find(DocumentUtils.getQueryFromDocument(doc).with(sort),
        getMetadata().getJavaType());
  }

  default long count(@Nullable Document doc) {
    return getMongoOperations().count(DocumentUtils.getQueryFromDocument(doc),
        getMetadata().getJavaType());
  }

  MongoEntityInformation<T, I> getMetadata();

  MongoOperations getMongoOperations();

}
