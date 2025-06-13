package com.mitec.business.repository;
import com.mitec.business.model.Report;
import com.mitec.business.model.ReportPDF;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportPDFRepository extends JpaRepository<ReportPDF, Long> {
    List<ReportPDF> findByContractId(Long contractId);

}