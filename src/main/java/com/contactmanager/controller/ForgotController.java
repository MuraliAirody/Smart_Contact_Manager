package com.contactmanager.controller;

import com.contactmanager.helper.Message;
import com.contactmanager.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/forgot")
    public String forgot(Model model){
        model.addAttribute("title","Forgot Password");

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
        String message ="This is your otp "+otp;
        String subject="OTP for password change";
        String  from ="techtestingindia101@gmail.com";

        boolean b = this.service.sendMail(message, subject, email, from);

        if(b){
            session.setAttribute("message",new Message("OTP send to registered Email","alert-success"));
            return "ForgotPassword/verify";
        }
        else {
            session.setAttribute("message",new Message("Check your Email","alert-danger"));
            return "ForgotPassword/forgot";
        }

    }
}
