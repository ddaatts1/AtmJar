package com.mitec.business.service.categories;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mitec.business.dto.ATMDto;
import com.mitec.business.dto.ContractAtmDto;
import com.mitec.business.dto.ContractDto;
import com.mitec.business.event.ChangeStatusAtmEvent;
import com.mitec.business.event.CreatePeriodEvent;
import com.mitec.business.model.ATM;
import com.mitec.business.model.Bank;
import com.mitec.business.model.Contract;
import com.mitec.business.model.ContractAtm;
import com.mitec.business.model.CustomerEmail;
import com.mitec.business.repository.ATMRepository;
import com.mitec.business.repository.BankRepository;
import com.mitec.business.repository.ContractAtmRepository;
import com.mitec.business.repository.ContractRepository;
import com.mitec.business.repository.CustomerEmailRepository;
import com.mitec.business.specification.ObjectSpecification;
import com.mitec.business.utils.ClassMapper;
import com.mitec.business.utils.CustomFormatDate;
import com.mitec.business.utils.EmailTypeEnum;

@Service
public class ContractService {

	@Autowired
	private ContractRepository contractRepository;
	@Autowired
	private ATMRepository atmRepository;
	@Autowired
	private BankRepository bankRepository;
	@Autowired
	private ObjectSpecification objectSpecification;
	@Autowired
	private ClassMapper classMapper;
	@Autowired
	private CustomerEmailRepository customerEmailRepository;
	@Autowired
	private ContractAtmRepository contractAtmRepository;
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	public List<ATM> getAtms() {
		return atmRepository.findAll();
	}

	public Page<Contract> getContracts(Integer type, int pageNumber, int pageSize) {
		Specification<Contract> spec = objectSpecification.searchContract(type);

		return contractRepository.findAll(spec, PageRequest.of(pageNumber, pageSize));
	}

	public List<Bank> listAll() {
		return bankRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	public Contract saveContract(String body) throws JSONException {
		Contract contract = new Contract();
		JSONObject ob = new JSONObject(body);
		contract.setName(ob.getString("name"));
		contract.setMaintenanceCycle(Integer.parseInt(ob.getString("maintenance_cycle")));

		if (!ob.isNull("startTime")) {
			LocalDateTime dateTimeStart = CustomFormatDate.formatStartTimeForInput(ob.getString("startTime"));
			contract.setStartTime(dateTimeStart);
		}
		if (!ob.isNull("endTime")) {
			LocalDateTime dateTimeEnd = CustomFormatDate.formatStartTimeForInput(ob.getString("endTime"));
			contract.setEndTime(dateTimeEnd);
		}

		Long bankId = Long.parseLong(ob.getString("bank"));
		Bank bank = bankRepository.getById(bankId);
		contract.setBank(bank);
		contract.setStatus(1);
		contract.setType(Integer.parseInt(ob.getString("type")));

		JSONArray jsonArray = new JSONArray(ob.getString("listItem"));
		List<String> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			list.add(jsonArray.getString(i));
		}
		List<ATM> listATM = atmRepository.getByListId(list);
		contract.setAtms(listATM);
		contract = contractRepository.save(contract);

		applicationEventPublisher.publishEvent(new CreatePeriodEvent(this, contract));
		applicationEventPublisher.publishEvent(new ChangeStatusAtmEvent(this, listATM));

		return contract;
	}
	
	public List<Contract> listContractAll() {
		return contractRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	public void deleteContract(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		contractRepository.deleteById(id);
	}

	public ContractDto getContract(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));
		Contract contract = contractRepository.getById(id);
		ContractDto contractDto = new ContractDto();
		BeanUtils.copyProperties(contract, contractDto);
		
		contractDto.setStartTimeStr(CustomFormatDate.formatLocalDate(contract.getStartTime(), "dd/MM/yyyy"));
		contractDto.setEndTimeStr(CustomFormatDate.formatLocalDate(contract.getEndTime(), "dd/MM/yyyy"));
		
