package com.mitec.business.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.mitec.business.dto.ATMForStatistical;
import com.mitec.business.dto.PdfAccessoryDto;
import com.mitec.business.dto.PdfKpscDto;
import com.mitec.business.dto.PeriodDto;
import com.mitec.business.dto.ResultApi;
import com.mitec.business.dto.StatisticalAmountDto;
import com.mitec.business.dto.StatisticalContracDto;
import com.mitec.business.dto.StatisticalContract;
import com.mitec.business.dto.StatisticalRegion;
import com.mitec.business.model.ATM;
import com.mitec.business.model.Contract;
import com.mitec.business.model.Job;
import com.mitec.business.repository.ATMRepository;
import com.mitec.business.repository.ContractRepository;
import com.mitec.business.repository.JobRepository;
import com.mitec.business.utils.CustomFormatDate;
import com.mitec.business.utils.SpringUploadUtil;
import com.mitec.business.utils.UploadedFile;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@Slf4j
@Service
public class FileService {

	@Autowired
	private ATMRepository atmRepository;
	@Autowired
	private JobRepository jobRepository;
	@Autowired
	private ContractRepository contractRepository;
	@Autowired
	private StatisticalService statisticalService;

	public ResultApi genPdf(String input, String path, String contextPath)
			throws JSONException, JRException, IOException {
		log.debug("Proccessing genPdf()....");

		ResultApi resultApi = new ResultApi();
		Map<String, Object> data = new HashMap<>();

		/* Compile report template */
		InputStream inputStream = new ClassPathResource("static/reports/bao_cao_kpsc_atm.jrxml").getInputStream();
		JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
		JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

		/* Fill data from json to template */
		JSONObject ob = new JSONObject(input);
		Map<String, Object> parameters = new HashMap<>();

		String serialNumber = ob.getString("serialNumber");
		ATM atm = atmRepository.getById(serialNumber);

		Contract contract = contractRepository.getCurrentContractByAtm(serialNumber);

		List<PdfKpscDto> kpscs = new ArrayList<>();
		List<PdfAccessoryDto> accessories = new ArrayList<>();

		JSONArray jsonKpsc = ob.getJSONArray("kpscs");
		if (jsonKpsc != null && jsonKpsc.length() > 0) {
			for (int i = 0; i < jsonKpsc.length(); i++) {
				JSONObject kpscObj = jsonKpsc.getJSONObject(i);
				PdfKpscDto kpsc = new PdfKpscDto();
				kpsc.setIndex(i + 1);
				if (!kpscObj.isNull("device")) {
					kpsc.setDevice(kpscObj.getString("device"));
				}
				if (!kpscObj.isNull("error")) {
					kpsc.setError(kpscObj.getString("error"));
				}
				if (!kpscObj.isNull("jobPerform")) {
					kpsc.setJobPerform(kpscObj.getString("jobPerform"));
				}

				kpscs.add(kpsc);
			}
		} else {
			kpscs.add(new PdfKpscDto());
		}

		JSONArray jsonAccessory = ob.getJSONArray("accessories");
		if (jsonAccessory != null && jsonAccessory.length() > 0) {
			for (int i = 0; i < jsonAccessory.length(); i++) {
				JSONObject accObj = jsonAccessory.getJSONObject(i);
				PdfAccessoryDto accessory = new PdfAccessoryDto();
				accessory.setIndex(i + 1);
				if (!accObj.isNull("errorDevice")) {
					accessory.setDevice(accObj.getString("errorDevice"));
				}
				if (!accObj.isNull("desc")) {
					accessory.setDesc(accObj.getString("desc"));
				}

				accessories.add(accessory);
			}
		} else {
			accessories.add(new PdfAccessoryDto());
		}

		JRBeanCollectionDataSource kpscDataSource = new JRBeanCollectionDataSource(kpscs);
		JRBeanCollectionDataSource accessoryDataSource = new JRBeanCollectionDataSource(accessories);

		if (contract != null) {
			parameters.put("contractName", contract.getName());
		} else {
			parameters.put("contractName", "");
		}
		parameters.put("serialNumber", serialNumber);
		parameters.put("address", atm.getAddress());
		parameters.put("startTime", ob.getString("startTime"));
		parameters.put("endTime", ob.getString("endTime"));
		parameters.put("note", ob.getString("note") == null ? "" : ob.getString("note"));
		parameters.put("collectionKpsc", kpscDataSource);
		parameters.put("collectionAccessory", accessoryDataSource);

		byte[] newPdf = JasperExportManager
				.exportReportToPdf(JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource()));

		String folder = path + serialNumber;

		DateTimeFormatter fomatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

		StringBuilder fileName = new StringBuilder();
		fileName.append("report").append("-").append(fomatter.format(LocalDateTime.now())).append(".pdf");

		UploadedFile uploadFile = new UploadedFile();
		uploadFile.setUploadedPath(folder);
		uploadFile.setUploadedFileName(fileName.toString());
		uploadFile.setUploadedFileContent(newPdf);
		SpringUploadUtil.uploadFile(uploadFile);

		StringBuilder filePath = new StringBuilder();
		filePath.append(contextPath).append(serialNumber).append("/").append(fileName);

		data.put("filePath", filePath.toString());
		resultApi.setData(data);
		resultApi.setMessage("Success");
		resultApi.setSuccess(true);

		Job job = jobRepository.getById(ob.getLong("id"));
		job.setFilePath(filePath.toString());
		jobRepository.save(job);

		return resultApi;
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

