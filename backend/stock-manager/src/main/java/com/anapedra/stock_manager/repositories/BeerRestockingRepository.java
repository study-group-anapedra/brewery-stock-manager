package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.entities.BeerRestocking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerRestockingRepository extends JpaRepository<BeerRestocking, Long> {

}