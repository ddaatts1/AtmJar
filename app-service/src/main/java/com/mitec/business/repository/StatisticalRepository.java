package com.mitec.business.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.dto.StatisticalContract;
import com.mitec.business.model.Statistical;

@Repository
public interface StatisticalRepository extends JpaRepository<Statistical, Long> {
	
	public List<Statistical> findAll(Specification<Statistical> spec);
	
	public Optional<Statistical> findByJobId(Long jobId);
	
	@Transactional
	public void deleteByJobId(Long jobId);

	@Query(value = "select u.username, ct.count_job, ct.sum_quantity from user u "
			+ "left join "
			+ "(select s.username as username, count(s.job_id) as count_job, sum(s.quantity) as sum_quantity from statistical s "
			+ "where s.job_complete_time between :startTime and :endTime "
			+ "group by s.username) as ct "
			+ "on u.username = ct.username "
			+ "where u.id IN :ids group by u.username, ct.count_job", nativeQuery = true)
	public List<Object[]> getTotalAmountPerPerson(List<Long> ids, LocalDateTime startTime, LocalDateTime endTime);
	
	@Query(value = "select u.username, ct.count_job, ct.sum_quantity, ct_perform.count_perform from user u "
			+ "left join "
			+ "(select s.username as username, count(s.job_id) as count_job, sum(s.quantity) as sum_quantity from statistical s "
			+ "where s.job_complete_time between :startTime and :endTime "
			+ "group by s.username) as ct "
			+ "on u.username = ct.username "
			+ "left join ( "
			+ "select s.username as username, count(s.job_id) as count_perform "
			+ "from statistical as s join kpsc as k on k.job_id = s.job_id where k.job_perform_id = 5 "
			+ "and s.job_complete_time between :startTime and :endTime "
			+ "group by username "
			+ ") as ct_perform "
			+ "on u.username = ct_perform.username "
			+ "where u.id IN :ids group by u.username, ct.count_job, ct_perform.count_perform", nativeQuery = true)
	public List<Object[]> getTotalAmountIncluldeMobilePerPerson(List<Long> ids, LocalDateTime startTime, LocalDateTime endTime);
	
	@Query(value = "select a.serial_number, a.address, \r\n"
			+ "case when a1.countJob is null then '0' else a1.countJob end, \r\n"
			+ "case when a2.isMaintenance = 1 then 'true' else 'false' end, \r\n"
			+ "ca.order_number, \r\n"
			+ "case when a1.remoteService is null then '0' else a1.remoteService end \r\n"
			+ "from atm a\r\n"
			+ "left join \r\n"
			+ "(\r\n"
			+ "	select serial_number, count(serial_number) as countJob, sum(is_remote_service) as remoteService\r\n"
			+ "	from statistical\r\n"
			+ "	where job_complete_time between :startTime and :endTime \r\n"
			+ "	group by serial_number\r\n"
			+ ") as a1 on a.serial_number = a1.serial_number\r\n"
			+ "left join \r\n"
			+ "(\r\n"
			+ "	select serial_number, max(is_maintenance) as isMaintenance from statistical \r\n"
			+ "	where job_complete_time between :originStartTime and :endTime \r\n"
			+ "	group by serial_number\r\n"
			+ ") as a2 on a.serial_number = a2.serial_number\r\n"
			+ "join contract_atm ca \r\n"
			+ "on a.serial_number = ca.serial_number\r\n"
			+ "where ca.contract_id = :contractId \r\n"
			+ "order by ca.order_number", nativeQuery = true)
	public List<Object[]> getByPeriod(Long contractId, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime originStartTime);
	
	@Transactional
	public void deleteAll();
	
	public List<Statistical> findByUsernameOrderByJobCompleteTimeDesc(String username);
	
	@Query(value = "select s from Statistical s where s.username = :username and s.jobCompleteTime between :startTime and :endTime order by s.jobCompleteTime desc")
	public List<Statistical> getByUsernameAndJobCompleteTime(String username, LocalDateTime startTime, LocalDateTime endTime);
	
	// Tổng lượng service theo tháng
	@Query(value = "select a.region_id, a.department_id, count(distinct a.serial_number), count(distinct s.job_id) from atm as a "
			+ "left join "
			+ "(select * from statistical where job_complete_time between :startDate and :endDate) as s "
			+ "on a.serial_number = s.serial_number "
			+ "where a.region_id is not null and a.department_id is not null "
			+ "group by a.region_id, a.department_id", nativeQuery = true)
	public List<Object[]> findByMonth(LocalDateTime startDate, LocalDateTime endDate);
	
