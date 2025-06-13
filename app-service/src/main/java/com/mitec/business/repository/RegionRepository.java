package com.mitec.business.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long>{
	
	@Query(value = "select r.* from region r where r.name = :name limit 1", nativeQuery = true)
	public Region getByNameLimit1(String name);
	
	public Page<Region> findAll(Specification<Region> spec, Pageable pageable);
	
	public List<Region> findAll(Specification<Region> spec);
	
	@Query(value = "select r.* from region r join department d on r.id = d.region_id where d = :departmentId limit 1", nativeQuery = true)
	public Region getByDepartmentId(Long departmentId);
}
