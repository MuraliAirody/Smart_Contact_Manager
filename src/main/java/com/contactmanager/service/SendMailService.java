package com.contactmanager.service;

import org.springframework.stereotype.Service;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


@Service
public class SendMailService {

    public boolean sendMail(String message, String subject, String to,String from) {

        boolean f = false;
//        variable for mail
        String host = "smtp.gmail.com";

//        get the system properties
        Properties properties = System.getProperties();
//        System.out.println(properties);

//        set some properties

        properties.put("mail.smtp.host",host);
        properties.put("mail.smtp.port","465");
        properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.auth","true");

//     step 1 :   to get a session object
        Session instance = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("techtestingindia101@gmail.com", "ojltjrjjyjnligxb");
            }
        });

        instance.setDebug(true);

//      step2: compose the message {text,multimedia}

        MimeMessage mimeMessage = new MimeMessage(instance);

        try{
            //from email
            mimeMessage.setFrom(new InternetAddress(from));

            //add recipient email
            mimeMessage.addRecipient(Message.RecipientType.TO,new InternetAddress(to));

            //add subject
            mimeMessage.setSubject(subject);

            //add text
//            mimeMessage.setText(message);
            mimeMessage.setContent(message,"text/html");

            //send mail
            Transport.send(mimeMessage);

            System.out.println("message sent successfully");
            f = true;
            return f;
        }
        catch (Exception e){
            e.printStackTrace();
            return f;
        }

    }
}
