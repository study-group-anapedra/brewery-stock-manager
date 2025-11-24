package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;


/**

 * Interface Repository para a entidade Category.

 * Estendendo JpaRepository, herda métodos CRUD básicos (save, findById, findAll, delete, etc.).

 * Parâmetros: <Entidade, Tipo do ID>

 */

@Repository

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);



}