	@Query(value = "select a.region_id, a.department_id, count(distinct a.serial_number), count(distinct s.job_id) from ATM as a "
			+ "left join (select * from statistical where job_complete_time between :startDate and :endDate and contract_id = :contractId) as s on a.serial_number = s.serial_number "
			+ "where a.region_id is not null and a.department_id is not null "
			+ "group by a.region_id, a.department_id", nativeQuery = true)
	public List<Object[]> findByMonthOnlyContract(LocalDateTime startDate, LocalDateTime endDate, Long contractId);
	
	@Query(value = "select a.region_id, a.department_id, count(distinct a.serial_number), count(distinct s.job_id) from ATM as a "
			+ "left join (select * from statistical where job_complete_time between :startDate and :endDate and region_id = :regionId ) as s "
			+ "on a.serial_number = s.serial_number "
			+ "where a.region_id is not null and a.department_id is not null "
			+ "group by a.region_id, a.department_id", nativeQuery = true)
	public List<Object[]> findByMonthOnlyRegion(LocalDateTime startDate, LocalDateTime endDate, Long regionId);
	
	@Query(value = "select a.region_id, a.department_id, count(distinct a.serial_number), count(distinct s.job_id) from ATM as a "
			+ "left join (select * from statistical where job_complete_time between :startDate and :endDate and department_id = :departmentId) as s on a.serial_number = s.serial_number "
			+ "where a.region_id is not null and a.department_id is not null "
			+ "group by a.region_id, a.department_id", nativeQuery = true)
	public List<Object[]> findByMonthOnlyDepartment(LocalDateTime startDate, LocalDateTime endDate, Long departmentId);
	
	@Query(value = "select a.region_id, a.department_id, count(distinct a.serial_number), count(distinct s.job_id) from ATM as a "
			+ "left join (select * from statistical where job_complete_time between :startDate and :endDate and contract_id = :contractId and department_id = :departmentId) as s on a.serial_number = s.serial_number "
			+ "where a.region_id is not null and a.department_id is not null "
			+ "group by a.region_id, a.department_id", nativeQuery = true)
	public List<Object[]> findByMonthWithContractAndDepartment(LocalDateTime startDate, LocalDateTime endDate, Long contractId, Long departmentId);
	
	@Query(value = "select a.region_id, a.department_id, count(distinct a.serial_number), count(distinct s.job_id) from ATM as a "
			+ "left join (select * from statistical where job_complete_time between :startDate and :endDate and contract_id = :contractId and region_id = :regionId) as s on a.serial_number = s.serial_number "
			+ "where a.region_id is not null and a.department_id is not null "
			+ "group by a.region_id, a.department_id", nativeQuery = true)
	public List<Object[]> findByMonthWithContractAndRegion(LocalDateTime startDate, LocalDateTime endDate, Long contractId, Long regionId);
	
	@Query(value = "select a.region_id, a.department_id, count(distinct a.serial_number), count(distinct s.job_id) from ATM as a "
			+ "left join (select * from statistical where job_complete_time between :startDate and :endDate and region_id = :regionId and department_id = :departmentId) as s on a.serial_number = s.serial_number "
			+ "where a.region_id is not null and a.department_id is not null "
			+ "group by a.region_id, a.department_id", nativeQuery = true)
	public List<Object[]> findByMonthWithRegionAndDepartment(LocalDateTime startDate, LocalDateTime endDate, Long regionId, Long departmentId);
	
	@Query(value = "select a.region_id, a.department_id, count(distinct a.serial_number), count(distinct s.job_id) from ATM as a "
			+ "left join (select * from statistical where job_complete_time between :startDate and :endDate and region_id = :regionId and contract_id = :contractId and department_id = :departmentId) as s "
			+ "on a.serial_number = s.serial_number "
			+ "where a.region_id is not null and a.department_id is not null "
			+ "group by a.region_id, a.department_id", nativeQuery = true)
	public List<Object[]> findByMonth(LocalDateTime startDate, LocalDateTime endDate, Long contractId, Long regionId, Long departmentId);
	
	@Query(value = "select a.region_id, a.department_id, count(distinct a.serial_number), count(distinct s.job_id) from ATM as a \r\n"
			+ "left join \r\n"
			+ "(\r\n"
			+ "	select * from statistical \r\n"
			+ "    where job_complete_time between :startDate and :endDate \r\n"
			+ "    and (:regionId is null or region_id = 1)\r\n"
			+ "    and (:contractId is null or contract_id = 1)\r\n"
			+ "    and (:departmentId is null or department_id = 1)\r\n"
			+ ") as s \r\n"
			+ "on a.serial_number = s.serial_number \r\n"
			+ "where a.region_id is not null and a.department_id is not null \r\n"
			+ "group by a.region_id, a.department_id", nativeQuery = true)
	public List<Object[]> findByMonths(LocalDateTime startDate, LocalDateTime endDate, Long contractId, Long regionId, Long departmentId);

