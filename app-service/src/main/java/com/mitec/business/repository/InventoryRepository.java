package com.mitec.business.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mitec.business.model.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {
	
	@Query(value = "select i from Inventory i join i.users u where u.username = :username")
	public List<Inventory> findByUsername(String username);
	
	public Page<Inventory> findAll(Specification<Inventory> spec, Pageable pageable);
	
	public List<Inventory> findByIdNot(Long id);
	
	@Query(value = "select case when count(*) > 0 then 'true' else 'false' end \r\n"
			+ "from inventory i \r\n"
			+ "join user_inventory ui \r\n"
			+ "on i.id = ui.inventory_id \r\n"
			+ "join user u \r\n"
			+ "on u.id = ui.user_id \r\n"
			+ "where u.username = :username \r\n"
			+ "and i.id = :inventoryId", nativeQuery = true)
	public boolean isExistInventory(String username, Long inventoryId);
	
	@Query(value = "select i.name from Inventory i where i.id = :id")
	public String getNameById(Long id);
}
