package com.mitec.business.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.dto.ATMScheduleDto;
import com.mitec.business.model.ATM;

@Repository
public interface ATMRepository extends JpaRepository<ATM, String>{
	
	@Query(value = "select a from ATM a where a.serialNumber IN :ids")
	public List<ATM> getByListId(List<String> ids);
	
	public List<ATM> findAll(Specification<ATM> spec);
	
	@Query(value = "select a from ATM a where a.serialNumber = :serialNumberQuery")
	public ATM getBySerialNumber(String serialNumberQuery);
	
	@Query(value = "select a.serial_number, a.address, c.name, c.start_time, c.end_time, c.maintenance_cycle, c.id from atm a join contract_atm ca on a.serial_number = ca.serial_number "
			+ "join contract as c on ca.contract_id = c.id "
			+ "where ca.contract_id = :contractId and c.status = 1", nativeQuery = true)
	public List<Object[]> getByContractID(Long contractId);
	
	@Query(value = "select a.serial_number, a.address, c.name, c.start_time, c.end_time, c.maintenance_cycle, c.id from atm a join contract_atm ca on a.serial_number = ca.serial_number "
			+ "join contract as c on ca.contract_id = c.id where c.status = 1", nativeQuery = true)
	public List<Object[]> getByAll();

	public Page<ATM> findAll(Specification<ATM> spec, Pageable pageable);
	
	@Query(value = "select a.serial_number as serialNumber, a.address as address, c.status as status, c.name as name from atm a join contract_atm ca on a.serial_number = ca.serial_number "
			+ "join contract as c on ca.contract_id = c.id where c.status = 1", 
			nativeQuery = true)
	public Page<Object[]> findDataContractATM(Pageable pageable);
	
	@Query(value = "select a.serial_number as serialNumber, a.address as address, c.status as status, c.name as name from atm a join contract_atm ca on a.serial_number = ca.serial_number "
			+ "join contract as c on ca.contract_id = c.id where c.status = 1 limit :pageSize offset :offset", 
			nativeQuery = true)
	public List<Object[]> findDataContractATM(int pageSize, int offset);
	
	@Query(value = "select count(*) from atm a join contract_atm ca on a.serial_number = ca.serial_number "
			+ "join contract as c on ca.contract_id = c.id where c.status = 1", 
			nativeQuery = true)
	public int countDataContractATM();
	
	@Query(value = "select new com.mitec.business.dto.ATMScheduleDto(a.serialNumber, MAX(a.status), MAX(c.endTime)) from ATM a \r\n"
			+ "join a.contracts c \r\n"
			+ "group by a.serialNumber")
	public List<ATMScheduleDto> getAtmForScheduler();
}
