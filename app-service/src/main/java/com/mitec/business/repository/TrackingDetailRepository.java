package com.mitec.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.TrackingDetail;

@Repository
public interface TrackingDetailRepository extends JpaRepository<TrackingDetail, Long>{

}
