package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}