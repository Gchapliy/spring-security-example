package com.example.springsecurityexample.web.controller;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.example.springsecurityexample.persistence.VerificationTokenRepository;
import com.example.springsecurityexample.web.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.springsecurityexample.service.IUserService;
import com.example.springsecurityexample.validation.EmailExistsException;
import com.example.springsecurityexample.web.model.User;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Properties;
import java.util.UUID;

@Controller
class RegistrationController {

    @Autowired
    private IUserService userService;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @RequestMapping(value = "signup")
    public ModelAndView registrationForm() {
        return new ModelAndView("registrationPage", "user", new User());
    }

    @RequestMapping(value = "user/register")
    public ModelAndView registerUser(@Valid final User user, final BindingResult result, final HttpServletRequest request) {
        if (result.hasErrors()) {
            return new ModelAndView("registrationPage", "user", user);
        }
        try {
            user.setEnabled(false);
            userService.registerNewUser(user);

            final String token = UUID.randomUUID().toString();
            final VerificationToken myToken = new VerificationToken(token, user);
            verificationTokenRepository.save(myToken);

            final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            sendVerificationEmail(user, token, appUrl);
        } catch (EmailExistsException e) {
            result.addError(new FieldError("user", "email", e.getMessage()));
            return new ModelAndView("registrationPage", "user", user);
        }
        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(value = "/registrationConfirm")
    public ModelAndView confirmRegistration(final Model model, @RequestParam("token") final String token, final RedirectAttributes redirectAttributes){
        final VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        final User user = verificationToken.getUser();

        user.setEnabled(true);
        userService.saveRegisteredUser(user);

        redirectAttributes.addFlashAttribute("message", "Your account verified successfully");
        return new ModelAndView("redirect:/login");
    }

    private void sendVerificationEmail(User user, String token, String appUrl) {

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.mailtrap.io");
        prop.put("mail.smtp.port", 25);
        prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("87ba3d9555fae8", "91cb4379af43ed");
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("to@gmail.com"));
            message.setSubject("Mail Subject");

            String msg = appUrl;

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
