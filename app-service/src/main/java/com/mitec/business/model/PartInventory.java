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

import com.mitec.business.utils.PartInventoryTypeEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
public class PartInventory {

	@Id
	@Getter @Setter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Getter @Setter
	@Column(name = "part_id", nullable = false)
	private Long partId;
	
	@Getter @Setter
	@Column(name = "name", nullable = false)
	private String name;
	
	@Getter @Setter
	@Column(name = "type")
	private Integer type;

	@Getter @Setter
	@Column(name = "quantity", nullable = false)
	private Long quantity;
	
	@Getter @Setter
	@ManyToOne()
	@JoinColumn(name = "inventory_id")
	private Inventory inventory;
	
	@Setter
	@Transient
	private String typeStr;
	
	public String getTypeStr() {
		return PartInventoryTypeEnum.fromKey(type).getValue();
	}
	
	@PrePersist
	public void prePersist() {
		if (quantity == null) {
			quantity = 0L;
		}
	}

}
