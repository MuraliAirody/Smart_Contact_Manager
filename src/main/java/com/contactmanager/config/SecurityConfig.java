package com.contactmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	public UserDetailServiceImpl detailServiceImpl() {
		return new UserDetailServiceImpl();
	}
	
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(this.detailServiceImpl());
		auth.setPasswordEncoder(passwordEncoder());
		
		return auth;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
	    return	authenticationConfiguration.getAuthenticationManager();
	}

	
	@Bean
	public SecurityFilterChain chain(HttpSecurity httpSecurity) throws Exception {
		  httpSecurity.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN").
		antMatchers("/user/**").hasRole("USER").
		antMatchers("/**").permitAll().and().
		formLogin().
		loginPage("/signin").
		loginProcessingUrl("/post-signin").
		defaultSuccessUrl("/user/index").
		and().csrf().disable(); 
		  
		  httpSecurity.authenticationProvider(authenticationProvider());
		  

		  
		  return httpSecurity.build();
	}
	
}
