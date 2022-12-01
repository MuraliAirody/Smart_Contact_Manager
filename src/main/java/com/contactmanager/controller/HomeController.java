package com.contactmanager.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contactmanager.dao.Repository;
import com.contactmanager.entities.User;
import com.contactmanager.helper.Message;

@Controller
public class HomeController {

	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private Repository repository;
	
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about() {
	    return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model){
		model.addAttribute("user", new User());
	    return "signup";
	}
	
	@PostMapping("/do-register")
	public String signupData(@Valid @ModelAttribute("user") User user,
			BindingResult result,
			@RequestParam(value = "agreement",defaultValue = "false")boolean agreement,
			Model model,HttpSession session) {
		
     try {
 		if(!agreement) {
			System.out.println("agreement not checked");
			throw new Exception("Terms and condition not agreed");
		}
 		if(result.hasErrors())
 		{
 			model.addAttribute("user",user);
 			return "signup";
 		}
		user.setEnabled(true);
		user.setRole("ROLE_USER");
		user.setImageUrl("default.png");
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		
		User u = repository.save(user);
		model.addAttribute("user",new User());
   	    session.setAttribute("message", new Message("Registered Successfuly", "alert-success"));

		System.out.println(agreement);
		System.out.println(u);
		
		 return "signup";

     }
     catch (Exception e) {
//    	 e.printStackTrace();
    	 model.addAttribute("user",user);
    	 session.setAttribute("message", new Message("something went wrong "+e.getMessage(), "alert-danger"));
    	 
		 return "signup";
	}
		
	}
	
	@GetMapping("/signin")
	public String logIn(Model model) {
	
		return "login";
	}
}