		List<ATMDto> atmDtos = new ArrayList<>();
		if (contract.getAtms() != null && !contract.getAtms().isEmpty()) {
			atmDtos = contract.getAtms().stream().map(item -> {
				ATMDto atmDto = new ATMDto();
				atmDto.setSerialNumber(item.getSerialNumber());
				atmDto.setAddress(item.getAddress());
				return atmDto;
			}).collect(Collectors.toList());
		}
		contractDto.setAtms(atmDtos);
		return contractDto;
	}

	public Map<String, String> getContractTime(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Map<String, String> result = new HashMap<>();
		LocalDateTime firstDateInYear = LocalDateTime.of(LocalDate.of(LocalDate.now().getYear(), 1, 1), LocalTime.MIN);
		
		if (!ob.isNull("id")) {
			Contract contract = contractRepository.getById(ob.getLong("id"));
			if (contract.getStartTime().isAfter(firstDateInYear)) {
				firstDateInYear = contract.getStartTime();
			}
		}
		
		result.put("startTime", CustomFormatDate.formatLocalDate(firstDateInYear, "dd-MM-yyyy"));
		result.put("endTime", CustomFormatDate.formatLocalDate(LocalDateTime.now(), "dd-MM-yyyy"));
		
		return result;
	}

	public Contract updateContract(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("id"));

		Contract contract = contractRepository.getById(id);
		contract.setName(ob.getString("name"));
		contract.setMaintenanceCycle(Integer.parseInt(ob.getString("maintenance_cycle")));

		if (!ob.isNull("startTime")) {
			LocalDateTime dateTimeStart = CustomFormatDate.formatStartTimeForInput(ob.getString("startTime"));
			contract.setStartTime(dateTimeStart);
		}
		if (!ob.isNull("endTime")) {
			LocalDateTime dateTimeEnd = CustomFormatDate.formatStartTimeForInput(ob.getString("endTime"));
			contract.setEndTime(dateTimeEnd);
		}

		Long bankId = Long.parseLong(ob.getString("bank"));
		Bank bank = bankRepository.getById(bankId);
		contract.setBank(bank);
		contract.setStatus(1);
		contract.setType(Integer.parseInt(ob.getString("type")));

		JSONArray jsonArray = new JSONArray(ob.getString("listItem"));
		List<String> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			list.add(jsonArray.getString(i));
		}
		List<ATM> listATM = atmRepository.getByListId(list);
		contract.setAtms(listATM);
		contract = contractRepository.save(contract);

		// Tính lại chu kỳ
		applicationEventPublisher.publishEvent(new CreatePeriodEvent(this, contract));
		applicationEventPublisher.publishEvent(new ChangeStatusAtmEvent(this, listATM));

		return contract;
	}

	public ContractDto listContractATM(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("contract_id"));
		Contract contract = contractRepository.getById(id);
		return classMapper.convertContractDtoForEditATMs(contract);
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

	public byte[] exportContractAtm(String body) throws IOException, JSONException {

		JSONObject ob = new JSONObject(body);
		Long id = Long.parseLong(ob.getString("contract_id"));
		Contract contract = contractRepository.getById(id);
		ContractDto lists = classMapper.convertContractDtoForEditATMs(contract);

		List<ContractAtmDto> list = lists.getContractAtmDtos();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
		System.out.println(contract.getStartTime().toString());

		LocalDate startTimes = LocalDate.parse(contract.getStartTime().toString(), formatters);
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String startTime = startTimes.format(format);
		LocalDate endTimes = LocalDate.parse(contract.getEndTime().toString(), formatters);
		DateTimeFormatter formats = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String endTime = endTimes.format(formats);

		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet("Danh sách ATM của hợp đồng");
		sheet.setColumnWidth(0, 10 * 256);
		sheet.setColumnWidth(1, 25 * 256);
		sheet.setColumnWidth(2, 90 * 256);

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

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));
		Row row = sheet.createRow(0); // row 1
