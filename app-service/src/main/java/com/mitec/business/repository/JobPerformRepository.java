package com.mitec.business.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.JobPerform;

@Repository
public interface JobPerformRepository extends JpaRepository<JobPerform, Long>{
	public Page<JobPerform> findAll(Specification<JobPerform> spec, Pageable pageable);
}
