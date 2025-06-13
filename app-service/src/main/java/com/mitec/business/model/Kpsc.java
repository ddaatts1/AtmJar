package com.mitec.business.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

@Data
@Entity
@Table(name = "kpsc")
public class Kpsc {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne()
	@JoinColumn(name = "job_id")
	private Job job;
	
	@ManyToOne()
	@JoinColumn(name = "job_perform_id")
	private JobPerform jobPerform;
	
	@Column(name = "error_desc")
	private String errorDesc;

	@ManyToOne
	@JoinColumn(name = "device_id")
	private Device device;
	
	@OneToMany(mappedBy = "kpsc", cascade = CascadeType.REMOVE)
	private List<ReplacementAccessory> replacementAccessories;
}