	public byte[] statisticalAmount(String body) throws IOException, JSONException {

		List<StatisticalContracDto> data = statisticalService.totalAmounts(body).join();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet("Báo cáo tổng lượng services");
		sheet.setColumnWidth(0, 5 * 256);
		sheet.setColumnWidth(1, 50 * 256);
		sheet.setColumnWidth(2, 30 * 256);
		sheet.setColumnWidth(3, 20 * 256);
		sheet.setColumnWidth(4, 20 * 256);
		sheet.setColumnWidth(5, 20 * 256);
		sheet.setColumnWidth(6, 20 * 256);
		sheet.setColumnWidth(7, 30 * 256);

		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);

		setBorder(headerStyle);
		CellStyle dataStyle = workbook.createCellStyle();

		dataStyle.setAlignment(HorizontalAlignment.LEFT);
		dataStyle.setWrapText(true);
		setBorder(dataStyle);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
		Row row = sheet.createRow(0); // row 1
		Cell cell = row.createCell(0); // cell A1
		cell.setCellValue("Báo cáo tổng lượng services");
		cell.setCellStyle(headerStyle);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));
		Row row1 = sheet.createRow(1); // row 1
		Cell cell1 = row1.createCell(0); // cell A1
		cell1.setCellValue("Thời gian bắt đầu:");
		cell1.setCellStyle(dataStyle);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 7));
		Row row2 = sheet.createRow(2); // row 1
		Cell cell2 = row2.createCell(0); // cell A1
		cell2.setCellValue("Thời gian kết thúc:");
		cell2.setCellStyle(dataStyle);

		for (int i = 0; i < 8; i++) {
			Cell cellitem = row.createCell(i);
			cellitem.setCellValue("Báo cáo tổng lượng services");
			Cell cell1item = row1.createCell(i);
			cell1item.setCellValue("Thời gian bắt đầu:");
			Cell cell2item = row2.createCell(i);
			cellitem.setCellStyle(headerStyle);
			cell1item.setCellStyle(dataStyle);
			cell2item.setCellStyle(dataStyle);
			cell2item.setCellValue("Thời gian kết thúc:");
		}

		Row title = sheet.createRow(3);
		Cell titleCell = title.createCell(0);
		titleCell = title.createCell(0);
		titleCell.setCellValue("STT");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(1);
		titleCell.setCellValue("Hợp đồng");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(2);
		titleCell.setCellValue("Vùng");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(3);
		titleCell.setCellValue("Đơn vị phụ trách");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(4);
		titleCell.setCellValue("Số lượng máy");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(5);
		titleCell.setCellValue("Số lần services");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(6);
		titleCell.setCellValue("Số linh kiện thay thế");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(7);
		titleCell.setCellValue("Bình quân services / số máy");
		titleCell.setCellStyle(headerStyle);

		CellStyle cs = workbook.createCellStyle();
		cs.setWrapText(true);
		setBorder(dataStyle);

		for (int i = 0; i < data.size(); i++) {
			Row data2 = sheet.createRow(i + 4);
			Cell dataCell = data2.createCell(0);

			dataCell = data2.createCell(0);
			dataCell.setCellValue((i + 1));
			dataCell.setCellStyle(cs);

			dataCell = data2.createCell(1);
			dataCell.setCellValue((String) data.get(i).getContract());
			dataCell.setCellStyle(cs);

			dataCell = data2.createCell(2);
			dataCell.setCellValue((Long) data.get(i).getId());
			dataCell.setCellStyle(cs);
		}

		workbook.write(bos);
		workbook.close();

		return bos.toByteArray();
	}

	public byte[] statisticalAmountRegion(String body) throws IOException, JSONException {
		JSONObject ob = new JSONObject(body);
		Set<StatisticalRegion> data = statisticalService.totalAmountRegionExcel(body);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
				
		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet("Thống kê số lượng máy");
		sheet.setColumnWidth(0, 5 * 256);
		sheet.setColumnWidth(1, 20 * 256);
		sheet.setColumnWidth(2, 50 * 256);
		sheet.setColumnWidth(3, 50 * 256);
		sheet.setColumnWidth(4, 20 * 256);
		sheet.setColumnWidth(5, 20 * 256);
		sheet.setColumnWidth(6, 20 * 256);
		sheet.setColumnWidth(7, 40 * 256);

		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		setBorder(headerStyle);

		CellStyle headerStyle1 = workbook.createCellStyle();
		Font headerFont1 = workbook.createFont();
		// headerStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerFont1.setBold(true);
		headerStyle1.setFont(headerFont1);
		headerStyle1.setAlignment(HorizontalAlignment.CENTER);
		setBorder(headerStyle1);
		// headerStyle1.setFillBackgroundColor(IndexedColors.BRIGHT_GREEN.index);

		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 20);
		font.setFontName("Arial");
		font.setColor(IndexedColors.RED.getIndex());

		headerStyle1.setFont(font);

		CellStyle dataStyle = workbook.createCellStyle();
		dataStyle.setAlignment(HorizontalAlignment.LEFT);
		dataStyle.setWrapText(true);
		setBorder(dataStyle);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
		Row row = sheet.createRow(0); // row 1
		Cell cell = row.createCell(0); // cell A1
		cell.setCellValue("Thống kê số lượng máy");
		cell.setCellStyle(headerStyle);

		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));
		Row row1 = sheet.createRow(1); // row 1
		Cell cell1 = row1.createCell(0); // cell A1
		cell1.setCellValue("Thời gian bắt đầu:");
		cell1.setCellStyle(dataStyle);

		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 7));
		Row row2 = sheet.createRow(2); // row 1
		Cell cell2 = row2.createCell(0); // cell A1
		cell2.setCellValue("Thời gian kết thúc:");
		cell2.setCellStyle(dataStyle);

		for (int i = 0; i < 8; i++) {
			Cell cellitem = row.createCell(i);
			cellitem.setCellValue("Thống kê số lượng máy");
			Cell cell1item = row1.createCell(i);
			cell1item.setCellValue("Thời gian bắt đầu: " + (ob.isNull("startTime") ? "" : ob.getString("startTime")));
			Cell cell2item = row2.createCell(i);
			cellitem.setCellStyle(headerStyle1);
			cell1item.setCellStyle(dataStyle);
			cell2item.setCellStyle(dataStyle);
			// TO-DO: Thời gian truyền từ view
			cell2item.setCellValue("Thời gian kết thúc: " + (ob.isNull("endTime") ? "" : ob.getString("endTime")));
		}

		Row title = sheet.createRow(3);
		Cell titleCell = title.createCell(0);
		titleCell = title.createCell(0);
		titleCell.setCellValue("STT");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(1);
		titleCell.setCellValue("Vùng, Miền");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(2);
		titleCell.setCellValue("Đơn Vị Chuyên trách");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(3);
		titleCell.setCellValue("Hợp đồng");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(4);
		titleCell.setCellValue("Số lượng máy");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(5);
		titleCell.setCellValue("Số lần services");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(6);
		titleCell.setCellValue("Số linh kiện thay thế");
		titleCell.setCellStyle(headerStyle);

		titleCell = title.createCell(7);
		titleCell.setCellValue("Bình quân services / số lượng máy");
		titleCell.setCellStyle(headerStyle);

		CellStyle cs = workbook.createCellStyle();
		cs.setWrapText(true);
		setBorder(dataStyle);
		List<StatisticalContract> statisticalContracts = new ArrayList<>();
		int countEnd = 3;
		int countstart = 4;
		int countitem = 0;
