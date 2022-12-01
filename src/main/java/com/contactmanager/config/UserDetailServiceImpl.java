package com.contactmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.contactmanager.dao.UserRepository;
import com.contactmanager.entities.User;


public class UserDetailServiceImpl implements UserDetailsService{

	@Autowired
	private UserRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User userByName = repository.getUserByName(username);
//		System.out.println(userByName);
		
		if(userByName==null) {
			throw new UsernameNotFoundException("User could not found");
		}
		
		UserDetailsImpl detailsImpl = new UserDetailsImpl(userByName);
		return detailsImpl;
	}

}
