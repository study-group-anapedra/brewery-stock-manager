package com.anapedra.stock_manager.repositories;

import com.anapedra.stock_manager.domain.entities.User;
import com.anapedra.stock_manager.projections.UserDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade Usuário (User).
 *
 * <p>Fornece métodos para operações CRUD e consultas customizadas relacionadas
 * aos usuários e suas permissões, essenciais para o sistema de autenticação
 * e segurança (Spring Security).</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @see UserDetailsProjection
 * @since 0.0.1-SNAPSHOT
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca dados de segurança (username, password e roles) de um usuário
     * através do seu e-mail, utilizando uma consulta SQL nativa.
     *
     * <p>O resultado é mapeado para a Projeção {@link UserDetailsProjection},
     * otimizando a consulta para o processo de autenticação do Spring Security.</p>
     *
     * @param email O e-mail (username) do usuário.
     * @return Uma lista de {@link UserDetailsProjection} contendo as credenciais
     * e as autoridades (roles) do usuário.
     */
    @Query(nativeQuery = true, value = """
             SELECT tb_user.email AS username, tb_user.password, tb_role.id AS roleId, tb_role.authority
             FROM tb_user
             INNER JOIN tb_user_role ON tb_user.id = tb_user_role.user_id
             INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id
             WHERE tb_user.email = :email
          """)
    List<UserDetailsProjection> searchUserAndRolesByEmail(String email);

    /**
     * Busca um usuário pelo seu e-mail (username).
     *
     * <p>Este é um método de consulta derivado do nome, muito comum para
     * login ou verificação de unicidade de e-mail.</p>
     *
     * @param email O e-mail (username) do usuário.
     * @return Um {@link Optional} contendo a entidade {@link User} se encontrada, ou vazio.
     */
    Optional<User> findByEmail(String email);
}