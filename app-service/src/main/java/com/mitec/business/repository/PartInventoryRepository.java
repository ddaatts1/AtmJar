package com.mitec.business.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.dto.PartInventoryDto;
import com.mitec.business.model.PartInventory;

@Repository
public interface PartInventoryRepository extends JpaRepository<PartInventory, Long> {

	@Query(value = "select pi from PartInventory pi where pi.inventory.id = :inventoryId order by pi.quantity desc")
	public List<PartInventory> findByInventoryId(Long inventoryId);
	
	@Query(value = "select new com.mitec.business.dto.PartInventoryDto(pi.id, pi.partId, pi.name, pi.type) from PartInventory pi where pi.inventory.id = :inventoryId and pi.type = :type")
	public List<PartInventoryDto> findByTypeAndInventoryId(Integer type, Long inventoryId);
	
	public Page<PartInventory> findAll(Specification<PartInventory> spec, Pageable pageable);
	
	@Query(value = "select pi.quantity from PartInventory pi where pi.id = :partId")
	public Long getQuantityByPartId(Long partId);
	
	@Query(value = "select new com.mitec.business.dto.PartInventoryDto(pi.id, pi.partId, pi.name, pi.type) from PartInventory pi where pi.inventory.id = :inventoryId and pi.type = :type and pi.quantity > 0")
	public List<PartInventoryDto> findByTypeAndQuantityGtZero(Integer type, Long inventoryId);
	
	@Query(value = "select * from part_inventory pi where pi.inventory_id = :inventoryId and pi.type = :partType and pi.name = :partName limit 1", nativeQuery = true)
	public PartInventory getPartInventory(Long inventoryId, String partName, Integer partType);
	
	@Modifying
	@Query(value = "update PartInventory pi SET pi.quantity = pi.quantity + :newQuantity "
			+ "where pi.inventory.id = :inventoryId and pi.partId = :partId and pi.type = :partType")
	public void updateQuantity(Long inventoryId, Long partId, Integer partType, Long newQuantity);
	
	@Query(value = "select pi.* from part_inventory pi where pi.inventory_id = :inventoryId "
			+ "and pi.part_id = :partId and pi.type = :type limit 1", nativeQuery = true)
	public Optional<PartInventory> getByPart(Long inventoryId, Long partId, Integer type);
}