//		for (int i = 0; i < data.size(); i++) {
		for (StatisticalRegion item : data) {
			statisticalContracts = item.getStatisticalContract();

			for (int j = 0; j < statisticalContracts.size(); j++) {
				countEnd += 1;
				countstart += 1;
				Row rows = sheet.createRow(countEnd);
				Cell dataCell = rows.createCell(1);
				dataCell.setCellStyle(dataStyle);

				if (j != 0 && statisticalContracts.get(j - 1).getDepartmentName()
						.equals(statisticalContracts.get(j).getDepartmentName())) {
					dataCell = rows.createCell(2);
					dataCell.setCellValue("");
					dataCell.setCellStyle(dataStyle);
				} else {
					dataCell = rows.createCell(2);
					dataCell.setCellValue(statisticalContracts.get(j).getDepartmentName());
					dataCell.setCellStyle(dataStyle);
				}

				dataCell = rows.createCell(0);
				dataCell.setCellStyle(headerStyle);
				if (j == 0) {
					countitem += 1;
//					if (statisticalRegion.size() > 1) {
//						CellRangeAddress mergedRegion = new CellRangeAddress(countEnd,
//								countEnd + (int) (long) data.get(i).getCountContract() - 1, 1, 1);
//						sheet.addMergedRegion(mergedRegion);
//						mergedRegion = new CellRangeAddress(countEnd,
//								countEnd + (int) (long) data.get(i).getCountContract() - 1, 0, 0);
//						sheet.addMergedRegion(mergedRegion);
//					}
					dataCell = rows.createCell(1);
					dataCell.setCellValue(item.getRegionName());
					dataCell.setCellStyle(dataStyle);
					dataCell = rows.createCell(0);
					dataCell.setCellValue(countitem);
					dataCell.setCellStyle(headerStyle);
				}

				dataCell = rows.createCell(3);

				dataCell.setCellValue(statisticalContracts.get(j).getContractName());
				dataCell.setCellStyle(dataStyle);

				dataCell = rows.createCell(4);
				dataCell.setCellValue(statisticalContracts.get(j).getCountAtm());
				dataCell.setCellStyle(headerStyle);

				dataCell = rows.createCell(5);
				dataCell.setCellValue(statisticalContracts.get(j).getCountService());
				dataCell.setCellStyle(headerStyle);

				dataCell = rows.createCell(6);
				dataCell.setCellValue(statisticalContracts.get(j).getSumQuantity());
				dataCell.setCellStyle(headerStyle);

				dataCell = rows.createCell(7);
				dataCell.setCellValue(statisticalContracts.get(j).getAverage());
				dataCell.setCellStyle(headerStyle);

			}

		}

		workbook.write(bos);
		workbook.close();

		return bos.toByteArray();
	}

	public List<Integer> getMonthListNumber(String startDate, String endDate) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		Date dateFrom = dateFormat.parse(startDate);
		Date dateTo = dateFormat.parse(endDate);

		Calendar calendar = Calendar.getInstance(Locale.US);
		calendar.setTime(dateFrom);
		List<Integer> months = new ArrayList<>();

		while (calendar.getTime().getTime() <= dateTo.getTime()) {
			String[] date = dateFormat.format(calendar.getTime()).toString().split("-");
			months.add(Integer.parseInt(date[1]));
			calendar.add(Calendar.MONTH, 1);
		}

		String numberMonth = endDate.toString().split("-")[1];
		if (!months.contains(Integer.parseInt(numberMonth))) {
			months.add(Integer.parseInt(numberMonth));
		}

		return months;
	}

	// Thành thêm hàm lấy list tháng từ khoảng thời gian
	public List<Pair<String, String>> getMonthListString(String startDate, String endDate) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		Date dateFrom = dateFormat.parse(startDate);
		Date dateTo = dateFormat.parse(endDate);

		Calendar calendar = Calendar.getInstance(Locale.US);
		calendar.setTime(dateFrom);
		List<Pair<String, String>> months = new ArrayList<>();

		Pair<String, String> coupleDate;
		while (calendar.getTime().getTime() <= dateTo.getTime()) {
			String date = dateFormat.format(calendar.getTime());

			if (calendar.getTime().getTime() == dateFrom.getTime()) {
				coupleDate = Pair.of(startDate, getLastDayOfMonth(date));
			} else if (calendar.getTime().getTime() < dateTo.getTime()
					&& calendar.getTime().getMonth() == dateTo.getMonth()) {
				coupleDate = Pair.of(getFirstDayOfMonth(date), endDate);
			} else {
				coupleDate = Pair.of(getFirstDayOfMonth(date), getLastDayOfMonth(date));
			}

			months.add(coupleDate);
			calendar.add(Calendar.MONTH, 1);
		}

		return months;
	}

	public String getLastDayOfMonth(String date) {
		LocalDate convertedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		convertedDate = convertedDate.withDayOfMonth(convertedDate.getMonth().length(convertedDate.isLeapYear()));
		return convertedDate.toString();
	}

	public String getFirstDayOfMonth(String date) {
		LocalDate convertedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		convertedDate = convertedDate.withDayOfMonth(1);
		return convertedDate.toString();
	}

	public byte[] personStatisticalAmount(String body) throws JSONException, IOException, ParseException {
		JSONObject ob = new JSONObject(body);
		Workbook workbook = new XSSFWorkbook();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		Sheet sheet = workbook.createSheet("Báo cáo");
		sheet.setColumnWidth(0, 7 * 256);
		sheet.setColumnWidth(1, 17 * 256);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setAlignment(HorizontalAlignment.LEFT);
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);

		Row header = sheet.createRow(0);
		Cell headerCell = header.createCell(0);
		headerCell.setCellStyle(headerStyle);
		headerCell.setCellValue("Thống kê theo cán bộ services ");

		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setWrapText(true);
		
		CellStyle dateCell = workbook.createCellStyle();
		dateCell.setVerticalAlignment(VerticalAlignment.CENTER);
		dateCell.setAlignment(HorizontalAlignment.LEFT);

		Row filterStartTime = sheet.createRow(1);

		Cell filterCell = filterStartTime.createCell(0);
		filterCell.setCellStyle(dateCell);
		filterCell.setCellValue("Thời gian bắt đầu: " + ob.getString("startTime"));

		Row filterEndTime = sheet.createRow(2);
		filterCell = filterEndTime.createCell(0);
		filterCell.setCellStyle(dateCell);
		filterCell.setCellValue("Thời gian kết thúc: " + ob.getString("endTime"));

		List<Integer> monthList = getMonthListNumber(ob.getString("startTime"), ob.getString("endTime"));

		CellStyle cellStyleCenter = workbook.createCellStyle();
		cellStyleCenter.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyleCenter.setAlignment(HorizontalAlignment.CENTER);
		
		Row title = sheet.createRow(3);
		Cell titleCell = title.createCell(0);
		titleCell.setCellStyle(cellStyleCenter);
		sheet.addMergedRegion(new CellRangeAddress(3, 4, 0, 0));
		titleCell.setCellValue("STT");

		titleCell = title.createCell(1);
		titleCell.setCellStyle(cellStyleCenter);
		sheet.addMergedRegion(new CellRangeAddress(3, 4, 1, 1));
		titleCell.setCellValue("Cán bộ");
		
		Row service = sheet.createRow(4);
		
		//tiêu đề
		//Row month = sheet.createRow(3);
		//j là số thứ tự của cột, bắt đầu đếm từ 0
		int j = 0;
		for (int i = 0; i < monthList.size(); i++) {
			Cell monthCell = title.createCell(j + 2);
			monthCell.setCellStyle(cellStyle);
			sheet.addMergedRegion(new CellRangeAddress(3, 3, j + 2, j + 4));
			j = j + 3;
			monthCell.setCellValue("Tháng " + monthList.get(i).toString());
			
			titleCell = service.createCell(j - 1);
			titleCell.setCellStyle(cellStyle);
			titleCell.setCellValue("Số lần Services");
			sheet.setColumnWidth(j, 10 * 256);

			titleCell = service.createCell(j);
			titleCell.setCellStyle(cellStyle);
			titleCell.setCellValue("Số linh kiện thay thế");
			sheet.setColumnWidth(j, 10 * 256);
			
			titleCell = service.createCell(j + 1);
			titleCell.setCellStyle(cellStyle);
			titleCell.setCellValue("Hướng dẫn qua điện thoại");
			sheet.setColumnWidth(j, 10 * 256);
		}

		// bước 1: lấy bảng tháng
		List<Pair<String, String>> monthListString = getMonthListString(ob.getString("startTime"),
				ob.getString("endTime"));

		// bước 2: lấy dữ liệu của tháng đầu tiên
		List<StatisticalAmountDto> list = statisticalService
				.personStatistical(monthListString.get(0).getFirst(), monthListString.get(0).getSecond()).join();
		List<Row> listDataRow = new ArrayList<>();

		// dữ liệu
		for (int i = 0; i < list.size(); i++) {
			Row data = sheet.createRow(5 + i);
			Cell dataCell = data.createCell(0);
			dataCell.setCellStyle(cellStyle);
			dataCell.setCellValue(i + 1);

			dataCell = data.createCell(1);
			dataCell.setCellStyle(cellStyle);
			dataCell.setCellValue(list.get(i).getUsername());

			dataCell = data.createCell(2);
			dataCell.setCellStyle(cellStyle);
			dataCell.setCellValue(list.get(i).getTotalServices() != null ? list.get(i).getTotalServices() : 0L);

			dataCell = data.createCell(3);
			dataCell.setCellStyle(cellStyle);
			dataCell.setCellValue(list.get(i).getTotalAccessories() != null ? list.get(i).getTotalAccessories() : 0L);
			
			dataCell = data.createCell(4);
			dataCell.setCellStyle(cellStyle);
			dataCell.setCellValue(list.get(i).getTotalMobile() != null ? list.get(i).getTotalMobile() : 0L);

			listDataRow.add(data);
		}

		// Thành thêm các tháng tiếp theo
		try {
			// bước 3: lấy dữ liệu các tháng tiếp theo
			for (int k = 1; k < monthListString.size(); k++) {
				List<StatisticalAmountDto> listTemp = statisticalService
						.personStatistical(monthListString.get(k).getFirst(), monthListString.get(k).getSecond())
						.join();

				// add 3 cột số liệu vào bảng gốc
				for (int i = 0; i < listTemp.size(); i++) {
					Row data = listDataRow.get(i);
					// Cell dataCell = data.createCell(0);
					
					//quy luật	
//					k=1, cell=5		
//					k=2, cell=8		
//					k=3, cell=11
//					k=4, cell=14
//					=> cell = 3k + 2

					Cell dataCell = data.createCell(k * 3 + 2);
					dataCell.setCellStyle(cellStyle);
					dataCell.setCellValue(
							listTemp.get(i).getTotalServices() != null ? listTemp.get(i).getTotalServices() : 0L);

					dataCell = data.createCell(k * 3 + 3);
					dataCell.setCellStyle(cellStyle);
					dataCell.setCellValue(
							listTemp.get(i).getTotalAccessories() != null ? listTemp.get(i).getTotalAccessories() : 0L);

					dataCell = data.createCell(k * 3 + 4);
					dataCell.setCellStyle(cellStyle);
					dataCell.setCellValue(
							listTemp.get(i).getTotalMobile() != null ? listTemp.get(i).getTotalMobile() : 0L);
				}
			}
		} catch (Exception e) {
			log.debug("==============> err: " + e.getMessage());
		}

		workbook.write(bos);
		workbook.close();

		return bos.toByteArray();
	}

	@SuppressWarnings("deprecation")
	public byte[] cyclicalStatistics(String body) throws JSONException, IOException {
		try {
			JSONObject ob = new JSONObject(body);
			List<ATMForStatistical> list = statisticalService.cyclicalStatistics(body).join();

			Integer totalPeriods = list.get(0).getPeriods().size();

			Contract contract = contractRepository.getById(ob.getLong("contractId"));

			Workbook workbook = new XSSFWorkbook();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			Sheet sheet = workbook.createSheet();
			sheet.setColumnWidth(0, 1152);
			sheet.setColumnWidth(1, 3510);
			sheet.setColumnWidth(2, 12725);
			sheet.setColumnWidth(3 + totalPeriods, 3510);

			workbook.setSheetName(0, contract.getBank().getName());

			// merge theo period
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, (3 + totalPeriods)));
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, (2 + totalPeriods)));
			sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, (3 + totalPeriods)));

			sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 0));
			sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 1));
			sheet.addMergedRegion(new CellRangeAddress(1, 2, 2, 2));
			sheet.addMergedRegion(new CellRangeAddress(1, 2, (3 + totalPeriods), (3 + totalPeriods)));

			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			headerStyle.setBorderBottom(BorderStyle.THIN);
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setItalic(true);
			headerFont.setFontHeight((short) (14 * 20));
			headerStyle.setFont(headerFont);

			Row headerRow = sheet.createRow(0);
			headerRow.setHeight((short) 645);
			Cell headerCell = headerRow.createCell(0);
			headerCell.setCellStyle(headerStyle);
			headerCell.setCellValue("BẢNG TỔNG KẾT SERVICES " + contract.getBank().getName() + " THEO CHU KỲ");

			XSSFCellStyle titleStyle = (XSSFCellStyle) workbook.createCellStyle();
			titleStyle.setAlignment(HorizontalAlignment.CENTER);
			titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			titleStyle.setWrapText(true);
			titleStyle.setBorderBottom(BorderStyle.THIN);
			titleStyle.setBorderLeft(BorderStyle.THIN);
			titleStyle.setBorderRight(BorderStyle.THIN);
			titleStyle.setBorderTop(BorderStyle.THIN);
			titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//			titleStyle.setFillForegroundColor(new XSSFColor(new Color(128, 214, 134)));
			XSSFColor xssfColor = new XSSFColor(new byte[] {(byte)128, (byte)214, (byte)134}, null);
			titleStyle.setFillForegroundColor(xssfColor);

			Font titleFont = workbook.createFont();
			titleFont.setBold(true);
			titleFont.setItalic(true);
			titleFont.setFontHeight((short) (10 * 20));
			titleStyle.setFont(titleFont);

			Row titleRow1 = sheet.createRow(1);
			Cell titleCell1 = titleRow1.createCell(0);
			titleCell1.setCellStyle(titleStyle);
			titleCell1.setCellValue("STT");

			titleCell1 = titleRow1.createCell(1);
			titleCell1.setCellStyle(titleStyle);
			titleCell1.setCellValue("Serial number");

			titleCell1 = titleRow1.createCell(2);
			titleCell1.setCellStyle(titleStyle);
			titleCell1.setCellValue("Địa chỉ");

			for (int i = 0; i < totalPeriods; i++) {
				titleCell1 = titleRow1.createCell(3 + i);
				titleCell1.setCellStyle(titleStyle);
			}

			titleCell1 = titleRow1.createCell(3 + totalPeriods);
			titleCell1.setCellStyle(titleStyle);
			titleCell1.setCellValue("Tổng số lần KPSC");

			Row titleRow2 = sheet.createRow(2);
			titleRow2.setHeight((short) 465);

			Cell titleCell2 = titleRow2.createCell(0);
			titleCell2.setCellStyle(titleStyle);
			titleCell2 = titleRow2.createCell(1);
			titleCell2.setCellStyle(titleStyle);
			titleCell2 = titleRow2.createCell(2);
			titleCell2.setCellStyle(titleStyle);
			titleCell2 = titleRow2.createCell(3 + totalPeriods);
			titleCell2.setCellStyle(titleStyle);

			// Foreach theo period
			for (int i = 0; i < totalPeriods; i++) {
				titleCell2 = titleRow2.createCell(3 + i);
				titleCell2.setCellStyle(titleStyle);
				titleCell2.setCellValue(list.get(0).getPeriods().get(i).getName());

				sheet.setColumnWidth(3 + i, 3510);
			}

			XSSFCellStyle subTitleStyle = (XSSFCellStyle) workbook.createCellStyle();
			subTitleStyle.setAlignment(HorizontalAlignment.LEFT);
			subTitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//			subTitleStyle.setFillForegroundColor(new XSSFColor(new Color(168, 211, 237)));
			XSSFColor xssfColor1 = new XSSFColor(new byte[] {(byte)168, (byte)211, (byte)237}, null);
			subTitleStyle.setFillForegroundColor(xssfColor1);

			Row subTitle = sheet.createRow(3);
			Cell subTitleCell = subTitle.createCell(0);
			subTitleCell.setCellStyle(subTitleStyle);
			subTitleCell.setCellValue("HĐ " + contract.getName());

			// CellStyle data
			CellStyle dataCellStyle1 = workbook.createCellStyle();
			dataCellStyle1.setAlignment(HorizontalAlignment.CENTER);
			dataCellStyle1.setBorderBottom(BorderStyle.THIN);
			dataCellStyle1.setBorderLeft(BorderStyle.THIN);
			dataCellStyle1.setBorderRight(BorderStyle.THIN);
			dataCellStyle1.setBorderTop(BorderStyle.THIN);

			CellStyle dataCellStyle2 = workbook.createCellStyle();
			dataCellStyle2.setAlignment(HorizontalAlignment.LEFT);
			dataCellStyle2.setBorderBottom(BorderStyle.THIN);
			dataCellStyle2.setBorderLeft(BorderStyle.THIN);
			dataCellStyle2.setBorderRight(BorderStyle.THIN);
			dataCellStyle2.setBorderTop(BorderStyle.THIN);

			XSSFCellStyle dataCellStyle3 = (XSSFCellStyle) workbook.createCellStyle();
			dataCellStyle3.setAlignment(HorizontalAlignment.CENTER);
			dataCellStyle3.setBorderBottom(BorderStyle.THIN);
			dataCellStyle3.setBorderLeft(BorderStyle.THIN);
			dataCellStyle3.setBorderRight(BorderStyle.THIN);
			dataCellStyle3.setBorderTop(BorderStyle.THIN);
			dataCellStyle3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//			dataCellStyle3.setFillForegroundColor(new XSSFColor(new Color(128, 214, 134)));

			XSSFColor xssfColor2 = new XSSFColor(new byte[] {(byte)128, (byte)214, (byte)134}, null);
			dataCellStyle3.setFillForegroundColor(xssfColor2);

			// print data
			for (int i = 0; i < list.size(); i++) {
				Row dataRow = sheet.createRow(4 + i);
				Cell dataCell = dataRow.createCell(0);
				dataCell.setCellStyle(dataCellStyle1);
				dataCell.setCellValue("" + (i + 1));

				dataCell = dataRow.createCell(1);
				dataCell.setCellStyle(dataCellStyle1);
				dataCell.setCellValue(list.get(i).getSerialNumber());

				dataCell = dataRow.createCell(2);
				dataCell.setCellStyle(dataCellStyle2);
				dataCell.setCellValue(list.get(i).getAddress());

				for (int j = 0; j < totalPeriods; j++) {
					PeriodDto periodDto = list.get(i).getPeriods().get(j);

					dataCell = dataRow.createCell(3 + j);
					if (periodDto.isMaintenance()) {
						dataCell.setCellStyle(dataCellStyle3);
						dataCell.setCellValue("Đã bảo trì");
					} else {
						dataCell.setCellStyle(dataCellStyle2);
						dataCell.setCellValue(" ");
					}
				}

				dataCell = dataRow.createCell(3 + totalPeriods);
				dataCell.setCellStyle(dataCellStyle1);
				dataCell.setCellValue(list.get(i).getTotalKpsc().longValue());

			}

			workbook.write(bos);
			workbook.close();
			return bos.toByteArray();
		} catch (Exception e) {
			log.debug("==============> err: " + e.getMessage());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public byte[] statisticalAccessory(String body) throws IOException, JSONException {
		JSONObject ob = new JSONObject(body);
		long monthsBetween;
		Long contractId = null;
		LocalDateTime startTime = null;
		LocalDateTime endTime = null;
		String endTimeString = null;
		if (StringUtils.isNotBlank(ob.getString("contractId"))) {
			contractId = Long.valueOf(ob.getString("contractId"));
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		if (StringUtils.isNotBlank(ob.getString("startTime"))) {
			startTime = LocalDate.parse(ob.getString("startTime"), formatter).atTime(LocalTime.MIN);
		}
		if (StringUtils.isBlank(ob.getString("endTime"))) {
			endTime = LocalDateTime.now();
		} else {
			endTime = LocalDate.parse(ob.getString("endTime"), formatter).atTime(LocalTime.MAX);
			endTimeString = ob.getString("endTime");
		}

		if ((startTime != null) && (endTime != null)) {
			monthsBetween = ChronoUnit.MONTHS.between(startTime.withDayOfMonth(1), endTime.withDayOfMonth(1));
		} else {
			monthsBetween = 5;
		}

		List<Long> listMonth = new ArrayList<>();
		for (long i = monthsBetween; i >= 0; i--) {
			LocalDateTime now = endTime;
			now = now.minusMonths(i);
			int month = now.getMonthValue();
			listMonth.add(Long.parseLong(String.valueOf(month)));
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Workbook workbook = new XSSFWorkbook();

		Sheet sheet = workbook.createSheet("Báo cáo thống kê theo linh kiện");
		sheet.setColumnWidth(0, 30 * 256);
		sheet.setColumnWidth(1, 30 * 356);
//		sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 1));
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, (int) monthsBetween + 2));
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, (int) monthsBetween + 2));
		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		CellStyle titleStyle = workbook.createCellStyle();
		titleStyle.setBorderBottom(BorderStyle.THIN);
		titleStyle.setBorderLeft(BorderStyle.THIN);
		titleStyle.setBorderRight(BorderStyle.THIN);
		titleStyle.setBorderTop(BorderStyle.THIN);
		titleStyle.setAlignment(HorizontalAlignment.CENTER);
		titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		titleStyle.setFont(headerFont);

		Row header = sheet.createRow(0);
		header.setHeight((short) 645);
		Cell headerCell = header.createCell(0);
		headerCell.setCellStyle(headerStyle);
		headerCell.setCellValue("BÁO CÁO THỐNG KÊ THEO LINH KIỆN");

		CellStyle dataStyle = workbook.createCellStyle();
		dataStyle.setAlignment(HorizontalAlignment.LEFT);
		dataStyle.setWrapText(true);
		dataStyle.setBorderBottom(BorderStyle.THIN);
		dataStyle.setBorderLeft(BorderStyle.THIN);
		dataStyle.setBorderRight(BorderStyle.THIN);
		dataStyle.setBorderTop(BorderStyle.THIN);

		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		CellStyle centerStyle = workbook.createCellStyle();
		centerStyle.setAlignment(HorizontalAlignment.CENTER);
		centerStyle.setBorderBottom(BorderStyle.THIN);
		centerStyle.setBorderLeft(BorderStyle.THIN);
		centerStyle.setBorderRight(BorderStyle.THIN);
		centerStyle.setBorderTop(BorderStyle.THIN);

		Row title = sheet.createRow(1);

		Cell titleCell = title.createCell(0);
		titleCell.setCellValue("Linh kiện");
		titleCell.setCellStyle(titleStyle);
		
		Cell titleCell2 = title.createCell(1);
		titleCell2.setCellValue("Dòng máy phù hợp");
		titleCell2.setCellStyle(titleStyle);
		
		Cell titleCell12 = title.createCell(2);
		titleCell12.setCellValue("Số lượng");
		titleCell12.setCellStyle(titleStyle);

		Row title2 = sheet.createRow(2);
		for (int i = 0; i < listMonth.size(); i++) {
			Cell titleCell1 = title2.createCell(i + 2);
			titleCell1.setCellValue("Tháng " + listMonth.get(i));
			titleCell1.setCellStyle(titleStyle);
		}

		List<Map<String, Object>> data = statisticalService.getListContractATM(contractId, monthsBetween,
				endTimeString);
		// write data
		CellStyle cs = workbook.createCellStyle();
		cs.setWrapText(true);
		for (int i = 0; i < data.size(); i++) {
			Row data2 = sheet.createRow(3 + i);
			Cell dataCell = data2.createCell(0);

			dataCell = data2.createCell(0);
			dataCell.setCellValue((String) data.get(i).get("name"));
			Cell dataCell1 = data2.createCell(1);
			dataCell1 = data2.createCell(1);
			dataCell1.setCellValue((String) data.get(i).get("series"));
			
			dataCell.setCellStyle(dataStyle);
			dataCell1.setCellStyle(dataStyle);
			
			List<Long> listCount = new ArrayList<>();
			listCount = (List<Long>) data.get(i).get("count");
			for (int j = 0; j < listCount.size(); j++) {
				dataCell = data2.createCell(j + 2);
				dataCell.setCellValue((Long) listCount.get(j));
				dataCell.setCellStyle(centerStyle);
			}
		}

		workbook.write(bos);
		workbook.close();

		return bos.toByteArray();
	}
	
	public byte[] statisticalByMonth(String body) throws IOException, ParseException, JSONException {
		log.debug("Đang chạy function xuất excel thống kê service...");
		JSONObject ob = new JSONObject(body);
		InputStream inputStream = new ClassPathResource("static/reports/thong-ke-service-template.xlsx").getInputStream();
		Workbook workbook = new XSSFWorkbook(inputStream);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		Sheet sheet = workbook.getSheetAt(0);
		
		Long contractId = StringUtils.isNotBlank(ob.getString("contractId")) ? ob.getLong("contractId") : null;
		Long regionId = StringUtils.isNotBlank(ob.getString("regionId")) ? ob.getLong("regionId") : null;
		Long departmentId = StringUtils.isNotBlank(ob.getString("departmentId")) ? ob.getLong("departmentId") : null;
		
		//Thông tin hợp đồng 
		Row row0 = sheet.getRow(0);
		Cell contractCell = row0.createCell(2);
		if (contractId != null) {
			contractCell.setCellValue(contractRepository.getById(departmentId).getName());
		}else {
			contractCell.setCellValue("Tất cả hợp đồng");
		}
		
		Row row1 = sheet.getRow(1);
		Cell startTimeCell = row1.createCell(2);
		startTimeCell.setCellValue(ob.getString("startTime"));

		Row row2 = sheet.getRow(2);
		Cell endTimeCell = row2.createCell(2);
		endTimeCell.setCellValue(ob.getString("endTime"));
		
		Map<Object, Object> data = statisticalService.statiscalAmountServicesForExport(contractId, regionId, departmentId, 
				CustomFormatDate.formatStartTimeForInput(ob.getString("startTime")), CustomFormatDate.formartEndTimeForInput(ob.getString("endTime")));
		@SuppressWarnings("unchecked")
		List<String> months = (List<String>) data.get("months");
		@SuppressWarnings("unchecked")
		List<Map<Object, Object>> listData = (List<Map<Object,Object>>) data.get("data");
		
		//Cell style
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setWrapText(true);
		
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		
		// title bảng
		if (!months.isEmpty() && listData != null && !listData.isEmpty()) {
			for (int borderRow = 4; borderRow < 7 + (int) data.get("departmentSize"); borderRow++) {
				Row row = sheet.getRow(borderRow) != null ? sheet.getRow(borderRow) : sheet.createRow(borderRow);
				for (int borderCol = 0; borderCol < 3 + 3*months.size(); borderCol++) {
					Cell cell = row.getCell(borderCol) != null ? row.getCell(borderCol) : row.createCell(borderCol);
					cell.setCellStyle(cellStyle);
				}
			}
			Row row4 = sheet.getRow(4);
			Cell title1 = row4.getCell(3);
			title1.setCellValue("Số lần service");
			sheet.addMergedRegion(new CellRangeAddress(4, 4, 3, 2 + 3*months.size()));
			
			for (int i = 1; i <= months.size(); i++) {
				Row row5 = sheet.getRow(5);
				Cell title2= row5.getCell(3*i);
				title2.setCellValue(months.get(i-1));
				sheet.addMergedRegion(new CellRangeAddress(5, 5, 3*i, 3*i + 2));
				
				Row row6 = sheet.getRow(6);
				Cell title3= row6.getCell(3*i);
				title3.setCellValue("Số services");
				title3 = row6.getCell(3*i + 1);
				title3.setCellValue("Số máy");
				title3 = row6.getCell(3*i + 2);
				title3.setCellValue("Số services / số máy");
			}
		
			//print data
			int currentRow = 7;
			@SuppressWarnings("unchecked")
			List<Map<Object, Object>> region1 = (List<Map<Object, Object>>) listData.get(0).get("regions");
			for (int i = 0; i < region1.size(); i++) {
				@SuppressWarnings("unchecked")
				List<Map<Object, Object>> departments = (List<Map<Object, Object>>) region1.get(i).get("departments");
				if (departments!= null && !departments.isEmpty()) {
					Row regionRow = sheet.getRow(currentRow);
					Cell sttCell = regionRow.getCell(0);
					sttCell.setCellValue(i + 1);
					
					Cell regionCell = regionRow.getCell(1);
					regionCell.setCellValue((String) region1.get(i).get("regionName"));
					if (currentRow < currentRow + departments.size() - 1) {
						sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow + departments.size() - 1, 0, 0));
						sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow + departments.size() - 1, 1, 1));
					}
					
					currentRow = currentRow + departments.size();
				}
			}
			
			for (int i = 0; i < listData.size(); i++) {
				final int finalI = i;
				@SuppressWarnings("unchecked")
				List<Map<Object, Object>> regions1 = (List<Map<Object, Object>>) listData.get(i).get("regions");
				int rowNumber = 7;
				for (Map<Object, Object> r : regions1) {
					@SuppressWarnings("unchecked")
					List<Map<Object, Object>> departments1 = (List<Map<Object, Object>>) r.get("departments");
					
					if (departments1 != null && !departments1.isEmpty()) {
						for (int j = 0; j < departments1.size(); j++) {
							Row departmentRow = sheet.getRow(rowNumber + j);
							Cell departmentCell = departmentRow.getCell(2);
							departmentCell.setCellValue((String) departments1.get(j).get("departmentName"));
							
							departmentCell = departmentRow.getCell(3 + finalI*3);
							departmentCell.setCellValue(Long.valueOf(departments1.get(j).get("countService").toString()));
							
							departmentCell = departmentRow.getCell(3 + finalI*3 + 1);
							departmentCell.setCellValue(Long.valueOf(departments1.get(j).get("countAtm").toString()));
							
							departmentCell = departmentRow.getCell(3 + finalI*3 + 2);
							departmentCell.setCellValue(Double.parseDouble(new DecimalFormat("##.##").format(1.0d * Long.valueOf(departments1.get(j).get("countService").toString()) / Long.valueOf(departments1.get(j).get("countAtm").toString()))));
						}
						rowNumber = rowNumber + departments1.size();
					}
				}
			}
			
		}
		
		workbook.write(bos);
		workbook.close();

		return bos.toByteArray();
	}
}