//		Cell cell = row.createCell(0);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
		Row row1 = sheet.createRow(1); // row 1
		Cell cell1 = row1.createCell(0);
		cell1.setCellStyle(dataStyle);
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 2));
		Row row2 = sheet.createRow(2); // row 1
		Cell cell2 = row2.createCell(0);
		cell2.setCellStyle(dataStyle);
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 2));
		Row row3 = sheet.createRow(3); // row 1
		Cell cell3 = row3.createCell(0);
		cell3.setCellStyle(dataStyle);
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 2));
		Row row4 = sheet.createRow(4); // row 1
		Cell cell4 = row4.createCell(0);
		cell4.setCellStyle(dataStyle);

		for (int i = 0; i < 3; i++) {
			Cell cellitem = row.createCell(i);
			cellitem.setCellValue("Danh sách ATM của " + contract.getName());
			cellitem.setCellStyle(headerStyle);
			Cell cellitem1 = row1.createCell(i);
			cellitem1.setCellValue(contract.getBank().getName());
			cellitem1.setCellStyle(dataStyle);
			Cell cellitem2 = row2.createCell(i);
			cellitem2.setCellValue("Thời gian bắt đầu: " + startTime);
			cellitem2.setCellStyle(dataStyle);
			Cell cellitem3 = row3.createCell(i);
			cellitem3.setCellValue("Thời gian kết thúc: " + endTime);
			cellitem3.setCellStyle(dataStyle);
			Cell cellitem4 = row4.createCell(i);
			cellitem4.setCellValue("Chu kỳ bảo trì (tháng): " + contract.getMaintenanceCycle());
			cellitem4.setCellStyle(dataStyle);
		}

		Row title = sheet.createRow(5);
		Cell titleCell = title.createCell(0);
		titleCell = title.createCell(0);
		titleCell.setCellValue("STT");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(1);
		titleCell.setCellValue("SerialNumber");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(2);
		titleCell.setCellValue("Địa chỉ");
		titleCell.setCellStyle(headerStyle);

		CellStyle cs = workbook.createCellStyle();
		cs.setWrapText(true);
		setBorder(dataStyle);

		for (int j = 0; j < list.size(); j++) {
			Row rows = sheet.createRow(j + 6);
			Cell dataCell = rows.createCell(1);
			dataCell.setCellStyle(dataStyle);
			dataCell = rows.createCell(0);
			dataCell.setCellValue(
					(list.get(j).getOrderNumber() == null) ? "" : list.get(j).getOrderNumber().toString());
			dataCell.setCellStyle(headerStyle);

			dataCell = rows.createCell(1);
			dataCell.setCellValue(list.get(j).getAtmDto().getSerialNumber());
			dataCell.setCellStyle(headerStyle);

			dataCell = rows.createCell(2);
			dataCell.setCellValue(list.get(j).getAtmDto().getAddress());
			dataCell.setCellStyle(dataStyle);
		}

		workbook.write(bos);
		workbook.close();

		return bos.toByteArray();
	}

	public List<Map<String, Object>> listContractEmail(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		List<Map<String, Object>> result = new ArrayList<>();
		if (!ob.isNull("contract_id")) {
			Long id = Long.parseLong(ob.getString("contract_id"));
			List<CustomerEmail> customerEmails = customerEmailRepository.getByContractId(id);
			if (customerEmails != null && !customerEmails.isEmpty()) {
				result = customerEmails.stream().map(item -> {
					Map<String, Object> email = new HashMap<>();
					email.put("id", item.getId());
					email.put("email", item.getEmail());
					email.put("type", EmailTypeEnum.fromKey(item.getType()).getValue());
					return email;
				}).collect(Collectors.toList());
			}
		}
		return result;
	}

	public void deleteContractEmail(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		if (!ob.isNull("email_id")) {
			customerEmailRepository.deleteById(ob.getLong("email_id"));
		}
	}

	public void changeStatusContractEmail(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		if (!ob.isNull("email_id")) {
			CustomerEmail customerEmail = customerEmailRepository.getById(ob.getLong("email_id"));
			customerEmail.setType(
					customerEmail.getType().equals(EmailTypeEnum.INTERNAL.getKey()) ? EmailTypeEnum.CUSTOMER.getKey()
							: EmailTypeEnum.INTERNAL.getKey());
			customerEmailRepository.save(customerEmail);
		}
	}

	public void addContractEmail(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		if (!ob.isNull("contract_id")) {
			CustomerEmail customerEmail = new CustomerEmail();
			customerEmail.setEmail(ob.getString("email") != null ? ob.getString("email") : null);
			Contract contract = contractRepository.getById(ob.getLong("contract_id"));
			customerEmail.setContract(contract);
			customerEmail.setType(EmailTypeEnum.CUSTOMER.getKey());
			customerEmailRepository.save(customerEmail);
		}
	}

	public ContractAtmDto getContractAtm(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		ContractAtm contractAtm = new ContractAtm();
		if (!ob.isNull("id")) {
			contractAtm = contractAtmRepository.getById(ob.getLong("id"));
		}
		return classMapper.convertContractAtmDto(contractAtm);
	}

	public void saveContractAtm(String body) throws JSONException {
		JSONObject ob = new JSONObject(body);
		if (!ob.isNull("id")) {
			ContractAtm contractAtm = contractAtmRepository.getById(Long.parseLong(ob.getString("id")));
			String orderNumberStr = ob.isNull("orderNumber") ? null : ob.getString("orderNumber");
			Long orderNumber = StringUtils.isNotBlank(orderNumberStr) ? Long.valueOf(orderNumberStr) : 1L;
			contractAtm.setOrderNumber(orderNumber);

			contractAtm = contractAtmRepository.save(contractAtm);
		}
	}

	public String getNameATM(String serialNumber) {
		ATM acc = atmRepository.getById(serialNumber);
		return acc.getAddress();
	}

	public CompletableFuture<Boolean> importAtmContract(MultipartFile excelFile, Long contract_id) {
		try {
			Workbook workbook = new XSSFWorkbook(excelFile.getInputStream());
			XSSFSheet worksheet = (XSSFSheet) workbook.getSheetAt(0);
			XSSFRow dataRows = worksheet.getRow(5);
			if (!dataRows.getCell(0).toString().equals("STT")
					&& !dataRows.getCell(1).toString().equals("SerialNumber")) {
				return CompletableFuture.completedFuture(false);
			}
			int i = 6;
			Contract contract = contractRepository.getById(contract_id);

			while (i <= worksheet.getLastRowNum()) {

				XSSFRow dataRow = worksheet.getRow(i++);
				if (dataRow != null) {
					String orderNumber = "";

					if (dataRow.getCell(0) != null) {
						try {
							if (StringUtils.isNotBlank(dataRow.getCell(0).getStringCellValue())) {
								orderNumber = dataRow.getCell(0).getStringCellValue();
							}
						} catch (Exception e) {
							double data = dataRow.getCell(0).getNumericCellValue();
							orderNumber = String.format("%.0f", data);
						}
					}
					if (dataRow.getCell(1) != null) {
						for (ContractAtm item : contract.getContractAtms()) {

							if (dataRow.getCell(1).getStringCellValue().equals(item.getAtm().getSerialNumber())) {
								ContractAtm contractAtm = contractAtmRepository.getById(item.getId());
								contractAtm.setOrderNumber(Long.valueOf(orderNumber));
								contractAtmRepository.save(contractAtm);
							}

						}
					}
				}
			}

			workbook.close();
			return CompletableFuture.completedFuture(true);
		} catch (Exception e) {
			return CompletableFuture.completedFuture(false);
		}
	}

}
