package com.turkraft.springfilter.example.repository;

import com.turkraft.springfilter.example.model.Industry;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndustryRepository
    extends CrudRepository<Industry, Integer>, JpaSpecificationExecutor<Industry> {

}
