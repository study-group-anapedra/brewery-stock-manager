package com.anapedra.stock_manager.repositories;


import com.anapedra.stock_manager.domain.entities.Beer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeerRepository extends JpaRepository<Beer, Long> {
}
