package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade Cargo/Permissão (Role).
 *
 * <p>Esta interface estende {@link JpaRepository}, fornecendo métodos CRUD básicos
 * para a entidade {@link Role}, que representa os níveis de acesso ou autoridade
 * dentro do sistema (ex: ROLE_ADMIN, ROLE_CLIENT).</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}