package com.mitec.web.controller.categories;

import com.mitec.business.dto.ATMDto;
import com.mitec.business.dto.ReportDTO;
import com.mitec.business.dto.ReportPdfDTO;
import com.mitec.business.model.ATM;
import com.mitec.business.model.Contract;
import com.mitec.business.model.ReportPDF;
import com.mitec.business.model.User;
import com.mitec.business.repository.ContractRepository;
import com.mitec.business.repository.UserRepository;
import com.mitec.business.service.DocxService;
import com.mitec.business.service.UserService;
import com.mitec.business.service.categories.AtmService;
import com.mitec.web.service.ReportPDFService;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.core.io.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/report")
public class ReportPDFController {

    @Autowired
    private ReportPDFService reportPDFService;
    @Autowired
    ContractRepository contractRepository;
    @Autowired
    UserService userService;
    @Autowired
    AtmService atmService;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    DocxService docxService;
    private static final String UPLOAD_DIR = "C:\\MitecAtm";

    @GetMapping("/addReport")
    public String index(Model model) {
        model.addAttribute("reportPDF", new ReportPDF());
        return "addReport";
    }

//    @PostMapping("/upload")
//    public String uploadReport(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("id_add_report") Long id,
//            @RequestParam(value = "xPosition1",defaultValue = "-1") float xPosition1,
//            @RequestParam(value = "yPosition1",defaultValue = "-1") float yPosition1,
//            @RequestParam(value = "xPosition2",defaultValue = "-1") float xPosition2,
//            @RequestParam(value = "yPosition2",defaultValue = "-1") float yPosition2,
//            @RequestParam(value = "xPosition3",defaultValue = "-1") float xPosition3,
//            @RequestParam(value = "yPosition3",defaultValue = "-1") float yPosition3,
//            @RequestParam(value = "xPosition4",defaultValue = "-1") float xPosition4,
//            @RequestParam(value = "yPosition4",defaultValue = "-1") float yPosition4,
//            @RequestParam(value = "xPosition41",defaultValue = "-1") float xPosition41,
//            @RequestParam(value = "yPosition41",defaultValue = "-1") float yPosition41,
//            @RequestParam(value = "xPosition5",defaultValue = "-1") float xPosition5,
//            @RequestParam(value = "yPosition5",defaultValue = "-1") float yPosition5,
//
//            Model model) {
//
//        try {
//            // Save the file
//            String fileName = UUID.randomUUID().toString()+"_"+file.getOriginalFilename();
//            Path filePath = Paths.get(UPLOAD_DIR, fileName);
//            Files.createDirectories(filePath.getParent());
//            Contract contract = contractRepository.getById(id);
//
//            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
//                fos.write(file.getBytes());
//            }
//
//            // Save report details to database
//            ReportPDF reportPDF = new ReportPDF();
//            reportPDF.setFileName(fileName);
//            reportPDF.setXPosition(xPosition1);
//            reportPDF.setYPosition(yPosition1);
//            reportPDF.setXPosition1(xPosition2);
//            reportPDF.setYPosition1(yPosition2);
//            reportPDF.setXPosition2(xPosition3);
//            reportPDF.setYPosition2(yPosition3);
//            reportPDF.setXPosition3(xPosition4);
//            reportPDF.setYPosition3(yPosition4);
//            reportPDF.setXPosition31(xPosition41);
//            reportPDF.setYPosition31(yPosition41);
//            reportPDF.setXPosition4(xPosition5);
//            reportPDF.setYPosition4(yPosition5);
//            reportPDF.setContract(contract);
//            reportPDFService.save(reportPDF);
//
//            model.addAttribute("message", "Report uploaded successfully");
//            return "redirect:/contracts"; // Redirect to avoid form resubmission
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            model.addAttribute("error", "Failed to upload report");
//            return "redirect:/contracts"; // Redirect to avoid form resubmission
//        }
//    }



    @PostMapping("/upload")
    public String uploadReport(
            @RequestParam("file") MultipartFile file,
            @RequestParam("id_add_report") Long id,
            @RequestParam(value = "keyword1",defaultValue = "") String keyword1,
            @RequestParam(value = "index1",defaultValue = "-1") int index1,
            @RequestParam(value = "keyword2",defaultValue = "") String keyword2,
            @RequestParam(value = "index2",defaultValue = "-1") int index2,
            @RequestParam(value = "keyword3",defaultValue = "") String keyword3,
            @RequestParam(value = "index3",defaultValue = "-1") int index3,
            @RequestParam(value = "keyword4",defaultValue = "") String keyword4,
            @RequestParam(value = "index4",defaultValue = "-1") int index4,

            @RequestParam(value = "keyword5",defaultValue = "") String keyword5,
            @RequestParam(value = "index5",defaultValue = "-1") int index5,
            @RequestParam(value = "keyword6",defaultValue = "") String keyword6,
            @RequestParam(value = "index6",defaultValue = "-1") int index6,
            Model model) {

        try {
            // Save the file
            String fileName = UUID.randomUUID().toString()+"_"+file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.createDirectories(filePath.getParent());
            Contract contract = contractRepository.getById(id);

            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                fos.write(file.getBytes());
            }

            // Save report details to database
            ReportPDF reportPDF = new ReportPDF();
            reportPDF.setFileName(fileName);

            reportPDF.setKeyword1(keyword1);
            reportPDF.setIndex1(index1);
            reportPDF.setKeyword2(keyword2);
            reportPDF.setIndex2(index2);
            reportPDF.setKeyword3(keyword3);
            reportPDF.setIndex3(index3);
            reportPDF.setKeyword4(keyword4);
            reportPDF.setIndex4(index4);
            reportPDF.setKeyword5(keyword5);
            reportPDF.setIndex5(index5);

            reportPDF.setKeyword6(keyword6);
            reportPDF.setIndex6(index6);

            reportPDF.setContract(contract);
            reportPDFService.save(reportPDF);

            model.addAttribute("message", "Report uploaded successfully");
            return "redirect:/contracts"; // Redirect to avoid form resubmission

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to upload report");
            return "redirect:/contracts"; // Redirect to avoid form resubmission
        }
    }

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

