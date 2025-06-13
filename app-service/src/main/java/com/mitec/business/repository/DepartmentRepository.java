package com.mitec.business.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>{

	@Query(value = "select d.* from department d where d.name = :name limit 1", nativeQuery = true)
	public Department getByNameLimit1(String name);
	
	public Page<Department> findAll(Pageable pageable);
	
	public List<Department> findAll(Specification<Department> spec);
}
