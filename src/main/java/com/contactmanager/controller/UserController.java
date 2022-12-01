package com.contactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contactmanager.dao.ContactRepository;
import com.contactmanager.dao.Repository;
import com.contactmanager.entities.Contact;
import com.contactmanager.entities.User;
import com.contactmanager.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private Repository repository;
	
	@Autowired
	private ContactRepository contactRepository;
	
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
	@GetMapping("/process-addcontact")
	public String  getprocesscontact(Model modell){
		
		return "normal/add-contact";
	}
	
	@PostMapping("/process-addcontact")
	public String processcontact(@Valid @ModelAttribute Contact contact,BindingResult bindingResult,Principal principal,
			@RequestParam("profile-image") MultipartFile file,
			HttpSession session,Model model){
		
		try {
        			User user = repository.getUserByName(principal.getName());
        			
        			if(bindingResult.hasErrors())
        			{
        				return "normal/add-contact";
        			}
        			
        			//processing and uploading the file
        			if(file.isEmpty()) {
        				
        			}else {
        				//upload the file to folder and update the name to contact
        				
        				contact.setImage(file.getOriginalFilename());
        				
        				File saveFile = new ClassPathResource("static/images").getFile();
        				Path pathtoSave = Paths.get(saveFile.getAbsolutePath()+ File.separator+file.getOriginalFilename());
        						
        				Files.copy(file.getInputStream(), pathtoSave, StandardCopyOption.REPLACE_EXISTING);
        				
        			}
        			 		
        			
		    user.getContacts().add(contact);
		    contact.setUser(user);
		    repository.save(user);
		    
		    //send the success message using session
		    session.setAttribute("message", new Message("Contact Successfuly added", "alert-success"));
		    
			System.out.println(contact);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		    session.setAttribute("message", new Message("Something went wrong", "alert-danger"));

		}


		return "normal/add-contact";
	}
	
	@GetMapping("/show_contacts/{page}")
	public String showContacts(@PathVariable("page") Integer currentPage,Model model,Principal principal) {

		String name = principal.getName();
		User user = this.repository.getUserByName(name);
		
	Pageable pageable  =  PageRequest.of(currentPage, 1);
	Page<Contact> contacts = this.contactRepository.getContactsByUserId(user.getId(),pageable);
	System.out.println(contacts);
	
	model.addAttribute("titile","View Contacts");
	model.addAttribute("currentPage", currentPage);
	model.addAttribute("totalPage",contacts.getTotalPages());
	model.addAttribute("contacts", contacts);
	
	return "normal/show_contacts";
	}
	
}
