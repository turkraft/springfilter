package com.turkraft.springfilter.example.repository;

import com.turkraft.springfilter.example.model.Company;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository
    extends CrudRepository<Company, Integer>, JpaSpecificationExecutor<Company> {

}
