package com.mitec.business.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.dto.DeviceDto;
import com.mitec.business.model.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long>{

	@Query(value = "select d.* from device d where d.name = :name limit 1", nativeQuery = true)
	public Device getByNameLimit1(String name);
	
	public Page<Device> findAll(Specification<Device> spec, Pageable pageable);
	
	@Query(value = "select new com.mitec.business.dto.DeviceDto(d.id, d.name) from Device d order by d.id desc")
	public List<DeviceDto> findAllDto();
	
	@Query(value = "select new com.mitec.business.dto.DeviceDto(d.id, d.name) from Device d where d.id = :id")
	public DeviceDto getDtoById(Long id);
}
