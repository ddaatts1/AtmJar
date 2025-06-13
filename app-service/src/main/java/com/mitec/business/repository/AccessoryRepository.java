package com.mitec.business.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.Accessory;

@Repository
public interface AccessoryRepository extends JpaRepository<Accessory, Long>{
 
	public Page<Accessory> findAll(Specification<Accessory> spec, Pageable pageable);
	
	@Query(value = "SELECT a.id, a.name, GROUP_CONCAT(s.name) as s_name FROM accessory_series as ac "
			+ "JOIN accessory as a on ac.accessory_id = a.id "
			+ "JOIN series AS s On ac.series_id = s.id "
			+ "GROUP BY a.id "
			+ "ORDER BY ac.accessory_id ASC;", nativeQuery = true)
	public List<Object[]> findListAllAccessory();
	
	@Query(value = "SELECT SUM(ra.quantity) FROM accessory AS a \r\n"
			+ "JOIN replacement_accessory ra on a.id = ra.accessory_id \r\n"
			+ "JOIN kpsc k ON ra.kpsc_id = k.id \r\n"
			+ "JOIN \r\n"
			+ "(select j.* from job j \r\n"
			+ "JOIN contract_atm ca ON j.serial_number = ca.serial_number \r\n"
			+ "where :contractId is null or ca.contract_id = :contractId \r\n"
			+ "group by j.id) as j1\r\n"
			+ "ON k.job_id = j1.id \r\n"
			+ "WHERE a.id = :accessoryId\r\n"
			+ "AND j1.check_in_time between :startDate and :endDate\r\n"
			+ "GROUP BY accessory_id;", nativeQuery = true)
	public Long sumQuantityAccessory(Long accessoryId, Long contractId, LocalDateTime startDate, LocalDateTime endDate);
	
	@Query(value = "SELECT s.name FROM accessory_series AS ac "
			+"JOIN series as s on ac.series_id = s.id "
			+"WHERE ac.accessory_id = :accessoryId ;", nativeQuery = true)
	public List<Object[]> getListSeriesById(Long accessoryId);
}
