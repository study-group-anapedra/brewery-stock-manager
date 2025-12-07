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

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository repository;
    
    @Autowired
    private CustomUserUtil customUserUtil;
    

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       
       logger.info("SERVICE: Tentativa de autenticação para o email: {}", username);

       List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
       
       if (result.size() == 0) {
          logger.error("SERVICE ERROR: Email não encontrado durante a autenticação: {}", username);
          throw new UsernameNotFoundException("Email not found");
       }
       
       User user = new User();
       user.setEmail(result.get(0).getUsername());
       user.setPassword(result.get(0).getPassword());
       for (UserDetailsProjection projection : result) {
          user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
       }

       logger.info("SERVICE: Usuário {} autenticado com sucesso. Roles: {}", username, user.getAuthorities());
       
       return user;
    }


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
          throw new ForbiddenException("Invalid user authentication");
       }
    }



    @Transactional(readOnly = true)
    public UserDTO getMe() {
       logger.info("SERVICE: Iniciando busca de dados do usuário autenticado (getMe).");
       User entity = authenticated();
       logger.info("SERVICE: Dados do usuário ID {} obtidos com sucesso.", entity.getId());
       return new UserDTO(entity);
    }
}