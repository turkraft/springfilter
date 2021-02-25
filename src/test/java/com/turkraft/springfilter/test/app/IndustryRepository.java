package com.turkraft.springfilter.test.app;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndustryRepository
    extends CrudRepository<Industry, Integer>, JpaSpecificationExecutor<Industry> {

}