//        try {
//            // Get the processed PDF as a byte array
//            byte[] pdfBytes = docxService.addTextAfterString(docFile, name, address, serialNumber, "\n\n\n\n\n\n" + name, phone, reportPDF, atmId);
//
//            HttpHeaders headers = new HttpHeaders();
//            String fileName = reportPDF.getFileName();
//            int underscoreIndex = fileName.indexOf('_') + 1;
//            int dotIndex = fileName.lastIndexOf('.');
//
//// Check if there is an extension
//            String fileNameWithoutExtension;
//            if (dotIndex > underscoreIndex) {
//                fileNameWithoutExtension = fileName.substring(underscoreIndex, dotIndex); // Extract name without extension
//            } else {
//                fileNameWithoutExtension = fileName.substring(underscoreIndex); // No extension, take everything after the underscore
//            }
//
//            String bank = reportPDF.getContract().getBank().getName();
//            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + serialNumber + " " +bank+" "+ fileNameWithoutExtension + ".pdf");
//            headers.setContentType(MediaType.APPLICATION_PDF);
//
//            // Return the PDF as a ByteArrayResource
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .contentLength(pdfBytes.length)
//                    .body(new ByteArrayResource(pdfBytes));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }

        try {
            // Get the processed DOCX as a byte array
            byte[] docxBytes = docxService.addTextAfterString(docFile, name, address, serialNumber, "\n\n\n\n\n\n" + name, phone, reportPDF, atmId);

            HttpHeaders headers = new HttpHeaders();
            String secondHalf = reportPDF.getFileName().substring(reportPDF.getFileName().indexOf('_') + 1);

            // Set the filename and content type for DOCX
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + serialNumber + ".docx");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // DOCX is typically served as an octet-stream

            // Return the DOCX as a ByteArrayResource
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(docxBytes.length)
                    .body(new ByteArrayResource(docxBytes));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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




    @ResponseBody
    @PostMapping("/list-report")
    public ResponseEntity<Object> listContractReport(@RequestBody String body) throws org.springframework.boot.configurationprocessor.json.JSONException {
        try {
            JSONObject jsonObject = new JSONObject(body);
            Long contractId = jsonObject.getLong("contract_id");
            List<ReportPdfDTO> listReport = reportPDFService.getReportsByContractId(contractId);
            return new ResponseEntity<>(listReport, HttpStatus.OK);
        } catch (org.json.JSONException e) {
            return new ResponseEntity<>("Invalid JSON format", HttpStatus.BAD_REQUEST);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Invalid contract ID format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
        try {
            // Find the reportPDF by ID
            ReportPDF reportPDF = reportPDFService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid report Id:" + id));

            // Delete the file from the filesystem
            Path filePath = Paths.get(UPLOAD_DIR, reportPDF.getFileName());
            File fileToDelete = filePath.toFile();
            if (fileToDelete.exists() && !fileToDelete.delete()) {
                throw new IOException("Failed to delete file: " + filePath.toString());
            }

            // Delete the reportPDF entry from the database
            reportPDFService.delete(id);

            return ResponseEntity.ok().body("Report deleted successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete the file");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Report not found");
        }
    }


    @GetMapping("/reportsBycontract/{contractId}")
    public ResponseEntity<List<ReportPdfDTO>> getReportsByContractId(@PathVariable Long contractId) {
        List<ReportPdfDTO> reports = reportPDFService.getReportsByContractId(contractId);
        if (reports.isEmpty()) {
            return ResponseEntity.noContent().build(); // Returns HTTP 204 if no reports are found
        }
        return ResponseEntity.ok(reports); // Returns HTTP 200 with the list of reports
    }


    @ResponseBody
    @PostMapping("/get-reports")
    public ResponseEntity<Object> listContractReport1(@RequestParam String serialNumber) {
        try {
            ATM atm = atmService.getATMBySerialNumber(serialNumber);
            Contract contract = atm.getContracts().get(0);
            List<ReportPdfDTO> listReport = reportPDFService.getReportsByContractId(contract.getId());
            return new ResponseEntity<>(listReport, HttpStatus.OK);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
    }


}
