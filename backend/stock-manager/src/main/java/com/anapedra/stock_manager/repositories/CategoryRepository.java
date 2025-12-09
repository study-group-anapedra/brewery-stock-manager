package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade Categoria (Category).
 *
 * <p>Esta interface estende {@link JpaRepository}, fornecendo métodos CRUD básicos
 * para a entidade {@link Category}, como salvar, buscar por ID, listar todos e deletar.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Métodos específicos do JpaRepository (herdados):
    // save(), findById(), findAll(), delete(), etc.
}