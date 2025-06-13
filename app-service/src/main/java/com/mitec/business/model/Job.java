package com.mitec.business.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mitec.business.utils.JobStatusEnum;

import lombok.Data;

@Data
@Entity
@Table(name = "job")
public class Job {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "serial_number")
	private ATM atm;
	
	@Column(name = "check_in_time")
	private LocalDateTime checkInTime;
	
	@Column(name = "check_out_time")
	private LocalDateTime checkOutTime;
	
	@Column(name = "job_reason")
	private String jobReason;
	
	@Column(name = "status")
	private Integer status;
	
	@Column(name = "note")
	private String note;
	
	@Column(name = "job_error")
	private String jobError;
	
	@ManyToOne()
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(name = "complete_time")
	private LocalDateTime completeTime;

	@Column(name = "is_maintenance")
	private boolean isMaintenance;

	@Column(name = "is_kpsc")
	private boolean isKpsc;
	
	@JsonBackReference(value = "job-kpsc")
	@OneToMany(mappedBy = "job", cascade = CascadeType.REMOVE)
	private List<Kpsc> kpscs;
	
	@Column(name = "take_report")
	private boolean takeReport;
	
	@Column(name = "file_path")
	private String filePath;
	
	@Transient
	private String statusDesc;
	
	@PrePersist
	public void prePersist() {
		checkInTime = LocalDateTime.now();
		if (status == null) {
			status = JobStatusEnum.IN_PROCESS.getKey(); // Only set default status if not already set
		}
		if (StringUtils.isBlank(note) || note.trim().equals("null")) {
			note = null;
		}
		if (StringUtils.isBlank(jobError) || jobError.trim().equals("null")) {
			jobError = null;
		}
		if (StringUtils.isBlank(jobReason) || jobReason.trim().equals("null")) {
			jobReason = null;
		}
	}
	
	@PreUpdate
	public void preUpdate() {
//		checkOutTime = LocalDateTime.now();
		
		if (StringUtils.isBlank(note) || note.trim().equals("null")) {
			note = null;
		}
		if (StringUtils.isBlank(jobError) || jobError.trim().equals("null")) {
			jobError = null;
		}
		if (StringUtils.isBlank(jobReason) || jobReason.trim().equals("null")) {
			jobReason = null;
		}
	}
}
