package com.mitec.web.service;


import com.mitec.business.dto.ReportDTO;
import com.mitec.business.dto.ReportPdfDTO;
import com.mitec.business.model.Report;
import com.mitec.business.model.ReportPDF;
import com.mitec.business.repository.ReportPDFRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportPDFService {

    @Autowired
    private ReportPDFRepository reportPDFRepository;

    private static final String FILE_STORAGE_DIR = "C:/MitecAtm/";


    public ReportPDF getReportPDF(Long id) {
        return reportPDFRepository.findById(id).orElse(null);
    }



    public Optional<ReportPDF> findById(Long id) {
        return reportPDFRepository.findById(id);
    }

    public ReportPDF save(ReportPDF reportPDF) {
        return reportPDFRepository.save(reportPDF);
    }

    // New method to get all reports by contract ID
    public List<ReportPdfDTO> getReportsByContractId(Long contractId) {
        List<ReportPDF> reports = reportPDFRepository.findByContractId(contractId);
        return reports.stream().map(report -> {
            ReportPdfDTO dto = new ReportPdfDTO();
            dto.setId(report.getId());
            dto.setName(report.getFileName().split("_")[1]);
//            dto.setContractId(report.getContract().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    public void delete(Long id) {
        reportPDFRepository.deleteById(id);
    }


}
