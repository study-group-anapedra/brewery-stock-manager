package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
