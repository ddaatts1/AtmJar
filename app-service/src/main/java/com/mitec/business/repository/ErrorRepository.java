package com.mitec.business.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.Error;

@Repository
public interface ErrorRepository extends JpaRepository<Error, Long>{
	public Page<Error> findAll(Specification<Error> spec, Pageable pageable);
}
