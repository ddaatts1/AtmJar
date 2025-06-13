package com.mitec.business.repository;

import com.mitec.business.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByContractId(Long contractId);

}