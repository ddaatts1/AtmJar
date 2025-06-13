package com.mitec.api.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import com.mitec.business.model.ATM;
import com.mitec.business.model.AddressUpdateRequest;
import com.mitec.business.model.ReportPDF;
import com.mitec.business.model.User;
import com.mitec.business.service.DocxService;
import com.mitec.business.service.ReportPDFService1;
import com.mitec.business.service.UserService;
import com.mitec.business.service.categories.AtmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.core.io.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mitec.api.config.AppProperties;
import com.mitec.api.service.AuthorizationService;
import com.mitec.business.service.FileService;
import com.mitec.business.service.api.ApiService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class MainController {

	@Autowired
	private ApiService apiService;

	@Autowired
	private FileService fileService;

	@Autowired
	private AppProperties appProperties;

	@Autowired
	AtmService atmService;

	@Autowired
	UserService userService;
	@Autowired
	ReportPDFService1 reportPDFService;

	@Autowired
	DocxService docxService;
	private static final String UPLOAD_DIR = "C:\\MitecAtm";

	@Autowired
	private ResourceLoader resourceLoader;
	private HttpHeaders configHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-type", "application/json; charset=UTF-8");

		return headers;
	}

	@PostMapping(value = "/checkIn")
	public ResponseEntity<Object> checkIn(@RequestBody String body) {
		try {
			return new ResponseEntity<>(apiService.checkIn(body).join(), configHeaders(), HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), configHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/personalHistory")
	public ResponseEntity<Object> personalHistory(@RequestBody String body) throws JSONException {
		return new ResponseEntity<>(apiService.personalHistory(body), configHeaders(), HttpStatus.OK);
	}

	@PostMapping(value = "/atmHistory")
	public ResponseEntity<Object> atmHistory(@RequestBody String body) throws JSONException {
		return new ResponseEntity<>(apiService.atmHistory(body), configHeaders(), HttpStatus.OK);
	}

	@PostMapping(value = "/cancelJob")
	public ResponseEntity<Object> cancelJob(@RequestBody String body) throws JSONException, IOException {
		return new ResponseEntity<>(apiService.cancelJob(body, appProperties.getAttachmentMiniPath()), configHeaders(), HttpStatus.OK);
	}

	@GetMapping(value = "/getJob")
	public ResponseEntity<Object> getJobDetail(@RequestParam(name = "jobId") Long jobId) {
		log.debug("get job()....");
		return new ResponseEntity<>(apiService.jobDetail(jobId), configHeaders(), HttpStatus.OK);
	}

	@PostMapping(value = "/checkOut")
	public ResponseEntity<Object> checkOut(@RequestBody String body, HttpServletRequest req) throws JSONException {
		return new ResponseEntity<>(apiService.checkOut(body, AuthorizationService.getUser(req.getHeader("Authorization"))), configHeaders(), HttpStatus.OK);
	}

	@GetMapping(value = "/jobProcessing")
	public ResponseEntity<Object> getJobProcessing(@RequestParam(name = "username") String username) {
		return new ResponseEntity<>(apiService.jobProcessing(username), configHeaders(), HttpStatus.OK);
	}

	@GetMapping(value = "/getAllDevice")
	public ResponseEntity<Object> getAllDevice() {
		log.debug("get all device");
		return new ResponseEntity<>(apiService.getAllDevice(), configHeaders(), HttpStatus.OK);
	}

	@GetMapping(value = "/getAccessoryByErrorDeviceId")
	public ResponseEntity<Object> getAccessoryByDeviceId(@RequestParam(name = "errorDeviceId") Long errorDeviceId) {
		return new ResponseEntity<>(apiService.getAccessoryByErrorDeviceId(errorDeviceId), configHeaders(), HttpStatus.OK);
	}

	@GetMapping(value = "/getErrorByDeviceId")
	public ResponseEntity<Object> getErrorByDeviceId(@RequestParam(name = "deviceId") Long deviceId) {
		return new ResponseEntity<>(apiService.getErrorByDeviceId(deviceId), configHeaders(), HttpStatus.OK);
	}

	@GetMapping(value = "/getAllJobPerform")
	public ResponseEntity<Object> getAllJobPerform() {
		return new ResponseEntity<>(apiService.getAllJobPerform(), configHeaders(), HttpStatus.OK);
	}

	@GetMapping(value = "/getAllError")
	public ResponseEntity<Object> getAllError() {
		return new ResponseEntity<>(apiService.getAllError(), configHeaders(), HttpStatus.OK);
	}

	@GetMapping(value = "/getAllAccessory")
	public ResponseEntity<Object> getAllAccessory() {
		return new ResponseEntity<>(apiService.getAllAccessory(), configHeaders(), HttpStatus.OK);
	}

	@PostMapping(value = "/getReport")
	public ResponseEntity<Object> getReport(@RequestBody String input) {
		try {
			return new ResponseEntity<>(fileService.genPdf(input, appProperties.getAttachmentPath(),
					appProperties.getAttachmentContextPath()), configHeaders(), HttpStatus.OK);
		}
		catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/searchAtm")
	public ResponseEntity<Object> searchATM(@RequestParam(name = "search") String input) {
		return new ResponseEntity<>(apiService.searchAtm(input), configHeaders(), HttpStatus.OK);
	}

	@PostMapping(value = "/getAllAtm")
	public ResponseEntity<Object> getAllAtm(@RequestBody String body) {
		try {
			return new ResponseEntity<>(apiService.getAllAtm(body), configHeaders(), HttpStatus.OK);
		}catch (Exception e) {
			log.debug("Error: " + e.getMessage());
			return new ResponseEntity<>(e.getMessage(), configHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/getUser")
	public ResponseEntity<Object> getUserDetail(@RequestParam(name = "username") String username) {
		return new ResponseEntity<>(apiService.getUserDetail(username), configHeaders(), HttpStatus.OK);
	}

	@PostMapping(value = "/atmHistoryPage")
	public ResponseEntity<Object> atmHistoryPagination(@RequestBody String body) {
		try {
			log.debug("body =========> " + body);
			return new ResponseEntity<>(apiService.atmHistoryPagination(body), configHeaders(), HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), configHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/personalHistoryPage")
	public ResponseEntity<Object> personalHistoryPagination(@RequestBody String body) {
		try {
			return new ResponseEntity<>(apiService.personalHistoryPage(body), configHeaders(), HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), configHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/getAllErrorDevice")
	public ResponseEntity<Object> getAllErrorDevice() {
		try {
			return new ResponseEntity<>(apiService.getAllErrorDevice(), configHeaders(), HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), configHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/update-address")
	public ResponseEntity<ATM> updateAtmAddress(@RequestBody AddressUpdateRequest request,
												HttpServletRequest req
	) {
		try {
			String userName = AuthorizationService.getUser(req.getHeader("Authorization"));
			ATM updatedAtm = atmService.updateAddress(request,userName);
			return new ResponseEntity<>( HttpStatus.OK);
		} catch (EntityNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}


//
//	@GetMapping("/{id}/view")
//	public ResponseEntity<Resource> viewReport(@PathVariable Long id,
//											   @RequestParam(value = "userName", defaultValue = "2") String userName,
//											   @RequestParam(value = "serialNumber", defaultValue = "5300377162") String serialNumber) {
//		ReportPDF reportPDF = reportPDFService.findById(id)
//				.orElseThrow(() -> new IllegalArgumentException("Invalid report Id:" + id));
//
//		Path filePath = Paths.get(UPLOAD_DIR, reportPDF.getFileName());
//		File pdfFile = filePath.toFile();
//
//		String name = "";
//		String phone = "";
//		String address = "";
//
//		User user = userService.getUserByUserName(userName);
//		if (user != null) {
//			name = user.getFullName();
//			phone = user.getPhoneNumber();
//		}
//
//		ATM atm = atmService.getATMBySerialNumber(serialNumber);
//		if (atm != null) {
//			address = atm.getAddress();
//		}
//
//		try {
//			// Load the PDF document
//			PDDocument document = PDDocument.load(pdfFile);
//
//			// Load the font file from classpath using ResourceLoader
//			Resource fontResource = resourceLoader.getResource("classpath:static/fonts/times-new-roman-14.ttf");
//			try (InputStream fontStream = fontResource.getInputStream()) {
//				PDType0Font font = PDType0Font.load(document, fontStream);
//
//				// Function to add text to the appropriate page
//				addTextToPage(document, reportPDF.getXPosition(), reportPDF.getYPosition(), font, name);
//				addTextToPage(document, reportPDF.getXPosition1(), reportPDF.getYPosition1(), font, phone);
//				addTextToPage(document, reportPDF.getXPosition2(), reportPDF.getYPosition2(), font, serialNumber);
//				addTextToPage(document, reportPDF.getXPosition4(), reportPDF.getYPosition4(), font, name);
//
//				if(reportPDF.getXPosition31() != -1 && reportPDF.getYPosition31() !=-1){
//					// Split the address into words
//					String[] words = address.split("\\s+");
//
//					// Handle edge cases: if there are fewer than 3 words
//					if (words.length < 3) {
//						String address1 = address; // If not enough words, use the whole address as address1
//						String address2 = ""; // No remaining address
//						addTextToPage(document, reportPDF.getXPosition3(), reportPDF.getYPosition3(), font, address1);
//					} else {
//						// Extract the first 3 words
//						String address1 = String.join(" ", Arrays.copyOfRange(words, 0, 3));
//
//						// Extract the remaining words
//						String address2 = String.join(" ", Arrays.copyOfRange(words, 3, words.length));
//
//
//						// Use address1 or address2 as needed
//						addTextToPage(document, reportPDF.getXPosition3(), reportPDF.getYPosition3(), font, address1);
//						addTextToPage(document, reportPDF.getXPosition31(), reportPDF.getYPosition31(), font, address2);
//					}
//
//				}else {
//					addTextToPage(document, reportPDF.getXPosition3(), reportPDF.getYPosition3(), font, address);
//
//				}
//
//				// Save the modified PDF to a new file
//				Path modifiedFilePath = Paths.get(UPLOAD_DIR, "modified_" + reportPDF.getFileName());
//				document.save(modifiedFilePath.toFile());
//				document.close();
//
//				// Return the modified PDF file as a viewable response
//				Resource resource = new UrlResource(modifiedFilePath.toUri());
//				return ResponseEntity.ok()
//						.header(HttpHeaders.CONTENT_TYPE, "application/pdf")
//						.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
//						.body(resource);
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//		}
//	}






	@GetMapping("/{id}/view")
	public ResponseEntity<Resource> viewReport(@PathVariable Long id,
											   @RequestParam(value = "userName", defaultValue = "hoandg") String userName,
											   @RequestParam(value = "serialNumber", defaultValue = "5300377162") String serialNumber) {
		ReportPDF reportPDF = reportPDFService.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid report Id:" + id));

		Path filePath = Paths.get(UPLOAD_DIR, reportPDF.getFileName());
		File docFile = filePath.toFile();

		String name = "";
		String phone = "";
		String address = "";
		String atmId = "";

		User user = userService.getUserByUserName(userName);
		if (user != null) {
			name = user.getFullName();
			phone = user.getPhoneNumber();
		}

		ATM atm = atmService.getATMBySerialNumber(serialNumber);
		if (atm != null) {
			String temp = atm.getAddress();
			address = extractAddress(temp, 1);
			atmId = extractAddress(temp, 0);
		}

		try {
			// Get the processed PDF as a byte array
			byte[] pdfBytes = docxService.addTextAfterString(docFile, name, address, serialNumber, "\n\n\n\n\n\n" + name, phone, reportPDF, atmId);

			HttpHeaders headers = new HttpHeaders();
			String fileName = reportPDF.getFileName();
			int underscoreIndex = fileName.indexOf('_') + 1;
			int dotIndex = fileName.lastIndexOf('.');

// Check if there is an extension
			String fileNameWithoutExtension;
			if (dotIndex > underscoreIndex) {
				fileNameWithoutExtension = fileName.substring(underscoreIndex, dotIndex); // Extract name without extension
			} else {
				fileNameWithoutExtension = fileName.substring(underscoreIndex); // No extension, take everything after the underscore
			}


			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + serialNumber + " " + fileNameWithoutExtension + ".pdf");
			headers.setContentType(MediaType.APPLICATION_PDF);

			// Return the PDF as a ByteArrayResource
			return ResponseEntity.ok()
					.headers(headers)
					.contentLength(pdfBytes.length)
					.body(new ByteArrayResource(pdfBytes));

		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}

//		try {
//			// Get the processed DOCX as a byte array
//			byte[] docxBytes = docxService.addTextAfterString(docFile, name, address, serialNumber, "\n\n\n\n\n\n" + name, phone, reportPDF, atmId);
//
//			HttpHeaders headers = new HttpHeaders();
//		String fileName = reportPDF.getFileName();
//		int underscoreIndex = fileName.indexOf('_') + 1;
//		int dotIndex = fileName.lastIndexOf('.');
//
//// Check if there is an extension
//		String fileNameWithoutExtension;
//		if (dotIndex > underscoreIndex) {
//			fileNameWithoutExtension = fileName.substring(underscoreIndex, dotIndex); // Extract name without extension
//		} else {
//			fileNameWithoutExtension = fileName.substring(underscoreIndex); // No extension, take everything after the underscore
//		}

		//
//			// Set the filename and content type for DOCX
//			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + serialNumber + " " + fileNameWithoutExtension + ".docx");
//			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // DOCX is typically served as an octet-stream
//
//			// Return the DOCX as a ByteArrayResource
//			return ResponseEntity.ok()
//					.headers(headers)
//					.contentLength(docxBytes.length)
//					.body(new ByteArrayResource(docxBytes));
//
//		} catch (IOException e) {
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//		}

	}

	// index 1: address
	// index 0: atm id
	public static String extractAddress(String address,int  index) {
		// Check if the address contains a hyphen
		if (address.contains(" - ")) {
			// Split the string by " - " and take the second part
			return address.split(" - ")[index].trim();
		} else if (index ==1){
			// Return the entire address if no hyphen is found
			return address.trim();
		}else {
			return "";
		}
	}


}
