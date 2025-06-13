package com.mitec.business.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.mitec.business.dto.ATMForStatistical;
import com.mitec.business.dto.PeriodDto;
import com.mitec.business.dto.StatisticalAmountDto;
import com.mitec.business.dto.StatisticalContracDto;
import com.mitec.business.dto.StatisticalContract;
import com.mitec.business.dto.StatisticalDepartmeDto;
import com.mitec.business.dto.StatisticalRegion;
import com.mitec.business.dto.StatisticalRegionDto;
import com.mitec.business.model.ATM;
import com.mitec.business.model.Contract;
import com.mitec.business.model.Department;
import com.mitec.business.model.Period;
import com.mitec.business.model.Region;
import com.mitec.business.model.Statistical;
import com.mitec.business.model.User;
import com.mitec.business.repository.ATMRepository;
import com.mitec.business.repository.AccessoryRepository;
import com.mitec.business.repository.ContractRepository;
import com.mitec.business.repository.DepartmentRepository;
import com.mitec.business.repository.PeriodRepository;
import com.mitec.business.repository.RegionRepository;
import com.mitec.business.repository.StatisticalRepository;
import com.mitec.business.repository.UserRepository;
import com.mitec.business.specification.ObjectSpecification;
import com.mitec.business.utils.ContractTypeEnum;
import com.mitec.business.utils.CustomFormatDate;
import com.mitec.business.utils.JobStatusEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StatisticalService {

	@Autowired
	private StatisticalRepository statisticalRepository;
	@Autowired
	private ContractRepository contractRepository;
	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private PeriodRepository periodRepository;
	@Autowired
	private ObjectSpecification objectSpecification;
	@Autowired
	private ATMRepository atmRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AccessoryRepository accessoryRepository;

	private static final String CONTRACT_ID = "contractId";
	private static final String REGION_ID = "regionId";
	private static final String DEPARTMENT_ID = "departmentId";
	private static final String START_TIME = "startTime";
	private static final String END_TIME = "endTime";
	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd";

	// init data view thống kê số lượng máy
	public Set<StatisticalRegion> totalAmountRegion() {
		Set<StatisticalRegion> statisticalRegions = new HashSet<>();
		
		// Get data from database
		List<StatisticalContract> statisticalContracts = statisticalRepository.loadViewDataStatisticalRegion();
		
		for (StatisticalContract contract : statisticalContracts) {
			StatisticalRegion statisticalRegion = new StatisticalRegion();
			statisticalRegion.setRegionId(contract.getRegionId());
			statisticalRegion.setRegionName(contract.getRegionName());
			statisticalRegion.setCountAtm(0L);
			statisticalRegions.add(statisticalRegion);
		}
		
		statisticalRegions = statisticalRegions.stream().map(item -> {
			statisticalContracts.stream().forEach(contract -> {
				if (contract.getRegionId().equals(item.getRegionId())) {
					List<StatisticalContract> list = item.getStatisticalContract() == null ? new ArrayList<>() : item.getStatisticalContract();
					list.add(contract);
					item.setCountAtm(item.getCountAtm() + contract.getCountAtm());
					item.setStatisticalContract(list);
				}
			});
			return item;
		}).collect(Collectors.toSet());

		return statisticalRegions;
	}
	
	public Set<StatisticalRegion> totalAmountRegionExcel(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		
		LocalDateTime startTime = CustomFormatDate.formatStartTimeForInput(ob.isNull(START_TIME) ? null : ob.getString(START_TIME));
		LocalDateTime endTime = CustomFormatDate.formartEndTimeForInput(ob.isNull(END_TIME) ? null : ob.getString(END_TIME));
		if (startTime == null) startTime = LocalDateTime.now().minusYears(10l);
		
		Set<StatisticalRegion> statisticalRegions = regionRepository.findAll().stream().map(item -> new StatisticalRegion(item.getId(), item.getName())).collect(Collectors.toSet());
		
		// Get data from database
		List<Object[]> listOb = statisticalRepository.searchDataStatisticalRegion(startTime, endTime);
		
		// convert Object[] -> List<StatisticalContract>
		List<StatisticalContract> statisticalContracts = new ArrayList<>();
		for (Object[] object : listOb) {
			StatisticalContract contract = new StatisticalContract(Long.valueOf(object[0].toString()), 
					object[1] != null ? Long.valueOf(object[1].toString()) : null, Long.valueOf(object[2].toString()), 
					String.valueOf(object[3]), object[4] != null ? Long.valueOf(object[4].toString()) : 0L, 
					object[5] != null ? Long.valueOf(object[5].toString()) : 0L, object[6] != null ? Long.valueOf(object[6].toString()) : 0L);
			contract.setAverage(new DecimalFormat("##.##").format(1.0d * contract.getCountService() / contract.getCountAtm()));
			if (contract.getDepartmentId() != null) {
				Department department = departmentRepository.getById(contract.getDepartmentId());
				contract.setDepartmentName(department.getName());
			}else {
				contract.setDepartmentName("");
			}
			statisticalContracts.add(contract);
		}

		// filter data
		if (!ob.isNull(CONTRACT_ID) && StringUtils.isNotBlank(ob.getString(CONTRACT_ID))) {
			Long contractId = ob.getLong(CONTRACT_ID);
			statisticalContracts = statisticalContracts.stream().filter(item -> item.getContractId().equals(contractId)).collect(Collectors.toList());
		}
		if (!ob.isNull(REGION_ID) && StringUtils.isNotBlank(ob.getString(REGION_ID))) {
			Long regionId = ob.getLong(REGION_ID);
			statisticalContracts = statisticalContracts.stream().filter(item -> item.getRegionId().equals(regionId)).collect(Collectors.toList());
		}
		if (!ob.isNull(DEPARTMENT_ID) && StringUtils.isNotBlank(ob.getString(DEPARTMENT_ID))) {
			Long departmentId = ob.getLong(DEPARTMENT_ID);
			statisticalContracts = statisticalContracts.stream().filter(item -> item.getDepartmentId().equals(departmentId)).collect(Collectors.toList());
		}
		
		// result
		for (StatisticalContract contract : statisticalContracts) {
			statisticalRegions = statisticalRegions.stream().map(item -> {
				if (item.getRegionId().equals(contract.getRegionId())) {
					List<StatisticalContract> contracts = new ArrayList<>();
					if (item.getStatisticalContract() != null && !item.getStatisticalContract().isEmpty()) {
						contracts = item.getStatisticalContract();
					}
					item.setCountAtm(item.getCountAtm() + contract.getCountAtm());
					item.setCountService(item.getCountService() + contract.getCountService());
					item.setSumQuantity(item.getSumQuantity() + contract.getSumQuantity());
					item.setCountContract(item.getCountContract() + 1L);
					
					contracts.add(contract);
					item.setStatisticalContract(contracts);
				}
				
				return item;
			}).collect(Collectors.toSet());
		}
		
		statisticalRegions = statisticalRegions.stream().filter(item -> item.getStatisticalContract() != null && !item.getStatisticalContract().isEmpty()).collect(Collectors.toSet());
		
		return statisticalRegions;
	}

	@Async
	public CompletableFuture<List<Department>> getRegion(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		List<Department> region = new ArrayList<>();
		if (!ob.getString(REGION_ID).trim().isEmpty()) {
			Long regionId = Long.valueOf(ob.getString(REGION_ID));
			for (Department item : getDepartments()) {
				Department regionitem = new Department();
				if (regionId.equals(item.getRegion().getId())) {
					regionitem.setId(item.getId());
					regionitem.setName(item.getName());
					region.add(regionitem);
				}
			}
		} else {
			for (Department item : getDepartments()) {
				Department regionitem = new Department();
				regionitem.setId(item.getId());
				regionitem.setName(item.getName());
				region.add(regionitem);
			}
		}

		return CompletableFuture.completedFuture(region);
	}
	@Async
	public CompletableFuture<List<Region>> getDepartmen(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		List<Region> region = new ArrayList<>();
		if (ob.getString(DEPARTMENT_ID).trim().isEmpty()) {
			
			for (Region item : getRegions()) {
				Region regionitem = new Region();
				
					regionitem.setId(item.getId());
					regionitem.setName(item.getName());
					region.add(regionitem);
			}

		} else {
			Long regionId = getIdRegiontite(Long.valueOf(ob.getString(DEPARTMENT_ID)));
			for (Region item : getRegions()) {
				if (regionId.equals(item.getId())) {
					Region regionitem = new Region();
					regionitem.setId(item.getId());
					regionitem.setName(item.getName());
					region.add(regionitem);
				}
			}
		}

		return CompletableFuture.completedFuture(region);
	}

	@Async
	public CompletableFuture<List<StatisticalContracDto>> totalAmounts(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);

		Long contracId = null;
		Long regionId = null;
		Long departmentId = null;
		if (!ob.isNull(CONTRACT_ID) && StringUtils.isNotBlank(ob.getString(CONTRACT_ID))) {
			contracId = Long.valueOf(ob.getString(CONTRACT_ID));
		}
		if (!ob.isNull(REGION_ID) && StringUtils.isNotBlank(ob.getString(REGION_ID))) {
			regionId = Long.valueOf(ob.getString(REGION_ID));
		}
		if (!ob.isNull(DEPARTMENT_ID) && StringUtils.isNotBlank(ob.getString(DEPARTMENT_ID))) {
			departmentId = Long.valueOf(ob.getString(DEPARTMENT_ID));
		}
		String startTimeStr = ob.isNull(START_TIME) ? null : ob.getString(START_TIME);
		String endTimeStr = ob.isNull(END_TIME) ? null : ob.getString(END_TIME);

		LocalDateTime startTime = null;
		LocalDateTime endTime = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
		if (StringUtils.isNotBlank(startTimeStr)) {
			startTime = LocalDate.parse(startTimeStr, formatter).atTime(LocalTime.MIN);
		}
		if (StringUtils.isBlank(endTimeStr)) {
			endTime = LocalDateTime.now();
		} else {
			endTime = LocalDate.parse(endTimeStr, formatter).atTime(LocalTime.MAX);
		}

		Specification<Statistical> spec = objectSpecification.totalAmount(contracId, regionId, departmentId, startTime,
				endTime);
		System.out.println(spec.toString());
		List<Statistical> list = statisticalRepository.findAll(spec);

		Map<Long, Map<Long, Map<Long, Integer>>> designationWiseTotalSalarysssss = list.stream()
				.filter(c -> c.getContractId() != null && c.getQuantity() != null).collect(
						Collectors.groupingBy(Statistical::getContractId,
								Collectors.groupingBy(Statistical::getRegionId,
										Collectors.groupingBy(Statistical::getDepartmentId,
												Collectors.summingInt(Statistical::getQuantity)))));

		List<StatisticalContracDto> statisticalContracts = new ArrayList<>();

		designationWiseTotalSalarysssss.forEach((contractIds, valuecontractId) -> {
			StatisticalContracDto statisticalContract = new StatisticalContracDto();
			List<StatisticalRegionDto> statisticalRegion = new ArrayList<>();
			List<StatisticalDepartmeDto> statisticalDepartments = new ArrayList<>();
			valuecontractId.forEach((regionIds, valueRegionId) -> {

				valueRegionId.forEach((departmentIds, valueDepartmentIds) -> {
					StatisticalDepartmeDto statisticalDepartmentitem = new StatisticalDepartmeDto();
					statisticalDepartmentitem.setId(departmentIds);
					statisticalDepartmentitem.setContractId(contractIds);
					statisticalDepartmentitem.setRegionId(regionIds);
					statisticalDepartmentitem.setQuantity(valueDepartmentIds);
					statisticalDepartmentitem.setDepartment(getDepartmentitem(departmentIds));
					Specification<ATM> specATM = objectSpecification.totalATM(contractIds, regionIds, departmentIds);
					List<ATM> atms = atmRepository.findAll(specATM);
					Long totalService = list.stream()
							.filter(c -> c.getContractId() != null && c.getDepartmentId() == departmentIds
									&& c.getRegionId() == regionIds && c.getContractId() == contractIds)
							.collect(Collectors.counting());
					statisticalDepartmentitem.setTotalAtm(atms.size());
					statisticalDepartmentitem.setTotalService(totalService);
					statisticalDepartmentitem.setTotalAccessory(valueDepartmentIds);
					statisticalDepartmentitem.setAverage(String.format("%.2f", ((double) totalService / atms.size())));
					statisticalDepartments.add(statisticalDepartmentitem);
				});

				StatisticalRegionDto statisticalRegionitem = new StatisticalRegionDto();
				statisticalRegionitem.setContractId(contractIds);
				statisticalRegionitem.setId(regionIds);
				statisticalRegionitem.setRegion(getRegiontitem(regionIds));
				Long totalServiceDepartments = statisticalDepartments.stream().collect(Collectors.counting());
				statisticalRegionitem.setCountDepartment((int) (long) totalServiceDepartments);

				statisticalRegionitem.setStatisticalDepartment(statisticalDepartments);
				statisticalRegion.add(statisticalRegionitem);

			});
			statisticalContract.setId(contractIds);
			statisticalContract.setCountDepartment(statisticalDepartments.size());
			int totalDepartment = 0;
			for (StatisticalRegionDto statisticalRegions : statisticalRegion) {

				totalDepartment += statisticalRegions.getStatisticalDepartment().stream()
						.collect(Collectors.counting());

			}
			statisticalContract.setCountDepartment(totalDepartment);
			statisticalContract.setContract(getContractitem(contractIds));
			statisticalContract.setStatisticalRegion(statisticalRegion);
			statisticalContracts.add(statisticalContract);

		});

		return CompletableFuture.completedFuture(statisticalContracts);
	}

	@Async
	public CompletableFuture<List<StatisticalAmountDto>> personStatistical(String startTimeStr, String endTimeStr) {
		LocalDateTime startTime = null;
		LocalDateTime endTime = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
		if (StringUtils.isNotBlank(startTimeStr)) {
			startTime = LocalDate.parse(startTimeStr, formatter).atTime(LocalTime.MIN);
		} else {
			startTime = LocalDateTime.now().minusMonths(6L);
		}
		if (StringUtils.isBlank(endTimeStr)) {
			endTime = LocalDateTime.now();
		} else {
			endTime = LocalDate.parse(endTimeStr, formatter).atTime(LocalTime.MAX);
		}

		List<Object[]> list = statisticalRepository.getTotalAmountIncluldeMobilePerPerson(getListIds(), startTime, endTime);
		return CompletableFuture.completedFuture(convertToList(list));

	}

	@Async
	public CompletableFuture<List<StatisticalAmountDto>> personStatistical(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		LocalDateTime startTime = null;
		LocalDateTime endTime = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
		if (StringUtils.isNotBlank(ob.getString(START_TIME))) {
			startTime = LocalDate.parse(ob.getString(START_TIME), formatter).atTime(LocalTime.MIN);
		} else {
			startTime = LocalDateTime.now().minusMonths(6L);
		}
		if (StringUtils.isBlank(ob.getString(END_TIME))) {
			endTime = LocalDateTime.now();
		} else {
			endTime = LocalDate.parse(ob.getString(END_TIME), formatter).atTime(LocalTime.MAX);
		}

		List<Object[]> list = statisticalRepository.getTotalAmountIncluldeMobilePerPerson(getListIds(), startTime, endTime);

		return CompletableFuture.completedFuture(convertToList(list));
	}

	private List<Long> getListIds() {
		List<User> users = userRepository.getUserWithRoleDeployment();

		return users.stream().filter(item -> {
			List<String> roleNames = item.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList());
			return !roleNames.contains("ROLE_MONITOR");
		}).map(i -> i.getId()).collect(Collectors.toList());
	}

	private List<StatisticalAmountDto> convertToList(List<Object[]> list) {
		List<StatisticalAmountDto> result = new ArrayList<>();
		if (list != null && !list.isEmpty()) {
			result = list.stream().map(item -> {
				StatisticalAmountDto statisticalAmountDto = new StatisticalAmountDto();
				statisticalAmountDto.setUsername((String) item[0]);
				BigInteger totalServices = (BigInteger) item[1];
				BigDecimal totalAccessories = (BigDecimal) item[2];
				
				//Thành thêm số lần hướng dẫn từ xa qua điện thoại
				BigInteger totalMobile = (BigInteger) item[3];
				statisticalAmountDto.setTotalServices(totalServices == null ? null : totalServices.longValue());
				statisticalAmountDto
						.setTotalAccessories(totalAccessories == null ? null : totalAccessories.longValue());
				statisticalAmountDto.setTotalMobile(totalMobile == null ? null : totalMobile.longValue());

				return statisticalAmountDto;
			}).collect(Collectors.toList());
		}
		return result;
	}

	public List<Contract> getContracts() {
		return contractRepository.findAll();
	}
	
	public List<Contract> getContractByType() {
		return contractRepository.findByType(ContractTypeEnum.HOP_DONG_TAP_TRUNG.getKey());
	}
	
	public List<Contract> getCurrenContracts() {
		return contractRepository.getCurrentContract();
	}

	public String getDepartmentitem(Long a) {
		for (Department item : getDepartments()) {
			if (a.equals(item.getId())) return item.getName();
		}
		return "";
	}

	public String getContractitem(Long a) {
		for (Contract item : getContracts()) {
			if (a.equals(item.getId())) return item.getName();
		}
		return "";
	}
	public Long getIdRegiontite(Long a) {
		for (Department item : getDepartments()) {
			if (a.equals(item.getId())) return item.getRegion().getId();
		}
		return null;
	}

	public String getRegiontitem(Long a) {
		for (Region item : getRegions()) {
			if (a.equals(item.getId()))	return item.getName();
		}
		return "";
	}

	public List<Region> getRegions() {
		return regionRepository.findAll();
	}

	public List<Department> getDepartments() {
		return departmentRepository.findAll();
	}

	@Async
	public CompletableFuture<List<ATMForStatistical>> cyclicalStatistics(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		if (StringUtils.isNotBlank(ob.getString(CONTRACT_ID))) {
			Contract contract = contractRepository.findById(ob.getLong(CONTRACT_ID)).orElse(null);

			if (contract != null) {
				LocalDateTime startTime = CustomFormatDate.formatStartTimeForInput(ob.getString(START_TIME));
				LocalDateTime endTime = CustomFormatDate.formartEndTimeForInput(ob.getString(END_TIME));
				
				List<Period> periods = periodRepository.getByContractAndTime(ob.getLong(CONTRACT_ID), startTime, endTime);

				List<PeriodDto> periodDtos = new ArrayList<>();
				if (periods != null && !periods.isEmpty()) {
					for (int i = 0; i < periods.size(); i++) {
						PeriodDto periodDto = new PeriodDto();
						
						if (i == 0) {
							periodDto.setStartTime(startTime);
						}else {
							periodDto.setStartTime(periods.get(i).getStartTime());
						}
						
						periodDto.setOriginStartTime(periods.get(i).getStartTime());
						
						periodDto.setEndTime(periods.get(i).getEndTime());
						periodDto.setName("CK " + periods.get(i).getIndex());
						
						periodDtos.add(periodDto);
					}
				}

				// list ATM theo hợp đồng (chưa tính đến chu kỳ)
				List<Object[]> list = statisticalRepository.getByPeriod(ob.getLong(CONTRACT_ID), contract.getStartTime(), contract.getEndTime(), contract.getStartTime());

				List<ATMForStatistical> atms = new ArrayList<>();

				// list kết quả cuối cùng
				List<ATMForStatistical> results = new ArrayList<>();

				// convert list Object[] về dạng DTO
				if (list != null && !list.isEmpty()) {
					atms = list.stream().map(item -> {
						results.add(new ATMForStatistical());
						return new ATMForStatistical((String) item[0], (String) item[1], new ArrayList<>(),
								Long.valueOf(item[2].toString()), Boolean.parseBoolean(item[3].toString()),
								item[4] != null ? Long.valueOf(item[4].toString()) : null, item[5] == null ? 0L : Long.valueOf(item[5].toString()));
					}).collect(Collectors.toList());

					atms.sort((a1, a2) -> {
						if (a1.getOrderNumber() == null || a2.getOrderNumber() == null) {
							return 0;
						}
						return a1.getOrderNumber().compareTo(a2.getOrderNumber());
					});
				}

				for (PeriodDto period : periodDtos) {
					List<Object[]> atmForStatisticals = statisticalRepository.getByPeriod(ob.getLong(CONTRACT_ID),
							period.getStartTime(), period.getEndTime(), period.getOriginStartTime());
					for (int i = 0; i < atms.size(); i++) {
						
						if (atmForStatisticals.get(i)[0].toString().equals("5310105816")) {
							
							log.debug("Chu ky" + period.getStartTime() + " / " + period.getEndTime());
							log.debug("serial number = " + atmForStatisticals.get(i)[0].toString());
							log.debug("serial number = " + atmForStatisticals.get(i)[1].toString());
							log.debug("serial number = " + atmForStatisticals.get(i)[2].toString());
							log.debug("serial number = " + atmForStatisticals.get(i)[3].toString());
							log.debug("serial number = " + atmForStatisticals.get(i)[4].toString());
							log.debug("serial number = " + (atmForStatisticals.get(i)[5] == null ? "null" : atmForStatisticals.get(i)[5].toString()));
						}
						
						ATMForStatistical atm = results.get(i);
						atm.setSerialNumber(atms.get(i).getSerialNumber());
						atm.setAddress(atms.get(i).getAddress());
						atm.setMaxMaintenance(atms.get(i).isMaxMaintenance());

						Long atmKpsc = atm.getTotalKpsc() == null ? 0L : atm.getTotalKpsc();
						Long totalKpsc = Long.valueOf(atmForStatisticals.get(i)[2].toString());

						Long atmRemoteServices = atm.getRemoteServices() == null ? 0L : atm.getRemoteServices();
						Long totalRemoteServices = atmForStatisticals.get(i)[5] == null ? 0L : Long.valueOf(atmForStatisticals.get(i)[5].toString());
						
						boolean maxMaintenance = Boolean.parseBoolean(atmForStatisticals.get(i)[3].toString());

						atm.setTotalKpsc(atmKpsc + totalKpsc);
						atm.setRemoteServices(atmRemoteServices + totalRemoteServices);
						PeriodDto newPeriod = new PeriodDto();
						newPeriod.setName(period.getName());
						newPeriod.setStartTime(period.getStartTime());
						newPeriod.setEndTime(period.getEndTime());
						newPeriod.setMaintenance(maxMaintenance);
						
						List<PeriodDto> listPeriods = (atm.getPeriods() == null) ? new ArrayList<>() : atm.getPeriods();
						listPeriods.add(newPeriod);
						atm.setPeriods(listPeriods);
					}
				}
				return CompletableFuture.completedFuture(results);
			}
		}
		return new CompletableFuture<>();
	}

	public List<Map<String, Object>> getServiceByUser(String username) {
		List<Map<String, Object>> result = new ArrayList<>();
		List<Statistical> statisticals = statisticalRepository.findByUsernameOrderByJobCompleteTimeDesc(username);

		if (statisticals != null && !statisticals.isEmpty()) {
			result = statisticals.stream().map(item -> {
				Map<String, Object> data = new HashMap<>();
				data.put("time", CustomFormatDate.formatLocalDate(item.getJobCompleteTime(), "dd-MM-yyyy"));
				ATM atm = atmRepository.getById(item.getSerialNumber());
				data.put("serialNumber", item.getSerialNumber());
				data.put("address", atm.getAddress());
				data.put("isMaintenance", item.isMaintenance() ? "Bảo trì" : "");
				data.put("status", JobStatusEnum.fromKey(item.getStatus()).getValue());

				return data;
			}).collect(Collectors.toList());
		}

		return result;
	}
	
	public List<Map<String, Object>> getServiceByUser(String username, String startTimeStr, String endTimeStr) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		LocalDateTime startTime = CustomFormatDate.formartEndTime(startTimeStr);
		LocalDateTime endTime = CustomFormatDate.formartEndTime(endTimeStr);
		
		List<Statistical> statisticals = statisticalRepository.getByUsernameAndJobCompleteTime(username, startTime, endTime);

		if (statisticals != null && !statisticals.isEmpty()) {
			result = statisticals.stream().map(item -> {
				Map<String, Object> data = new HashMap<>();
				data.put("time", CustomFormatDate.formatLocalDate(item.getJobCompleteTime(), "dd-MM-yyyy"));
				ATM atm = atmRepository.getById(item.getSerialNumber());
				data.put("serialNumber", item.getSerialNumber());
				data.put("address", atm.getAddress());
				data.put("isMaintenance", item.isMaintenance() ? "Bảo trì" : "");
				data.put("status", JobStatusEnum.fromKey(item.getStatus()).getValue());

				return data;
			}).collect(Collectors.toList());
		}

		return result;
	}

	public List<Map<String, Object>> getListContractATM(Long contractId, Long monthsBetween, String endTime) {
		List<Map<String, Object>> result = new ArrayList<>();
		List<Object[]> listAccessory = accessoryRepository.findListAllAccessory();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		listAccessory.stream().forEach(value -> {
			
			List<Long> listCount = new ArrayList<>();
			Map<String, Object> atm = new HashMap<>();
			atm.put("id", value[0]);
			atm.put("name", value[1]);
			atm.put("series", value[2]);
			for (Long i = monthsBetween; i >= 0; i--) {
				LocalDateTime firstDateTime;

				if (StringUtils.isNotBlank(endTime)) {
					if (endTime.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {
						firstDateTime = LocalDateTime.parse(endTime + " 00:00:00", formatter);
					} else {
						firstDateTime = LocalDateTime.now();
					}
					
				} else {
					firstDateTime = LocalDateTime.now();
				}
				firstDateTime = firstDateTime.minusMonths(i);
				int year = firstDateTime.getYear();
				int month = firstDateTime.getMonthValue();
				int yearEnd = firstDateTime.plusMonths(1).getYear();
				int monthEnd = firstDateTime.plusMonths(1).getMonthValue();
				String month1;
				String month2;
				if (month < 10) {
					month1 = "0" + month;
				} else {
					month1 = String.valueOf(month);
				}

				if (monthEnd < 10) {
					month2 = "0" + monthEnd;
				} else {
					month2 = String.valueOf(monthEnd);
				}
				Long count;

				count = accessoryRepository.sumQuantityAccessory(Long.parseLong(value[0].toString()), contractId,
							LocalDateTime.parse(year + "-" + month1 + "-01 00:00:00", formatter),
							LocalDateTime.parse(yearEnd + "-" + month2 + "-01 00:00:00", formatter));
				if (count == null) {
					count = 0L;
				}
				listCount.add(count);
			}
			atm.put("count", listCount);
			result.add(atm);
		});
		return result;
	}

	public Map<Object, Object> statiscalAmountServices(Long contractId, String regionId, Long departmentId,
			String startTime, String endTime) throws ParseException {
		Map<Object, Object> result = new HashMap<>();
		Long inMonth = 4L;
		
		if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
			startTime = CustomFormatDate.formatDate(startTime, "dd/MM/yyyy", "yyyy-MM-dd");
			endTime = CustomFormatDate.formatDate(endTime, "dd/MM/yyyy", "yyyy-MM-dd");
		}

		LocalDate startDate = StringUtils.isNotBlank(startTime) ? LocalDate.parse(startTime) : null;
		LocalDate endDate = StringUtils.isNotBlank(endTime) ? LocalDate.parse(endTime) : LocalDate.now();

		int currentMonth = endDate.getMonthValue();
		
		if (startDate != null) {
			Long inYears = ChronoUnit.YEARS.between(startDate, endDate);
			if (inYears > 0) {
				inMonth = ChronoUnit.MONTHS.between(startDate.plusYears(inYears), endDate);
				inMonth = inMonth + inYears*12;
			}else {
				inMonth = ChronoUnit.MONTHS.between(startDate.plusYears(inYears), endDate);
			}
		}

		List<Map<Object, Object>> data = getDepartmentForStatistical(regionId != null ? regionId : "", departmentId);
		

		for (int i = inMonth.intValue() + 1; i > 0; i--) {
			data = amountServicePerMonth(getDataFromDb(currentMonth - i + 1, contractId, StringUtils.isNotBlank(regionId) ? Long.valueOf(regionId) : null, departmentId), currentMonth - i + 1, data);
		}
		
		result.put("data", data);
		result.put("months", getMonths(currentMonth, inMonth, false));
		return result;
	}
	
	private List<String> getMonths(int month, Long inMonth, boolean isChart) {
		List<String> list = new ArrayList<>();
		int year = LocalDate.now().getYear();
		for (int i = inMonth.intValue() + 1; i > 0; i--) {
			while (month - i < 0 || month - i > 11) {
				if (month - i < 0) {
					month = month + 12;
					year = year - 1;
				}
				if (month - i > 11) {
					month = month - 12;
					year = year + 1;
				}
			}
			
			if (isChart) {
				list.add((month - i + 1) + "/" + year);
			}else {
				list.add("Tháng " + (month-i+1) + "/" + year);
			}
		}
		return list;
	}

	private List<Map<Object, Object>> getDepartmentForStatistical(String regionId, Long departmentId) {
		if (regionId != null) {
			Specification<Department> spec = objectSpecification.getDepartmentForStatistical(StringUtils.isBlank(regionId) ? null : Long.valueOf(regionId), departmentId);
			List<Department> departments = departmentRepository.findAll(spec);
			
			Set<String> departmentSet = new HashSet<>();
			departments.stream().forEach(item -> {
				if (item.getRegion() != null) {
					departmentSet.add(item.getRegion().getName());
				}
				departmentSet.add(item.getName());
			});
			
			return departmentSet.stream().map(item -> {
				Map<Object, Object> data = new HashMap<>();
				data.put("name", item);
				data.put("months", new ArrayList<Map<Object, Object>>());
				return data;
			}).collect(Collectors.toList());
		}else {
			List<Region> regions = regionRepository.findAll();
			
			return regions.stream().map(item -> {
				Map<Object, Object> data = new HashMap<>();
				data.put("name", item.getName());
				data.put("months", new ArrayList<Map<Object, Object>>());
				return data;
			}).collect(Collectors.toList());
		}
	}
	
	private List<Object[]> getDataFromDb(int month, Long contractId, Long regionId, Long departmentId) {
		int year = LocalDate.now().getYear();

		if (month <= 0) {
			while (month <= 0) {
				month = month + 12;
				year = year - 1;
			}
		}

		LocalDateTime startDate = LocalDate.of(year, month, 1).atStartOfDay();
		LocalDateTime endDate = month < 12 ? LocalDate.of(year, month + 1, 1).atTime(LocalTime.MAX)
				: LocalDate.of(year + 1, 1, 1).atTime(LocalTime.MAX);

		List<Object[]> list;

//		if (contractId != null && regionId != null && departmentId != null) {
//			list = statisticalRepository.findByMonth(startDate, endDate, contractId, regionId, departmentId);
//		} else if (contractId != null && regionId == null && departmentId == null) {
//			list = statisticalRepository.findByMonthOnlyContract(startDate, endDate, contractId);
//		} else if (contractId == null && regionId != null && departmentId == null) {
//			list = statisticalRepository.findByMonthOnlyRegion(startDate, endDate, regionId);
//		} else if (contractId == null && regionId == null && departmentId != null) {
//			list = statisticalRepository.findByMonthOnlyDepartment(startDate, endDate, departmentId);
//		} else if (contractId != null && regionId != null) {
//			list = statisticalRepository.findByMonthWithContractAndRegion(startDate, endDate, contractId, regionId);
//		} else if (contractId != null) {
//			list = statisticalRepository.findByMonthWithContractAndDepartment(startDate, endDate, contractId,
//					departmentId);
//		} else if (regionId != null) {
//			list = statisticalRepository.findByMonthWithRegionAndDepartment(startDate, endDate, regionId, departmentId);
//		} else {
//			list = statisticalRepository.findByMonth(startDate, endDate);
//		}
//		
//		return list;
		return statisticalRepository.findByMonths(startDate, endDate, contractId, regionId, departmentId);
	}
	
	private List<Map<Object, Object>> amountServicePerMonth(List<Object[]> list, int monthFinal, List<Map<Object, Object>> result) {
		result = result.stream().map(item -> {
			String itemName = String.valueOf(item.get("name"));
			@SuppressWarnings("unchecked")
			List<Map<Object, Object>> months = (List<Map<Object, Object>>) item.get("months");
			if (!list.isEmpty()) {
				list.stream().forEach(iData -> {
					//department
					Long departmentId1 = iData[1] != null ? Long.valueOf(iData[1].toString()) : null;
					if (departmentId1 != null) {
						Department department = departmentRepository.getById(departmentId1);
						if (department.getName().equals(itemName)) {
							if (months.isEmpty()) {
								Map<Object, Object> monthData = new HashMap<>();
								monthData.put("month", monthFinal);
								monthData.put("countAtm", Long.valueOf(iData[2].toString()));
								monthData.put("countService", Long.valueOf(iData[3].toString()));
								
								months.add(monthData);
								item.replace("months", months);
							}else {
								boolean isContains = false;
								for (Map<Object, Object> m : months) {
									if (monthFinal == (int) m.get("month")) {
										isContains = true;
									}
								}
								if (isContains) {
									List<Map<Object, Object>> months1 = months.stream().map(m -> {
										if (monthFinal == (int) m.get("month")) {
											m.replace("countAtm", (Long) m.get("countAtm") + Long.valueOf(iData[2].toString()));
											m.replace("countService", (Long) m.get("countService") + Long.valueOf(iData[3].toString()));
										}
										return m;
									}).collect(Collectors.toList());
									item.replace("months", months1);
								}else {
									Map<Object, Object> monthData = new HashMap<>();
									monthData.put("month", monthFinal);
									monthData.put("countAtm", Long.valueOf(iData[2].toString()));
									monthData.put("countService", Long.valueOf(iData[3].toString()));
									
									months.add(monthData);
									item.replace("months", months);
								}
							}
						}
					}
				});
			}
			
			return item;
		}).filter(item -> {
			@SuppressWarnings("unchecked")
			List<Map<Object, Object>> list1 = (List<Map<Object, Object>>) item.get("months");
			return list1 != null && !list1.isEmpty();
		}).collect(Collectors.toList());

		return result;
	}
	
	public Map<Object, Object> getDataChart(Long contractId, String regionId, Long departmentId,
			String startTime, String endTime) {
		Map<Object, Object> result = new HashMap<>();
		Long inMonth = 4L;
		
		LocalDate startDate = CustomFormatDate.formatStartTimeForInput(startTime).toLocalDate();
		LocalDate endDate = CustomFormatDate.formartEndTimeForInput(endTime).toLocalDate();
		
		int currentMonth = endDate.getMonthValue();
		
		if (startDate != null) {
			Long inYears = ChronoUnit.YEARS.between(startDate, endDate);
			if (inYears > 0) {
				inMonth = ChronoUnit.MONTHS.between(startDate.plusYears(inYears), endDate);
				inMonth = inMonth + inYears*12;
			}else {
				inMonth = ChronoUnit.MONTHS.between(startDate.plusYears(inYears), endDate);
			}
		}

		List<Map<Object, Object>> data = getDepartmentForStatistical(regionId, departmentId);
		List<Map<Object, Object>> series = new ArrayList<>();
		result.put("series", series);
		
		for (int i = inMonth.intValue() + 1; i > 0; i--) {
			if (regionId != null) {
				data = amountServicePerMonth(getDataFromDb(currentMonth - i + 1, contractId, StringUtils.isNotBlank(regionId) ? Long.valueOf(regionId) : null, departmentId), currentMonth - i + 1, data);
			}else {
				data = amountServicePerMonthForChart(getDataFromDb(currentMonth - i + 1), currentMonth - i + 1, data);
			}
		}
		
		data.stream().forEach(item -> {
			String itemName = String.valueOf(item.get("name"));
			Map<Object, Object> seriesItem = new HashMap<>();
			List<Double> seriesData = new ArrayList<>();
			@SuppressWarnings("unchecked")
			List<Map<Object, Object>> months = (List<Map<Object, Object>>) item.get("months");
			if (months != null && !months.isEmpty()) {
				months.stream().forEach(monthData -> seriesData.add(Double.parseDouble(new DecimalFormat("##.##").format(1.0d * (Long) monthData.get("countService") / (Long) monthData.get("countAtm")))));
			}
			
			seriesItem.put("name", itemName);
			seriesItem.put("data", seriesData);
			
			series.add(seriesItem);
		});
		
		result.put("months", getMonths(currentMonth, inMonth, true));
		result.put("series", series);
		return result;
	}
	
	public Map<Object, Object> statiscalAmountServicesForExport(Long contractId, Long regionId, Long departmentId, LocalDateTime startDate, LocalDateTime endDate) {
		Map<Object, Object> result = new HashMap<>();
		List<Map<Object, Object>> data = new ArrayList<>();
		int currentMonth = LocalDate.now().getMonthValue();
		Long inMonth = 4L;
		
		if (startDate != null && endDate != null) {
			inMonth = ChronoUnit.MONTHS.between(startDate, endDate);
			currentMonth = endDate.getMonthValue();
		}else if (startDate != null && endDate == null) {
			inMonth = ChronoUnit.MONTHS.between(startDate, LocalDateTime.now());
		}else if (startDate == null && endDate != null) {
			currentMonth = endDate.getMonthValue();
		}
			
		int listSize = 0;
		
		for (int i = inMonth.intValue() + 1; i > 0; i--) {
			List<Object[]> list = getDataFromDb(currentMonth - i + 1, contractId, regionId, departmentId);
			Map<Object, Object> item = amountServicePerMonth(list, currentMonth - i + 1, regionId, departmentId);
			data.add(item);
			listSize = Integer.valueOf(item.get("departmentSize").toString());
		}
		
		result.put("data", data);
		result.put("months", getMonths(currentMonth, inMonth, false));
		result.put("departmentSize", listSize);
		return result;
	}
	
	private Map<Object, Object> amountServicePerMonth(List<Object[]> list, int month, Long regionId, Long departmentId) {
		Map<Object, Object> service = new HashMap<>();
		service.put("month", month);
		List<Map<Object, Object>> regions = new ArrayList<>();
		
		List<Region> regionList = new ArrayList<>();
		if (departmentId != null) {
			Region region = regionRepository.getByDepartmentId(departmentId);
			List<Department> departments =  new ArrayList<>();
			departments.add(departmentRepository.getById(departmentId));
			region.setDepartments(departments);
			regionList.add(region);
		}else if (regionId != null) {
			regionList.add(regionRepository.getById(regionId));
		}else {
			regionList.addAll(regionRepository.findAll());
		}

		int departmentSize = 0;
		for (Region item : regionList) {
			Map<Object, Object> region = new HashMap<>();
			region.put("regionId", item.getId());
			region.put("regionName", item.getName());
			List<Map<Object, Object>> departments = new ArrayList<>();
			
			for (Department i : item.getDepartments()) {
				Map<Object, Object> department = new HashMap<>();
				department.put("departmentId", i.getId());
				department.put("departmentName", i.getName());
				departments.add(department);
				departmentSize++;
			}
				
			region.put("departments", departments);
			
			regions.add(region);
		}
		
		service.put("regions", regions);
		
		if (list != null && !list.isEmpty()) {
			list.stream().forEach(item -> {
				@SuppressWarnings("unchecked")
				List<Map<Object, Object>> regionDatas = (List<Map<Object, Object>>) service.get("regions");
				regionDatas = regionDatas.stream().map(regionItem -> {
					if (regionItem.get("regionId").equals(Long.valueOf(item[0].toString()))) {
						@SuppressWarnings("unchecked")
						List<Map<Object, Object>> departments = (List<Map<Object, Object>>) regionItem.get("departments");
						departments = departments.stream().map(departmentItem -> {
							if (departmentItem.get("departmentId").equals(Long.valueOf(item[1].toString()))) {
								departmentItem.put("countService", item[3]);
								departmentItem.put("countAtm", item[2]);
							}
							return departmentItem;
						}).collect(Collectors.toList());
						
						regionItem.replace("departments", departments);
					}
					return regionItem;
				}).collect(Collectors.toList());
				
				service.replace("regions", regionDatas);
			});
		}
		
		service.put("departmentSize", departmentSize);
		return service;
	}
	
	private List<Object[]> getDataFromDb(int month) {
		int year = LocalDate.now().getYear();

		if (month <= 0) {
			while (month <= 0) {
				month = month + 12;
				year = year - 1;
			}
		}

		LocalDateTime startDate = LocalDate.of(year, month, 1).atStartOfDay();
		LocalDateTime endDate = month < 12 ? LocalDate.of(year, month + 1, 1).atTime(LocalTime.MAX)
				: LocalDate.of(year + 1, 1, 1).atTime(LocalTime.MAX);

		return statisticalRepository.getDataChartFirstTime(startDate, endDate);
	}
	
	private List<Map<Object, Object>> amountServicePerMonthForChart(List<Object[]> list, int monthFinal, List<Map<Object, Object>> result) {
		result = result.stream().map(item -> {
			String itemName = String.valueOf(item.get("name"));
			@SuppressWarnings("unchecked")
			List<Map<Object, Object>> months = (List<Map<Object, Object>>) item.get("months");
			if (!list.isEmpty()) {
				list.stream().forEach(iData -> {
					//department
					Long regionId = iData[0] != null ? Long.valueOf(iData[0].toString()) : null;
					if (regionId != null) {
						Region region = regionRepository.getById(regionId);
						if (region.getName().equals(itemName)) {
							Map<Object, Object> monthData = new HashMap<>();
							monthData.put("month", monthFinal);
							monthData.put("countAtm", Long.valueOf(iData[1].toString()));
							monthData.put("countService", Long.valueOf(iData[2].toString()));
							
							months.add(monthData);
							item.replace("months", months);
						}
					}
				});
			}
			
			return item;
		}).filter(item -> {
			@SuppressWarnings("unchecked")
			List<Map<Object, Object>> list1 = (List<Map<Object, Object>>) item.get("months");
			return list1 != null && !list1.isEmpty();
		}).collect(Collectors.toList());

		return result;
	}
}
