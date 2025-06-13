package com.mitec.business.service.categories;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import com.mitec.business.model.*;
import com.mitec.business.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mitec.business.dto.ATMDto;
import com.mitec.business.dto.ATMScheduleDto;
import com.mitec.business.event.CloneHistoryEvent;
import com.mitec.business.specification.ObjectSpecification;
import com.mitec.business.utils.AtmStatusEnum;
import com.mitec.business.utils.ClassMapper;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Slf4j
@Service
public class AtmService {

	@Autowired
	private ATMRepository atmRepository;
	@Autowired
	private SeriesRepository seriesRepository;
	@Autowired
	private RegionRepository regionRepository;
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	@Autowired
	private ObjectSpecification objectSpecification;
	@Autowired
	private ClassMapper classMapper;

	@Autowired
	JobRepository jobRepository;
	@Autowired
	UserRepository userRepository;

	private static final String SERIAL_NUMBER = "serial";
	private static final String REGION_ID = "region";
	private static final String DEPARTMENT_ID = "department";
	private static final String SERIES = "series";
	private static final String ADDRESS = "address";
	
	public Page<ATM> listAllATM(int pageNumber, int size, String serialNumber, String addressSearch, Long series, Long region, Long department) {
		Specification<ATM> spec = objectSpecification.searchATM(serialNumber, addressSearch, series, region, department);
		return atmRepository.findAll(spec, PageRequest.of(pageNumber, size));
	}
	
	public List<AtmStatusEnum> getAtmStatusEnum() {
		return Arrays.asList(AtmStatusEnum.values());
	}
	
	public boolean checkSerialNumberATM(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		String serialNumber = ob.getString("serial_number");
		
		ATM atm = atmRepository.getBySerialNumber(serialNumber);
		
		return atm == null;
	}
	
	public ATM saveATM(String body) throws JSONException {
		ATM atm = new ATM();
		JSONObject ob = new JSONObject(body);
		
		atm.setSerialNumber(ob.getString("serial_number"));
		atm.setAddress(ob.getString("address"));
		
		Long seriesId = Long.parseLong(ob.getString("series_id"));
		Series series = seriesRepository.getById(seriesId);
		atm.setSeries(series);
		
		if (!ob.isNull("region_id")) {
			Long regionId = Long.parseLong(ob.getString("region_id"));
			Region region = regionRepository.getById(regionId);
			atm.setRegion(region);
		}
		
		if (!ob.isNull("department_id")) {
			Long departmentId = Long.parseLong(ob.getString("department_id"));
			Department department = departmentRepository.getById(departmentId);
			atm.setDepartment(department);
		}
		
//		atm.setStatus(ob.getInt("status"));
		atm.setStatus(AtmStatusEnum.NOT_PROVIDED_YET.getKey());
		
		return atmRepository.save(atm);
	}
	
	public void deleteATM(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		String serialNumber = ob.getString("id");
		atmRepository.deleteById(serialNumber);
	}
	
	public ATMDto getATM(String body) throws JSONException{
		JSONObject ob = new JSONObject(body);
		String serialNumber = ob.getString("id");
		ATM atm = atmRepository.getById(serialNumber);
		return classMapper.convertToATMDto(atm);
	}
	
	public ATM updateATM(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		String serialNumber = ob.getString("id");
		
		ATM atm = atmRepository.getById(serialNumber);
		atm.setSerialNumber(ob.getString("serial_number"));
		atm.setAddress(ob.getString("address"));
		
		Long seriesId = Long.parseLong(ob.getString("series_id"));
		Series series = seriesRepository.getById(seriesId);
		atm.setSeries(series);
		
		if (!ob.isNull("region_id")) {
			Long regionId = Long.parseLong(ob.getString("region_id"));
			Region region = regionRepository.getById(regionId);
			atm.setRegion(region);
		}
		
		if (!ob.isNull("department_id")) {
			Long departmentId = Long.parseLong(ob.getString("department_id"));
			Department department = departmentRepository.getById(departmentId);
			atm.setDepartment(department);
		}
		
//		atm.setStatus(ob.getInt("status"));
		
		return atmRepository.save(atm);
	}

