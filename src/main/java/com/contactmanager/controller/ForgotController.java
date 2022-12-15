package com.contactmanager.controller;

import com.contactmanager.dao.Repository;
import com.contactmanager.entities.User;
import com.contactmanager.helper.Message;
import com.contactmanager.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Random;

@Controller
public class ForgotController {

    @Autowired
    private SendMailService service;

    @Autowired
    private Repository repository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @GetMapping("/forgot")
    public String forgot(Model model){
        model.addAttribute("title","Forgot Password");

        return "ForgotPassword/forgot";
    }

    @GetMapping("/send-otp")
    public String acceptOTPGet(Model model){
        model.addAttribute("title","Send OTP");

        return "ForgotPassword/forgot";
    }
    @PostMapping("/send-otp")
    public String acceptOTP(Model model, @RequestParam("email") String email, HttpSession session){
        model.addAttribute("title","Send OTP");

        System.out.println(email);

//        generating the otp
        Random rand = new Random();
        String otp = String.format("%04d%n", rand.nextInt(10000));
        System.out.printf("%04d%n", rand.nextInt(10000));

//        Sending required data to email service
        String message ="<div>"
                +"<h1>"
                +"This Your OTP"
                +"<b>"
                +"<div style='background:yellow'>"
                +otp
                +"</div>"
                +"</h1>"
                +"</div>";
        String subject="OTP for password change";
        String  from ="techtestingindia101@gmail.com";

        boolean b = this.service.sendMail(message, subject, email, from);

        if(b){
            session.setAttribute("myOtp",otp);
            session.setAttribute("userMail",email);
            session.setAttribute("message",new Message("OTP send to registered Email","alert-success"));
            return "ForgotPassword/verify";
        }
        else {
            session.setAttribute("message",new Message("Check your Email","alert-danger"));
            return "ForgotPassword/forgot";
        }

    }

    //process the otp
    @PostMapping("/verify_otp")
    public String verifyOtp(HttpSession session,@RequestParam("otp") int otp,Model model){

        System.out.println(otp);
        System.out.println(session.getAttribute("myOtp"));

        model.addAttribute("title","Change password");

        String email = (String) session.getAttribute("userMail");
        String motp = (String) session.getAttribute("myOtp");

        //there is number formate exception bcz of space here so I removed space in here
        motp= motp.replaceAll("\\s", "");

        try{
            int myOtp = Integer.parseInt(motp) ;
//            System.out.println(myOtp==otp);
            if(otp == myOtp)
            {
                //after verifying the otp, check the user exist in the DB or not
                User user = this.repository.getUserByName(email);
                if(user==null){
                    //if null send back forgot page with error message
                    session.setAttribute("message",new Message("User doesn't exist...please check your mailID","alert-danger"));
                    return "ForgotPassword/forgot";
                }
                else {
                    //send change password form
                    return "ForgotPassword/password_change_form";
                }
            }
            else {
                //send a error if otp is not verified
                session.setAttribute("message",new Message("You have entered wrong OTP","alert-danger"));
                return "ForgotPassword/verify";
            }
        }
        catch (Exception e)
        {
           e.printStackTrace();
        }
        return "ForgotPassword/verify";
    }

    @PostMapping("/change-password")
    public  String changePassword(HttpSession session,@RequestParam("newPassword") String newPassword,
                                  @RequestParam("reEnterPassword") String reEnterPassword){

        if(newPassword.equals(reEnterPassword)) {
            String mail = (String) session.getAttribute("userMail");
            User user = this.repository.getUserByName(mail);
            user.setPassword(this.passwordEncoder.encode(newPassword));
            this.repository.save(user);

            return "redirect:/signin?msg=password changed successfully";
        }
        else {
            session.setAttribute("message",new Message("Re entered password not matched...","alert-danger"));
            return "ForgotPassword/password_change_form";
        }
    }

}
