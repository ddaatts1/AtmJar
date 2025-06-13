package com.mitec.business.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "replacement_accessory")
public class ReplacementAccessory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "quantity")
	private Integer quantity;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "error_device_id")
	private ErrorDevice errorDevice;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "accessory_id")
	private Accessory accessory;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "kpsc_id")
	private Kpsc kpsc;
}
