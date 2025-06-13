package com.mitec.business.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "inventory")
public class Inventory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "region_id")
	private Long regionId;
	
	@Column(name = "region_name")
	private String regionName;
	
	@Column(name = "address")
	private String address;

	@OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
	private List<PartInventory> partInventories;
	
	@ManyToMany
	@JoinTable(name = "user_inventory",
		joinColumns = @JoinColumn(name = "inventory_id"),
		inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	private List<User> users;

	@Override
	public String toString() {
		return "Inventory [id=" + id + ", name=" + name + ", regionId=" + regionId + ", regionName=" + regionName
				+ ", address=" + address + "]";
	}
	
}
