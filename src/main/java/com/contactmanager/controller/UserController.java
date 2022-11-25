package com.contactmanager.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contactmanager.dao.Repository;
import com.contactmanager.entities.Contact;
import com.contactmanager.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private Repository repository;
	
	@ModelAttribute
	public void userNamePasser(Model model,Principal principal) {
		String name = principal.getName();
//		System.out.println(name);
		User user= repository.getUserByName(name);
		model.addAttribute("user",user);
	}
	@RequestMapping("/index")
	public String dashboard(Model model) {
 
		model.addAttribute("title","Dash Board");
		return "normal/user_dashboard";
	}

	@GetMapping("/add-contact")
	public String addcontact(Model modell){
		
		modell.addAttribute("title","Add Contact");
		modell.addAttribute("contact",new Contact());
		return "normal/add-contact";
	}
	
	@PostMapping("/process-addcontact")
	public String processcontact(@ModelAttribute Contact contact,Principal principal){
		
		User user = repository.getUserByName(principal.getName());
	    user.getContacts().add(contact);
	    contact.setUser(user);
	    repository.save(user);
	    
		System.out.println(contact);

		return "normal/add-contact";
	}
}
