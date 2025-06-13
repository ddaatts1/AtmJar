package com.mitec.business.service.categories;

import com.mitec.business.dto.ReportDTO;
import com.mitec.business.model.Report;
import com.mitec.business.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id).orElse(null);
    }

    public Report saveReport(Report report) {
        return reportRepository.save(report);
    }

    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
    // New method to get all reports by contract ID
    public List<ReportDTO> getReportsByContractId(Long contractId) {
        List<Report> reports = reportRepository.findByContractId(contractId);
        return reports.stream().map(report -> {
            ReportDTO dto = new ReportDTO();
            dto.setId(report.getId());
            dto.setTemplate(report.getTemplate());
            dto.setName(report.getName());
            dto.setContractId(report.getContract().getId());
            return dto;
        }).collect(Collectors.toList());
    }
}