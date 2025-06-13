package com.mitec.business.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.dto.ErrorDeviceDto;
import com.mitec.business.model.ErrorDevice;

@Repository
public interface ErrorDeviceRepository extends JpaRepository<ErrorDevice, Long>{

	@Query(value = "select ed.* from error_device ed where ed.name = :name limit 1", nativeQuery = true)
	public ErrorDevice getByNameLimit1(String name);
	
	public Page<ErrorDevice> findAll(Specification<ErrorDevice> spec, Pageable pageable);
	
	@Query(value = "select new com.mitec.business.dto.ErrorDeviceDto(ed.id, ed.name) from ErrorDevice ed where ed.id = :id")
	public ErrorDeviceDto getDtoById(Long id);
}
