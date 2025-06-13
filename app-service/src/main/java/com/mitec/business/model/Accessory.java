package com.mitec.business.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Setter
@Entity
@Table(name = "accessory")
public class Accessory {

	@Getter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Getter
	@Column(name = "name")
	private String name;

	@Getter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "error_device_id")
	private ErrorDevice errorDevice;

	@Getter
	@ManyToMany()
	@JoinTable(name = "accessory_series", 
			joinColumns = @JoinColumn(name = "accessory_id"),
			inverseJoinColumns = @JoinColumn(name = "series_id"))
	private List<Series> series;

	@Getter
	@Transient
	private Long quantity;

	@Getter
	@Transient
	private Long partId;
	
	@Transient
	private String seriesStr;

	public String getSeriesStr() {
		StringBuilder sb = new StringBuilder();
		if (!series.isEmpty()) {
			for (int i = 0; i < series.size(); i++) {
				sb.append(series.get(i).getName());
				if (i < series.size() - 1) {
					sb.append(", ");
				}
			}
		}
		
		return sb.toString();
	}
}