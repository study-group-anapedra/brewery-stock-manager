package com.anapedra.stock_manager.services;

import com.anapedra.stock_manager.domain.dtos.UserDTO;
import com.anapedra.stock_manager.domain.entities.Role;
import com.anapedra.stock_manager.domain.entities.User;
import com.anapedra.stock_manager.projections.UserDetailsProjection;
import com.anapedra.stock_manager.repositories.UserRepository;
import com.anapedra.stock_manager.services.exceptions.ForbiddenException;
import com.anapedra.stock_manager.util.CustomUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serviço de gerenciamento de Usuários (User), responsável por operações de negócio
 * e integrações com o Spring Security.
 *
 * <p>Implementa {@link UserDetailsService} para carregar dados do usuário durante
 * o processo de autenticação e fornece métodos utilitários para recuperar o
 * usuário atualmente autenticado.</p>
 *
 * @author Ana Santana
 * @version 1.0
 * @see UserRepository
 * @see UserDetailsProjection
 * @since 0.0.1-SNAPSHOT
 */
@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository repository;
    
    @Autowired
    private CustomUserUtil customUserUtil;
    

    /**
     * Método do Spring Security, utilizado para carregar os detalhes do usuário
     * (credenciais e permissões) com base no nome de usuário (e-mail).
     *
     * <p>Busca o usuário e seus {@link Role}s usando uma Projeção nativa.</p>
     *
     * @param username O e-mail do usuário.
     * @return Um objeto {@link UserDetails} (que é a entidade {@link User}) pronto para o Spring Security.
     * @throws UsernameNotFoundException Se o e-mail não for encontrado no banco de dados.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       
       logger.info("SERVICE: Tentativa de autenticação para o email: {}", username);

       List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
       
       if (result.size() == 0) {
          logger.error("SERVICE ERROR: Email não encontrado durante a autenticação: {}", username);
          throw new UsernameNotFoundException("Email not found");
       }
       
       // Mapeamento manual da projeção para a entidade User, incluindo as Roles
       User user = new User();
       user.setEmail(result.get(0).getUsername());
       user.setPassword(result.get(0).getPassword());
       for (UserDetailsProjection projection : result) {
          user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
       }

       logger.info("SERVICE: Usuário {} autenticado com sucesso. Roles: {}", username, user.getAuthorities());
       
       return user;
    }

    /**
     * Obtém a entidade {@link User} completa do usuário atualmente autenticado no contexto de segurança.
     *
     * <p>Usa o {@link CustomUserUtil} para obter o username logado e depois busca a entidade
     * {@link User} no banco de dados.</p>
     *
     * @return A entidade {@link User} logada.
     * @throws ForbiddenException Se o usuário não estiver autenticado ou se, por algum motivo,
     * o username logado não for encontrado no banco (inconsistência de segurança).
     */
    public User authenticated() {
       try {
          String username = customUserUtil.getLoggedUsername();
          logger.debug("SERVICE DEBUG: Buscando usuário logado no banco: {}", username);
          
          return repository.findByEmail(username)
                .orElseThrow(() -> {
                   logger.error("SERVICE ERROR: Usuário logado não encontrado no banco: {}", username);
                   return new ForbiddenException("User not found: " + username);
                });
       } catch (Exception e) {
          logger.error("SERVICE ERROR: Falha na validação de autenticação: {}", e.getMessage());
          // Captura exceções como NullPointerException se não houver contexto de segurança
          throw new ForbiddenException("Invalid user authentication");
       }
    }


    /**
     * Retorna os dados do usuário autenticado como um {@link UserDTO}.
     *
     * @return O {@link UserDTO} do usuário logado.
     * @throws ForbiddenException Se o usuário não estiver autenticado.
     */
    @Transactional(readOnly = true)
    public UserDTO getMe() {
       logger.info("SERVICE: Iniciando busca de dados do usuário autenticado (getMe).");
       User entity = authenticated();
       logger.info("SERVICE: Dados do usuário ID {} obtidos com sucesso.", entity.getId());
       return new UserDTO(entity);
    }
}