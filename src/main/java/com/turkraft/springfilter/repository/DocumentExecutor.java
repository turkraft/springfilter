package com.turkraft.springfilter.repository;

import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;

@NoRepositoryBean
public interface DocumentExecutor<T, ID> {

  Optional<T> findOne(@Nullable Document doc);

  List<T> findAll(@Nullable Document doc);

  Page<T> findAll(@Nullable Document doc, Pageable pageable);

  List<T> findAll(@Nullable Document doc, Sort sort);

  long count(@Nullable Document doc);
}
