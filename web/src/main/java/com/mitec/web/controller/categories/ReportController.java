package com.mitec.web.controller.categories;

import com.mitec.business.dto.ReportDTO;
import com.mitec.business.model.Contract;
import com.mitec.business.model.Report;
import com.mitec.business.repository.ContractRepository;
import com.mitec.business.service.categories.ReportService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {


    @Autowired
    private ReportService reportService;
    @Autowired
    ContractRepository contractRepository;

    @GetMapping
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping("/{id}")
    public ReportDTO getReportById(@PathVariable Long id) {
        Report report =  reportService.getReportById(id);

        ReportDTO dto = new ReportDTO();
        dto.setId(report.getId());
        dto.setTemplate(report.getTemplate());
        dto.setName(report.getName());
        dto.setContractId(report.getContract().getId());
    return dto;
    }

//    @PostMapping
//    public Report createReport(@RequestBody Report report) {
//        return reportService.saveReport(report);
//    }

    @PostMapping
    public Report createReport(@RequestParam("id_add_report") Long id,
                               @RequestParam("name") String name,
                               @RequestParam("template") String template) {
        Report report = new Report();
        report.setTemplate(template);
        report.setName(name);

        // Fetch the Contract based on the ID
        Contract contract = contractRepository.getById(id);
        report.setContract(contract);

        return reportService.saveReport(report);
    }

    @DeleteMapping("/{id}")
    public void deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
    }


    @ResponseBody
    @PostMapping("/list-report")
    public ResponseEntity<Object> listContractReport(@RequestBody String body) throws org.springframework.boot.configurationprocessor.json.JSONException {
        try {
            JSONObject jsonObject = new JSONObject(body);
            Long contractId = jsonObject.getLong("contract_id");
            List<ReportDTO> listReport = reportService.getReportsByContractId(contractId);
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







}
