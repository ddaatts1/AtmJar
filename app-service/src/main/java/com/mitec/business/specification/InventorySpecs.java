package com.mitec.business.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.mitec.business.model.Inventory;
import com.mitec.business.model.PartInventory;
import com.mitec.business.model.Tracking;

public class InventorySpecs {

	private InventorySpecs() {}
	
	public static Specification<Inventory> searchInventories(final String name) {
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			
			if (StringUtils.isNotBlank(name)) {
				predicates.add(cb.like(root.get("name"), "%" + name + "%"));
			}
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	
	public static Specification<PartInventory> searchPart(final Long id, final String type, final String name) {
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(root.get("inventory").get("id"), id));
			if (StringUtils.isNotBlank(type)) {
				predicates.add(cb.equal(root.get("type"), Integer.valueOf(type)));
			}
			if (StringUtils.isNotBlank(name)) {
				predicates.add(cb.like(root.get("name"), "%" + name + "%"));
			}
			
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
	
	public static Specification<Tracking> searchHistory(final Long inventoryId, 
			final String name, final String status, final String type) {
		return (root, cq, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.or(cb.equal(root.get("sendedInventory"), inventoryId), cb.equal(root.get("receivedInventory"), inventoryId)));
			
			if (StringUtils.isNotBlank(name)) {
				predicates.add(cb.or(cb.like(root.get("sender"), name), cb.like(root.get("receiver"), name)));
			}
			if (StringUtils.isNotBlank(status)) {
				try {
					predicates.add(cb.equal(root.get("status"), status));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (StringUtils.isNotBlank(type)) {
				try {
					predicates.add(cb.equal(root.get("type"), type));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			cq.orderBy(cb.desc(root.get("id")));
			return cb.and(predicates.stream().toArray(Predicate[]::new));
		};
	}
}
