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

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private CustomUserUtil customUserUtil;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
		if (result.size() == 0) {
			throw new UsernameNotFoundException("Email not found");
		}
		
		User user = new User();
		user.setEmail(result.get(0).getUsername());
		user.setPassword(result.get(0).getPassword());
		for (UserDetailsProjection projection : result) {
			user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
		}
		
		return user;
	}
	public User authenticated() {
		try {
			String username = customUserUtil.getLoggedUsername();
			return repository.findByEmail(username)
					.orElseThrow(() -> new ForbiddenException("User not found: " + username));
		} catch (Exception e) {
			throw new ForbiddenException("Invalid user authentication");
		}
	}



	@Transactional(readOnly = true)
	public UserDTO getMe() {
		User entity = authenticated();
		return new UserDTO(entity);
	}
}
