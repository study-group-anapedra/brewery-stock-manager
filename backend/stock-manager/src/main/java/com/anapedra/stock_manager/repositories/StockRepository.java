package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
     List<Stock> findByQuantityLessThan(Integer quantity);

}