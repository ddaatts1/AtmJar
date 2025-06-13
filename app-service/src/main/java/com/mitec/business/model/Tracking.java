package com.mitec.business.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.mitec.business.utils.CustomFormatDate;
import com.mitec.business.utils.TrackingStatusEnum;
import com.mitec.business.utils.TrackingTypeEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
public class Tracking {

	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Getter
	@Setter
	@Column(name = "sended_inventory")
	private Long sendedInventory;
	
	@Getter
	@Setter
	@Column(name = "received_inventory")
	private Long receivedInventory;
	
	@Getter
	@Setter
	@Column(name = "sender")
	private String sender;
	
	@Getter
	@Setter
	@Column(name = "receiver")
	private String receiver;

	@Getter
	@Setter
	@Column(name = "status")
	private Integer status;
	
	@Getter
	@Setter
	@Column(name = "send_date")
	private LocalDateTime sendDate;
	
	@Getter
	@Setter
	@Column(name = "received_date")
	private LocalDateTime receivedDate;
	
	@Getter
	@Setter
	@Column(name = "type")
	private Integer type;
	
	@Getter
	@Setter
	@Column(name = "received_phone")
	private String receivedPhone;
	
	@Getter
	@Setter
	@Column(name = "received_department")
	private String receivedDepartment;
	
	@Getter
	@Setter
	@Column(name = "received_address")
	private String receivedAddress;
	
	@Getter
	@Setter
	@Column(name = "note")
	private String note;
	
	@Getter
	@Setter
	@Column(name = "trading_code")
	private String tradingCode;
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "tracking")
	private List<TrackingDetail> trackingDetails;
	
	@Setter
	@Transient
	private Long totalQuantity;
	
	@Setter
	@Transient
	private String statusStr;
	
	@Setter
	@Transient
	private String typeStr;
	
	@Setter
	@Transient
	private String sendDateStr;

	@Setter
	@Transient
	private String receivedDateStr;

	public Long getTotalQuantity() {
		Long quantity = 0L;
		if (trackingDetails != null && !trackingDetails.isEmpty()) {
			for (TrackingDetail item : trackingDetails) {
				quantity += item.getQuantity();
			}
		}
		return quantity;
	}

	public String getStatusStr() {
		return TrackingStatusEnum.fromKey(status).getValue();
	}

	public String getTypeStr() {
		return TrackingTypeEnum.fromKey(type).getValue();
	}

	public String getSendDateStr() {
		if (sendDate != null) {
			return CustomFormatDate.formatLocalDate(sendDate, "dd/MM/yyyy");
		}
		return null;
	}

	public String getReceivedDateStr() {
		if (receivedDate != null) {
			return CustomFormatDate.formatLocalDate(receivedDate, "dd/MM/yyyy");
		}
		return null;
	}
}
