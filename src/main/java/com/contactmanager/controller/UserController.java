package com.contactmanager.controller;

import com.contactmanager.dao.ContactRepository;
import com.contactmanager.dao.Repository;
import com.contactmanager.entities.Contact;
import com.contactmanager.entities.User;
import com.contactmanager.helper.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
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
	
	// starting index page
	
	@RequestMapping("/index")
	public String dashboard(Model model) {
 
		model.addAttribute("title","Dash Board");
		return "normal/user_dashboard";
	}

	// add contact page
	
	@GetMapping("/add-contact")
	public String addcontact(Model model){

		
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		return "normal/add-contact";
	}
	
	//fetching add contact details through get method
	
	@GetMapping("/process-addcontact")
	public String  getprocesscontact(Model modell){
		modell.addAttribute("contact",new Contact());
		return "normal/add-contact";
	}
	
	//fetching add contact details through post method
	
	@PostMapping("/process-addcontact")
	public String processcontact(@Valid @ModelAttribute Contact contact,BindingResult bindingResult,Principal principal,
			@RequestParam("profile-image") MultipartFile file,
			HttpSession session,Model model){
		
		try {
        			User user = repository.getUserByName(principal.getName());
			
        			if(bindingResult.hasErrors())
        			{
        				model.addAttribute("contact",contact);
        				return "normal/add-contact";
        			}
        			      
        		    //set the contact work to empty if its empty
//        		    System.out.println("work"+contact.getWork());
        		    
        		    if(contact.getWork().isEmpty())
        		    {
        		    	System.out.println("work"+contact.getWork());
        		    	contact.setWork("(Empty)");
        		    }
        		    
        			
		    user.getContacts().add(contact);
		    contact.setUser(user);
		    
		    //save the contact entity bcz to get contact id to save the image uniquely (otherwise no need to save contact , we can directly save user)
		    this.contactRepository.save(contact);
		    
        			//processing and uploading the file
        			if(file.isEmpty()) {
        				contact.setImage("default.png");
        				}else {
        					
        				//upload the file to folder and update the name to contact
        				
        					//uniquely save the each image with their contact id
        					
        				contact.setImage(contact.getCid()+file.getOriginalFilename());
        				
        				File saveFile = new ClassPathResource("static/images").getFile();
        				Path pathtoSave = Paths.get(saveFile.getAbsolutePath()+ File.separator+contact.getCid()+file.getOriginalFilename());
        				System.out.println("pathtosave ->"+pathtoSave);
        				Files.copy(file.getInputStream(), pathtoSave, StandardCopyOption.REPLACE_EXISTING);
        				
        			}
        			 		    
		    repository.save(user);
		    
		    //send the success message using session
		    session.setAttribute("message", new Message("Contact Successfuly added", "alert-success"));
		    
//			System.out.println(contact);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		    session.setAttribute("message", new Message("Something went wrong", "alert-danger"));

		}

        model.addAttribute("contact",new Contact());
		return "normal/add-contact";
	}
	
	//fully display the contact details per page
	
	@GetMapping("/show_contacts/{page}")
	public String showContacts(@PathVariable("page") Integer currentPage,Model model,Principal principal) {

						String name = principal.getName();
						User user = this.repository.getUserByName(name);
						
					Pageable pageable  =  PageRequest.of(currentPage, 5);
					Page<Contact> contacts = this.contactRepository.getContactsByUserId(user.getId(),pageable);
					
					System.out.println(contacts.isEmpty());
					System.out.println(currentPage);
					System.out.println(contacts.getTotalPages());
					
					if(contacts.isEmpty())
					{
						model.addAttribute("totalPage",contacts.getTotalPages()+1);
					}
					else {
						model.addAttribute("totalPage",contacts.getTotalPages());
					}
					
					model.addAttribute("title","View Contacts");
					model.addAttribute("currentPage", currentPage);
					model.addAttribute("contacts", contacts);
					
					return "normal/show_contacts";
	}
	
	//onclick single contact display the details of particular contact
	
	@GetMapping("/contact_details/{cid}")
	public String showContactDetails(@PathVariable("cid") Integer cid,Model model,Principal principal) {
		
					Contact contact = this.contactRepository.findById(cid).get();
					
					User user = this.repository.getUserByName(principal.getName());
					
					if(user.getId() == contact.getUser().getId())
					{
						model.addAttribute("title", contact.getName());
			              model.addAttribute("contact", contact);
					}
					
					return "normal/contact_profile";
	}
	
	//delete the contact
	
	@GetMapping("/delete/{cid}")
	public String deleteingTheContact(@PathVariable("cid") Integer cid,Model model,Principal principal,HttpSession session) {
				
				User user = repository.getUserByName(principal.getName());
				Contact contact = this.contactRepository.findById(cid).get();
				
				
				// verifying the user who is deleting the contact correct or not
			try {	
				if(user.getId()==contact.getUser().getId()) {
			//while mapping contact entity is attached to User, so we have to dis attach it first
			//			contact.setUser(null);
			//			 this.contactRepository.delete(contact);
						 this.contactRepository.deleteContactById(cid);
//						 System.out.println("contact "+!"default.png".equals(contact.getImage())+" "+contact.getImage());
						 
						 // to save the default image in the static folder I added check, bcz if I delete default image other contact or profile default image also got deleted
						 if(!"default.png".equals(contact.getImage()))
						 {
							 File deleteFile = new ClassPathResource("static/images").getFile();
	                         File file2 = new File(deleteFile, contact.getImage());
	                         file2.delete();
						 }
                         
						 session.setAttribute("message",new Message("contact deleted successfuly", "alert-success"));
					}
			}
			catch (Exception e) {
                    e.printStackTrace();
			}
		
				return "redirect:/user/show_contacts/0";
	}
	
	//open update form handler
	@RequestMapping(value = "/update-contact/{cid}",method = RequestMethod.POST)
	public String updateContactFrom(@PathVariable("cid") int cid,Model model) {
						Contact contact = this.contactRepository.findById(cid).get();
						model.addAttribute("title", "Update Deatils");
						model.addAttribute("contact", contact);
						return "normal/update-contact";
	}
	
	//processing the updated contact
	@PostMapping("/process-updatecontact")
	public String processingTheUpdatedContact(@ModelAttribute Contact contact,Model model,
			@RequestParam("profile-image") MultipartFile file,HttpSession session,Principal principal) {
		
						System.out.println("contact name ->"+contact.getName());
						System.out.println("contact ID =>"+contact.getCid());
						System.out.println("image "+contact.getImage());
						
						Contact oldContactDetails = this.contactRepository.findById(contact.getCid()).get();

		try {
						//image
						if(!file.isEmpty())
						{
							//work on file
							
							//delete the old image
//							System.out.println(!"default.png".equals(oldContactDetails.getImage()) + " " + oldContactDetails.getImage());
							
							     //check for default image, if have default image as a profile keep it as it is in the folder bcz some contacts or profile using default images
							 if(!"default.png".equals(oldContactDetails.getImage()))
							 {
								 File deleteFile = new ClassPathResource("static/images").getFile();
		                         File file2 = new File(deleteFile, oldContactDetails.getImage());
		                         file2.delete();
							 }
							
							//update the new image
							File updateFile = new ClassPathResource("static/images").getFile();
	        				Path pathtoSave = Paths.get(updateFile.getAbsolutePath()+ File.separator+contact.getCid()+file.getOriginalFilename());
	        				Files.copy(file.getInputStream(), pathtoSave, StandardCopyOption.REPLACE_EXISTING);
	        				
	        				contact.setImage(contact.getCid()+file.getOriginalFilename());
							
						}
						else {
							 contact.setImage(oldContactDetails.getImage());
						}
						User user = this.repository.getUserByName(principal.getName());
						contact.setUser(user);
						this.contactRepository.save(contact);
						
						session.setAttribute("message", new Message("Conatct successfuly updated", "alert-success"));
						
		} catch (Exception e) {
               e.printStackTrace();
               System.out.println(e.getMessage());
		}
		
		return "redirect:/user/contact_details/"+contact.getCid();
	}
	
	
	//profile page url
	@GetMapping("/profile-page")
	public String profilePage(Model model,Principal principal) {
		
		String name = principal.getName();
		User user = this.repository.getUserByName(name);
		model.addAttribute("title","Profile");
		
		model.addAttribute("user",user);
		
		return "normal/profile";
	}

	@RequestMapping(value = "/update-user/{id}",method = RequestMethod.POST)
	public String updateUserProfile(@PathVariable("id") int id,Model model) {
						User user = this.repository.findById(id).get();
						model.addAttribute("title", "Update Profile");
						model.addAttribute("user", user);
						return "normal/update-user";
	}
	
	@PostMapping("/process-updateuser")
	public String process_The_Updated_UserDeatils_(@ModelAttribute User user,
			@RequestParam("profile-image")MultipartFile file,HttpSession session,Model model,Principal principal) {
	    
		System.out.println("user image ->"+user.getImageUrl());
		
		try {
			//image
			if(!file.isEmpty())
			{
							//work on file
							
							//delete the old image
							 if(!"default.png".equals(user.getImageUrl())) {
								 File deleteFile = new ClassPathResource("static/images").getFile();
				                 File file2 = new File(deleteFile, user.getImageUrl());
				                 file2.delete();
								 }
							
							//update the new image
							File updateFile = new ClassPathResource("static/images").getFile();
							Path pathtoSave = Paths.get(updateFile.getAbsolutePath()+ File.separator+user.getId()+"-USER-"+file.getOriginalFilename());
							Files.copy(file.getInputStream(), pathtoSave, StandardCopyOption.REPLACE_EXISTING);
							
							user.setImageUrl(user.getId()+"-USER-"+file.getOriginalFilename());
				
			}
			else {
							user.setImageUrl(user.getImageUrl());
			}
					User user1 = this.repository.getUserByName(principal.getName());
					user.setContacts(user1.getContacts());
					this.repository.save(user);
					
					session.setAttribute("message", new Message("user successfuly updated", "alert-success"));
			
		} catch (Exception e) {
			   e.printStackTrace();
			   System.out.println(e.getMessage());
		}
		
		
		
		return "redirect:/user/profile-page";
	}
	
	@GetMapping("/delete-user/{id}")
	public String deleteUserProfile(@PathVariable("id")int id,Principal principal,HttpSession session) {
		
		User loged_user = this.repository.getUserByName(principal.getName());
	
		// verifying the uloged user id and id passing within the url
	try {	
		if(loged_user.getId()==id) {

				 this.repository.delete(loged_user);
				 
				 if(!"default.png".equals(loged_user.getImageUrl())) {
				 File deleteFile = new ClassPathResource("static/images").getFile();
                 File file2 = new File(deleteFile, loged_user.getImageUrl());
                 file2.delete();
				 }
                 
				 session.setAttribute("message",new Message("Account deleted successfuly... Please register again", "alert-success"));
			}
	}
	catch (Exception e) {
            e.printStackTrace();
	}
           
		return "redirect:/logout";
	}

	@GetMapping("/setting")
	public  String settingpage(Model model){
		model.addAttribute("title","Settings");
		return  "normal/settings";
	}

	@PostMapping("/setting-process")
	public  String settingProcess(@RequestParam("oldPassword") String oldPassword,
								  @RequestParam("newPassword") String newPassword,
								  Principal principal,
								  HttpSession session){
		System.out.println("old "+oldPassword);
		System.out.println("new "+newPassword);

		User user = this.repository.getUserByName(principal.getName());

		System.out.println(passwordEncoder.matches(oldPassword,user.getPassword()));

		if(passwordEncoder.matches(oldPassword,user.getPassword())){
			user.setPassword(passwordEncoder.encode(newPassword));
			this.repository.save(user);
			session.setAttribute("message",new Message("Password Changed successfully","alert-success"));

		}
		else {
			 session.setAttribute("message",new Message("Incorrect old password","alert-danger"));
			 return "redirect:/user/setting";
		}

		return "redirect:/user/index";
	}

}
