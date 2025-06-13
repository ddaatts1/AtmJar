package com.mitec.business.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mitec.business.utils.PartInventoryTypeEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
public class TrackingDetail {
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "tracking_id")
	@JsonBackReference
	private Tracking tracking;
	
	@Getter
	@Setter
	@Column(name = "part_id")
	private Long partId;
	
	@Getter
	@Setter
	@Column(name = "part_name")
	private String partName;
	
	@Getter
	@Setter
	@Column(name = "type")
	private Integer type;
	
	@Getter
	@Setter
	@Column(name = "quantity")
	private Long quantity;
	
	@Getter
	@Setter
	@Column(name = "part_inventory_id")
	private Long partInventoryId;
	
	@Setter
	@Transient
	private String typeStr;

	@PrePersist
	public void prePersist() {
		if (quantity == null) {
			quantity = 0L;
		}
	}

	public String getTypeStr() {
		return PartInventoryTypeEnum.fromKey(type).getValue();
	}
}
