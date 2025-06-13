package com.mitec.business.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "period")
public class Period {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "start_time")
	private LocalDateTime startTime;
	
	@Column(name = "end_time")
	private LocalDateTime endTime;
	
	@Column(name = "period_index")
	private Integer index;
	
	@ManyToOne()
	@JoinColumn(name = "contract_id")
	private Contract contract;

	@Override
	public String toString() {
		return "Period [id=" + id + ", startTime=" + startTime + ", endTime=" + endTime + ", index=" + index
				+ ", contract=" + contract + "]";
	}
	
}
