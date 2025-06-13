package com.mitec.business.service;

import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import com.mitec.business.model.ATM;
import com.mitec.business.model.Accessory;
import com.mitec.business.model.Bank;
import com.mitec.business.model.Contract;
import com.mitec.business.model.Department;
import com.mitec.business.model.Device;
import com.mitec.business.model.Error;
import com.mitec.business.model.ErrorDevice;
import com.mitec.business.model.Job;
import com.mitec.business.model.JobPerform;
import com.mitec.business.model.Region;
import com.mitec.business.model.Series;
import com.mitec.business.model.Statistical;
import com.mitec.business.repository.ATMRepository;
import com.mitec.business.repository.AccessoryRepository;
import com.mitec.business.repository.BankRepository;
import com.mitec.business.repository.ContractRepository;
import com.mitec.business.repository.DepartmentRepository;
import com.mitec.business.repository.DeviceRepository;
import com.mitec.business.repository.ErrorDeviceRepository;
import com.mitec.business.repository.ErrorRepository;
import com.mitec.business.repository.JobPerformRepository;
import com.mitec.business.repository.JobRepository;
import com.mitec.business.repository.RegionRepository;
import com.mitec.business.repository.ReplacementAccessoryRepository;
import com.mitec.business.repository.SeriesRepository;
import com.mitec.business.repository.StatisticalRepository;
import com.mitec.business.service.categories.PeriodService;
import com.mitec.business.utils.AtmStatusEnum;
import com.mitec.business.utils.JobStatusEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImportDataService {
	
	@Autowired
	private ATMRepository atmRepository;
	@Autowired
	private ContractRepository contractRepository;
	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private BankRepository bankRepository;
	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
	private SeriesRepository seriesRepository;
	@Autowired
	private AccessoryRepository accessoryRepository;
	@Autowired
	private ErrorRepository errorRepository;
	@Autowired
	private JobPerformRepository jobPerformRepository;
	@Autowired
	private ErrorDeviceRepository errorDeviceRepository;
	@Autowired
	private PeriodService periodService;
	
	// fix statistical
	@Autowired
	private JobRepository jobRepository;
	@Autowired
	private StatisticalRepository statisticalRepository;
	@Autowired
	private ReplacementAccessoryRepository replacementAccessoryRepository;
	
	public void importData(String body) throws IOException, JSONException {
		JSONObject ob = new JSONObject(body);
		XSSFWorkbook workbook = new XSSFWorkbook(ob.getString("filePath"));
		
		//Sheet 0
		importBank(workbook.getSheetAt(0));
		importRegion(workbook.getSheetAt(0));
		importDepartment(workbook.getSheetAt(0));
		importContract(workbook.getSheetAt(0));
		importAtm(workbook.getSheetAt(0));

		//Sheet 1
		importErrorDeivce(workbook.getSheetAt(1));
		importAccessory(workbook.getSheetAt(1));
		
		//Sheet 2
		importDevice(workbook.getSheetAt(2));
		importError(workbook.getSheetAt(2));
		importJobPerform(workbook.getSheetAt(2));
		
		workbook.close();
	}
	
	private void importAtm(XSSFSheet worksheet) {
		int i = 1;
		Set<ATM> atms = new HashSet<>();
		log.debug("last row num =================>" + worksheet.getLastRowNum());
		List<AtmStatusEnum> statusEnums = Arrays.asList(AtmStatusEnum.values());
		while (i <= worksheet.getLastRowNum()) {
			XSSFRow dataRow = worksheet.getRow(i++);
			String serialNumber = "";
			if(dataRow.getCell(0) != null) {
				try {
					if (StringUtils.isNotBlank(dataRow.getCell(0).getStringCellValue())) {
						serialNumber = dataRow.getCell(0).getStringCellValue();
					}
				}catch (Exception e) {
					double data = dataRow.getCell(0).getNumericCellValue();
					serialNumber = String.format("%.0f", data);
				}
			}
			if (StringUtils.isNotBlank(serialNumber)) {
				ATM atm = new ATM();
				atm.setSerialNumber(serialNumber);
				atm.setAddress(dataRow.getCell(1).getStringCellValue());
				
				// series
				String seriesName = dataRow.getCell(2).getStringCellValue();
				Series series = seriesRepository.getByNameLimit1(seriesName);
				atm.setSeries(series);
				
				// Region
				String regionName = dataRow.getCell(3).getStringCellValue();
				Region region = regionRepository.getByNameLimit1(regionName);
				atm.setRegion(region);

				// department
				String departmentName = dataRow.getCell(4).getStringCellValue();
				Department department = departmentRepository.getByNameLimit1(departmentName);
				atm.setDepartment(department);
				
				//Contract 
				String contractName = dataRow.getCell(5).getStringCellValue();
				Contract contract = contractRepository.getContractByNameLimit1(contractName);
				if (contract != null) {
					List<Contract> contracts = atm.getContracts() == null ? new ArrayList<>() : atm.getContracts();
					contracts.add(contract);
					atm.setContracts(contracts);
				}
				
				// StatusStr
				String statusStr = dataRow.getCell(10).getStringCellValue();
				for (AtmStatusEnum item : statusEnums) {
					if (statusStr.equals(item.getValue())) {
						atm.setStatus(item.getKey());
						break;
					}
				}
				log.debug("=================>" + atm.toString());
				atms.add(atm);
			}
		}
		atmRepository.saveAll(atms);
	}
		
	private void importBank(XSSFSheet worksheet) {
		int i = 1;
		Set<String> bankNames = new HashSet<>();
		while (i <= worksheet.getLastRowNum()) {
			XSSFRow dataRow = worksheet.getRow(i++);
			if(dataRow.getCell(6) != null && StringUtils.isNotBlank(dataRow.getCell(6).getStringCellValue())) {
				bankNames.add(dataRow.getCell(6).getStringCellValue());
			}
		}
		
		bankNames.stream().forEach(item -> {
			Bank bank = new Bank();
			bank.setName(item);
			bank = bankRepository.save(bank);
			log.debug("=============>" + bank.toString());
		});
		
	}
	
	private void importRegion(XSSFSheet worksheet) {
		int i = 1;
		Set<String> regionNames = new HashSet<>();
		while (i <= worksheet.getLastRowNum()) {
			XSSFRow dataRow = worksheet.getRow(i++);
			if(dataRow.getCell(3) != null && StringUtils.isNotBlank(dataRow.getCell(3).getStringCellValue())) {
				regionNames.add(dataRow.getCell(3).getStringCellValue());
			}
		}
		
		regionNames.stream().forEach(item -> {
			Region region = new Region();
			region.setName(item);
			region = regionRepository.save(region);
			log.debug("=============>" + region.toString());
		});
	}
	
	private void importDepartment(XSSFSheet worksheet) {
		int i = 1;
		Set<String> departmentNames = new HashSet<>();
		while (i <= worksheet.getLastRowNum()) {
			XSSFRow dataRow = worksheet.getRow(i++);
			if(dataRow.getCell(4) != null && StringUtils.isNotBlank(dataRow.getCell(4).getStringCellValue())) {
				departmentNames.add(dataRow.getCell(4).getStringCellValue());
			}
		}
		
		departmentNames.stream().forEach(item -> {
			Department department = new Department();
			department.setName(item);
			department = departmentRepository.save(department);
			log.debug("=============>" + department.toString());
		});
	}
	
	private void importContract(XSSFSheet worksheet) {
		int i = 1;
		Set<Contract> contracts = new HashSet<>();
		while (i <= worksheet.getLastRowNum()) {
			XSSFRow dataRow = worksheet.getRow(i++);
			if (dataRow.getCell(5) != null && StringUtils.isNotBlank(dataRow.getCell(5).getStringCellValue())) {
				Contract contract = new Contract();
				
				contract.setName(dataRow.getCell(5).getStringCellValue());
				if (dataRow.getCell(7) != null && dataRow.getCell(7).getDateCellValue() != null) {
					contract.setStartTime(dataRow.getCell(7).getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
				}
				if (dataRow.getCell(8) != null && dataRow.getCell(8).getDateCellValue() != null) {
					contract.setEndTime(dataRow.getCell(8).getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
				}
				contract.setMaintenanceCycle((int) Math.round(dataRow.getCell(9).getNumericCellValue()));
				
				String bankName = dataRow.getCell(6).getStringCellValue();
				Bank bank = bankRepository.getByNameLimit1(bankName);
				
				contract.setBank(bank);
				
				contracts.add(contract);
			}
		}
		
		List<Contract> list = contractRepository.saveAll(contracts);
		list.stream().forEach(item -> periodService.getPeriod(item));
	}
	
	private void importErrorDeivce(XSSFSheet worksheet) {
		int i = 1;
		Set<ErrorDevice> errorDevices = new HashSet<>();
		while (i <= worksheet.getLastRowNum()) {
			XSSFRow dataRow = worksheet.getRow(i++);
			if (dataRow.getCell(0) != null && StringUtils.isNotBlank(dataRow.getCell(0).getStringCellValue())) {
				ErrorDevice errorDevice = new ErrorDevice();
				String name = dataRow.getCell(0).getStringCellValue();
				errorDevice.setName(name);
//				List<String> seriesNames = splitSeries(dataRow.getCell(2).getStringCellValue());
//				List<Series> series = seriesNames.stream().map(item -> seriesRepository.getByNameLimit1(item)).collect(Collectors.toList());
//				errorDevice.setSeries(series);
				errorDevices.add(errorDevice);
			}
		}
		
		errorDeviceRepository.saveAll(errorDevices);
		log.debug("====================>" + errorDevices.size());
	}
	
	private void importAccessory(XSSFSheet worksheet) {
		int i = 1;
		List<Accessory> accessories = new ArrayList<>();
		while (i <= worksheet.getLastRowNum()) {
			XSSFRow dataRow = worksheet.getRow(i++);
			if (dataRow.getCell(1) != null && StringUtils.isNotBlank(dataRow.getCell(1).getStringCellValue())) {
				Accessory accessory = new Accessory();
				accessory.setName(dataRow.getCell(1).getStringCellValue());
				String errorDeivceName = dataRow.getCell(0).getStringCellValue();
				ErrorDevice errorDevice = errorDeviceRepository.getByNameLimit1(errorDeivceName);
				accessory.setErrorDevice(errorDevice);
				
				List<String> seriesNames = splitSeries(dataRow.getCell(2).getStringCellValue());
				List<Series> series = seriesNames.stream().map(item -> seriesRepository.getByNameLimit1(item)).collect(Collectors.toList());
				accessory.setSeries(series);
				
				accessories.add(accessory);
			}
		}
		log.debug("===========>" + accessories.size());
		accessoryRepository.saveAll(accessories);
	}
	
	private List<String> splitSeries(String input) {
		List<String> result = Arrays.asList(input.split(","));
		return result.stream().map(item -> item.trim()).collect(Collectors.toList());
	}
	
	private void importDevice(XSSFSheet worksheet) {
		int i = 1;
		Set<Device> devices = new HashSet<>();
		log.debug("============>" + worksheet.getLastRowNum());
		while (i <= worksheet.getLastRowNum()) {
			XSSFRow dataRow = worksheet.getRow(i++);
			if (dataRow != null) {
				if (dataRow.getCell(1) != null && StringUtils.isNotBlank(dataRow.getCell(1).getStringCellValue())) {
					Device device = new Device();
					device.setName(dataRow.getCell(0).getStringCellValue());
					
					log.debug("=============>" + device);
					devices.add(device);
				}
			}
		}
		deviceRepository.saveAll(devices);
		log.debug("==============>" + devices.size());
	}
	
	private void importError(XSSFSheet worksheet) {
		int i = 1;
		List<Error> errors = new ArrayList<>();
		while (i <= worksheet.getLastRowNum()) {
			XSSFRow dataRow = worksheet.getRow(i++);
			if (dataRow != null) {
				if (dataRow.getCell(1) != null && StringUtils.isNotBlank(dataRow.getCell(1).getStringCellValue())) {
					Error error = new Error();
					error.setName(dataRow.getCell(1).getStringCellValue());
					String deviceName = dataRow.getCell(0).getStringCellValue();
					Device device = deviceRepository.getByNameLimit1(deviceName);
					error.setDevice(device);
					
					errors.add(error);
				}
			}
		}
		errorRepository.saveAll(errors);
		log.debug("=================> " + errors.size());
	}
	
	private void importJobPerform(XSSFSheet worksheet) {
		int i = 0;
		List<JobPerform> jobPerforms = new ArrayList<>();
		while (i <= worksheet.getLastRowNum()) {
			XSSFRow dataRow = worksheet.getRow(i++);
			if (dataRow != null) {
				if (dataRow.getCell(4) != null && StringUtils.isNotBlank(dataRow.getCell(4).getStringCellValue())) {
					JobPerform jobPerform = new JobPerform();
					jobPerform.setName(dataRow.getCell(4).getStringCellValue());
					
					log.debug("====>" + jobPerform);
					jobPerforms.add(jobPerform);
				}
			}
		}
		
		jobPerformRepository.saveAll(jobPerforms);
	}
	
	public void cloneMissedStatistical() {
		log.debug("cloneMissedStatistical()...");
		List<Job> jobs = jobRepository.findAll();
		
		jobs.stream().filter(item -> !item.getStatus().equals(JobStatusEnum.CANCEL.getKey())).forEach(item ->{
			cloneToStatistical(item);
		});
	}
	
	private void cloneToStatistical(Job job) {
		log.debug("Processing cloneToStatistical service()....");
		Statistical statistical = statisticalRepository.findByJobId(job.getId()).orElse(new Statistical());
		statistical.setJobId(job.getId());
		statistical.setMaintenance(job.isMaintenance());
		statistical.setJobCompleteTime(job.getCompleteTime());
		statistical.setUsername(job.getUser().getUsername());
		statistical.setStatus(job.getStatus());
		
		ATM atm = job.getAtm();
		if (atm.getDepartment() != null) {
			statistical.setDepartmentId(atm.getDepartment().getId());
		}
		if (atm.getRegion() != null) {
			statistical.setRegionId(atm.getRegion().getId());
		}
		statistical.setSerialNumber(atm.getSerialNumber());
		
		Contract contract = contractRepository.findCurrentContract(atm.getSerialNumber()).orElse(null);
		if(contract != null) {
			statistical.setContractId(contract.getId());
		}
		
		statistical.setQuantity(replacementAccessoryRepository.getSumQuantityByJobId(job.getId()));
	
		statisticalRepository.save(statistical);
	}
}
