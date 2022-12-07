package com.contactmanager.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.contactmanager.dao.ContactRepository;
import com.contactmanager.dao.Repository;
import com.contactmanager.entities.Contact;
import com.contactmanager.entities.User;

@RestController
public class SearchController {

	@Autowired
	private Repository repository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@GetMapping("/search/{query}")
	public ResponseEntity<Object> search(@PathVariable("query") String name, Principal principal){
	     User user = this.repository.getUserByName(principal.getName());
	     
	     List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(name,user);
	     
	     return ResponseEntity.ok(contacts);
	}
}