	//Home data
	@Query(value = "select t.ma_hd, name, type, serial_number, address, \r\n"
			+ "(select max(is_maintenance) from job where serial_number = t.serial_number and check_in_time between t.start_time and t.end_time) as tinh_trang_bao_tri, \r\n"
			+ "case \r\n"
			+ "when MIN(status) = 0 THEN 'Đang service' \r\n"
			+ "else '' \r\n"
			+ "end as trang_thai, \r\n"
			+ "DATEDIFF(t.end_time, now()) as so_ngay_ket_thuc_ck, \r\n"
			+ "(select 'Cảnh báo' from job \r\n"
			+ "where is_kpsc=1 and DATE_SUB(NOW(),INTERVAL +10 DAY) < check_in_time and serial_number = t.serial_number \r\n"
			+ "GROUP BY serial_number \r\n"
			+ "having count(serial_number) > 3) as tinh_trang \r\n"
			+ "from ( \r\n"
			+ "select j1.serial_number, j1.address, c.name, c.type, p.start_time, p.end_time, j1.is_maintenance, j1.status, j1.is_kpsc, j1.check_in_time, c.id as ma_hd \r\n"
			+ "from (\r\n"
			+ "select j.is_maintenance, j.status, j.is_kpsc, j.check_in_time, a.serial_number, a.address\r\n"
			+ "    from job j right join atm a on j.serial_number = a.serial_number\r\n"
			+ ") as j1, contract_atm ca, contract c, period p \r\n"
			+ "where j1.serial_number = ca.serial_number and ca.contract_id = c.id and c.id = p.contract_id \r\n"
			+ "and j1.address like %:address%) t  \r\n"
			+ "WHERE now() BETWEEN t.start_time and t.end_time \r\n"
			+ "GROUP BY serial_number, address, name, t.ma_hd, so_ngay_ket_thuc_ck, type, t.start_time, t.end_time\r\n"
			+ "ORDER BY so_ngay_ket_thuc_ck asc", nativeQuery = true)
	public List<Object[]> getHomeData(String address);
	
	@Query(value = "select new com.mitec.business.dto.StatisticalContract(a.region.id, r.name, c.id, c.name, count(distinct a.serialNumber)) "
			+ "from ATM a "
			+ "join ContractAtm ca on a.serialNumber = ca.atm.serialNumber "
			+ "join Contract c on c.id = ca.contract.id "
			+ "join Region r on a.region.id = r.id "
			+ "where now() between c.startTime and c.endTime "
			+ "group by a.region.id, r.name, c.id, c.name")
	public List<StatisticalContract> loadViewDataStatisticalRegion();
	
	@Query(value = "select a.region_id as regionId, a.department_id as departmentId, c.id as contractId, c.name as contractName, \r\n"
			+ "count(distinct a.serial_number) as countAtm, s.countService, s.quantity\r\n"
			+ "from atm a \r\n"
			+ "join contract_atm ca on a.serial_number = ca.serial_number\r\n"
			+ "join contract c on c.id = ca.contract_id \r\n"
			+ "left join \r\n"
			+ "(\r\n"
			+ "select region_id, department_id , contract_id, count(distinct job_id) as countService, sum(quantity) as quantity from statistical\r\n"
			+ "where job_complete_time between :startTime and :endTime \r\n"
			+ "group by region_id, department_id, contract_id\r\n"
			+ ") s on s.region_id = a.region_id and s.department_id = a.department_id and s.contract_id = c.id\r\n"
			+ "where now() between c.start_time and c.end_time \r\n"
			+ "group by a.region_id, a.department_id, c.id, c.name, s.countService, s.quantity", nativeQuery = true)
	public List<Object[]> searchDataStatisticalRegion(LocalDateTime startTime, LocalDateTime endTime);
	
	@Query(value = "select a.region_id, count(distinct a.serial_number), count(distinct s.job_id) from atm as a \r\n"
			+ "left join \r\n"
			+ "(select * from statistical where job_complete_time between :startDate and :endDate) as s \r\n"
			+ "on a.serial_number = s.serial_number \r\n"
			+ "where a.region_id is not null \r\n"
			+ "group by a.region_id ", nativeQuery = true)
	public List<Object[]> getDataChartFirstTime(LocalDateTime startDate, LocalDateTime endDate);
	
	@Query(value = "select case when count(*) > 0 then 'true' else 'false' end \r\n"
			+ "from statistical s\r\n"
			+ "join job j on s.job_id = j.id \r\n"
			+ "join kpsc k on j.id = k.job_id\r\n"
			+ "where s.id = :id \r\n"
			+ "and k.job_perform_id = 5", nativeQuery = true)
	public Boolean isRemoteService(Long id);
}