	public static CellStyle setBorder(CellStyle cellStyle) {
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.index);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.index);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setRightBorderColor(IndexedColors.GREY_50_PERCENT.index);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.index);
		return cellStyle;
	}

	public byte[] exportTemplateAtm(String body) throws IOException, JSONException {

		JSONObject ob = new JSONObject(body);
		
		String serial = null;
		String address = null;
		Long series = null;
		Long region = null;
		Long department = null;
		if (!ob.isNull(SERIAL_NUMBER)) {
			serial = ob.getString(SERIAL_NUMBER);
		}
		if (!ob.isNull(ADDRESS)) {
			address = ob.getString(ADDRESS);
		}
		if (!ob.isNull(SERIES) && StringUtils.isNotBlank(ob.getString(SERIES))) {
			series = ob.getLong(SERIES);
		}
		if (!ob.isNull(REGION_ID) && StringUtils.isNotBlank(ob.getString(REGION_ID))) {
			region = ob.getLong(REGION_ID);
		}
		if (!ob.isNull(DEPARTMENT_ID) && StringUtils.isNotBlank(ob.getString(DEPARTMENT_ID))) {
			department = ob.getLong(DEPARTMENT_ID);
		}
		Specification<ATM> spec = objectSpecification.searchATM(serial, address, series, region, department);
		
		
		List<ATM> list = atmRepository.findAll(spec);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet("Thống kê số lượng máy");
		sheet.setColumnWidth(0, 20 * 256);
		sheet.setColumnWidth(1, 55 * 256);
		sheet.setColumnWidth(2, 10 * 256);
		sheet.setColumnWidth(3, 15 * 256);
		sheet.setColumnWidth(4, 35 * 256);
		sheet.setColumnWidth(5, 22 * 256);


		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		setBorder(headerStyle);

		CellStyle headerStyle1 = workbook.createCellStyle();
		Font headerFont1 = workbook.createFont();
		headerFont1.setBold(true);
		headerStyle1.setFont(headerFont1);
		headerStyle1.setAlignment(HorizontalAlignment.CENTER);
		setBorder(headerStyle1);

		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 20);
		font.setFontName("Arial");
		font.setColor(IndexedColors.RED.getIndex());

		headerStyle1.setFont(font);

		CellStyle dataStyle = workbook.createCellStyle();
		dataStyle.setAlignment(HorizontalAlignment.LEFT);
		dataStyle.setWrapText(true);
		setBorder(dataStyle);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
		Row row = sheet.createRow(0); 
		Cell cell = row.createCell(0); 
		cell.setCellValue("Danh sách ATM");
		cell.setCellStyle(headerStyle);

		for (int i = 0; i < 6; i++) {	
			Cell cellitem = row.createCell(i);
			cellitem.setCellValue("Danh sách ATM");
			cellitem.setCellStyle(headerStyle1);
		}

		Row title = sheet.createRow(1);
		Cell titleCell = title.createCell(0);
		titleCell = title.createCell(0);
		titleCell.setCellValue("SerialNumber");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(1);
		titleCell.setCellValue("Địa chỉ");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(2);
		titleCell.setCellValue("Dòng máy");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(3);
		titleCell.setCellValue("Vùng");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(4);
		titleCell.setCellValue("Đơn vị");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(5);
		titleCell.setCellValue("Trạng thái");
		titleCell.setCellStyle(headerStyle);

		CellStyle cs = workbook.createCellStyle();
		cs.setWrapText(true);
		setBorder(dataStyle);

			for (int j = 0; j < list.size(); j++) {
				Row rows = sheet.createRow(j+2);
				Cell dataCell = rows.createCell(1);
				dataCell.setCellStyle(dataStyle);
				dataCell = rows.createCell(0);
				dataCell.setCellValue(list.get(j).getSerialNumber());
				dataCell.setCellStyle(headerStyle);
				
				dataCell = rows.createCell(1);
				dataCell.setCellValue(list.get(j).getAddress());
				dataCell.setCellStyle(dataStyle);

				dataCell = rows.createCell(2);
				dataCell.setCellValue(list.get(j).getSeries()!=null?list.get(j).getSeries().getName():"");
				dataCell.setCellStyle(dataStyle);

				dataCell = rows.createCell(3);
				dataCell.setCellValue(list.get(j).getRegion()!=null?list.get(j).getRegion().getName():"");
				dataCell.setCellStyle(dataStyle);

				dataCell = rows.createCell(4);
				dataCell.setCellValue(list.get(j).getDepartment()!=null?list.get(j).getDepartment().getName():"");
				dataCell.setCellStyle(dataStyle);

				dataCell = rows.createCell(5);
				dataCell.setCellValue(list.get(j).getStatusDesc());
				dataCell.setCellStyle(dataStyle);
			}

		

		workbook.write(bos);
		workbook.close();

		return bos.toByteArray();
	}

	public Boolean importAtmData(MultipartFile excelFile) {
		log.debug("Processing import atm data service()....");
		try {
			Workbook workbook = new XSSFWorkbook(excelFile.getInputStream());
			XSSFSheet worksheet = (XSSFSheet) workbook.getSheetAt(0);
			XSSFRow dataRows = worksheet.getRow(0);
			if(!dataRows.getCell(0).toString().equals("SerialNumber")) {
				return false;
			}
			
			int i = 1;

			Set<ATM> atms = new HashSet<>();
			List<AtmStatusEnum> statusEnums = Arrays.asList(AtmStatusEnum.values());
			while (i <= worksheet.getLastRowNum()) {
				XSSFRow dataRow = worksheet.getRow(i++);
				if (dataRow != null) {
					String serialNumber = "";
					
					if (dataRow.getCell(0) != null) {
						try {
							if (StringUtils.isNotBlank(dataRow.getCell(0).getStringCellValue())) {
								serialNumber = dataRow.getCell(0).getStringCellValue();
							}
						} catch (Exception e) {
							double data = dataRow.getCell(0).getNumericCellValue();
							serialNumber = String.format("%.0f", data);
						}
					}
					if (StringUtils.isNotBlank(serialNumber)) {
						ATM atm = atmRepository.findById(serialNumber).orElse(null);
						if (atm == null) {
							atm = new ATM();
							atm.setSerialNumber(serialNumber);
						}
						atm.setAddress(dataRow.getCell(1).getStringCellValue());

						// series
						if (dataRow.getCell(2) != null) {
							String seriesName = dataRow.getCell(2).getStringCellValue();
							Series series = seriesRepository.getByNameLimit1(seriesName);
							atm.setSeries(series);
						}

						// Region
						if (dataRow.getCell(3) != null) {
							String regionName = dataRow.getCell(3).getStringCellValue();
							Region region = regionRepository.getByNameLimit1(regionName);
							atm.setRegion(region);
						}

						// department
						if (dataRow.getCell(4) != null) {
							String departmentName = dataRow.getCell(4).getStringCellValue();
							Department department = departmentRepository.getByNameLimit1(departmentName);
							atm.setDepartment(department);
						}

						// StatusStr
						if (dataRow.getCell(5) != null) {
							String statusStr = dataRow.getCell(5).getStringCellValue();
							for (AtmStatusEnum item : statusEnums) {
								if (statusStr.equals(item.getValue())) {
									atm.setStatus(item.getKey());
									break;
								}
							}
						}

						log.debug("=================>" + atm.toString());
						atms.add(atm);
					}
				}
			}
			workbook.close();
			List<ATM> list = atmRepository.saveAll(atms);

			// publish event clone to history
			applicationEventPublisher.publishEvent(new CloneHistoryEvent(this, list));

			return true;
		} catch (Exception e) {
			log.debug("Import ATM err: " + e.getMessage());
			return false;
		}
	}
	
	public void atmSchedule() {
		List<ATMScheduleDto> atms = atmRepository.getAtmForScheduler();
		if (atms != null && !atms.isEmpty()) {
			atms.stream().forEach(item -> {
				if (item.getEndTime().isBefore(LocalDateTime.now()) && item.getStatus() > AtmStatusEnum.PAUSE.getKey()) {
					ATM a = atmRepository.getBySerialNumber(item.getSerialNumber());
					a.setStatus(AtmStatusEnum.PAUSE.getKey());
					atmRepository.save(a);
				}
			});
		}
	}
	
	public void changeStatus(String serialNumber) {
		ATM atm = atmRepository.getBySerialNumber(serialNumber);
		if (atm.getStatus().equals(AtmStatusEnum.PROVIDING.getKey())) {
			atm.setStatus(AtmStatusEnum.NOT_PROVIDED_YET.getKey());
		}else {
			atm.setStatus(AtmStatusEnum.PROVIDING.getKey());
		}
		
		atmRepository.save(atm);
	}

	public ATM getATMBySerialNumber(String serialNumber){
		return atmRepository.findById(serialNumber).orElse(null);
	}


	@Transactional
	public ATM updateAddress(AddressUpdateRequest request,String username) {
		Optional<ATM> optionalAtm = atmRepository.findById(request.getSerialNumber());
		if (optionalAtm.isPresent()) {
			ATM atm = optionalAtm.get();
			atm.setAddress(request.getNewAddress());
			atmRepository.save(atm);

			// Log the action by creating a new Job
			Job job = new Job();
			job.setAtm(atm);
			job.setJobReason(request.getNewAddress());
			job.setStatus(2);
			job.setCheckInTime(LocalDateTime.now());
			job.setNote("Thay đổi địa chỉ ");

			User optionalUser = userRepository.findByUsername(username);

			job.setUser(optionalUser);

			jobRepository.save(job);

			return atm;
		} else {
			throw new EntityNotFoundException("ATM not found with serial number: " + request.getSerialNumber());
		}
	}


}
