package com.mitec.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.History;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long>{

}
