package com.mitec.business.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.Tracking;

@Repository
public interface TrackingRepository extends JpaRepository<Tracking, Long> {
	
	public Page<Tracking> findAll(Specification<Tracking> spec, Pageable pageable);
}
