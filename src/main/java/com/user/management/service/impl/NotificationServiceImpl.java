package com.user.management.service.impl;

import com.user.management.entity.User;
import com.user.management.service.NotificationService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final JavaMailSender mailSender;
    private final Configuration freeMarkerConfiguration;

    @Autowired
    public NotificationServiceImpl(JavaMailSender mailSender, Configuration freeMarkerConfiguration) {
        this.mailSender = mailSender;
        this.freeMarkerConfiguration = freeMarkerConfiguration;
    }

    /**
     *sendWelcomeMail() method creates a hashmap with the required parameters that we want to render
     * in our email and pass it to the sendEmail() method along with the template name, receiver mail
     * and subject for the email
     */
    @Override
    public void sendWelcomeMail(User user) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstName", user.getFirstName());
        sendEmail("WelcomeEmail.ftl", "Welcome to User Management System", user.getEmail(), parameters);
    }

    /**
     *sendEmail() method creates a MimeMessage and then with help of FreeMarker and JavaMailSender
     * we send the mail to the specified receiver email
     */
    private void sendEmail(String templateName, String subject, String receiverEmail, Map<String, Object> parameters) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            Template template = freeMarkerConfiguration.getTemplate(templateName);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, parameters);

            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("adityahalder200@gmail.com", "User Management");
            helper.setTo(receiverEmail);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException | IOException | TemplateException e) {
            e.printStackTrace();
        }
    }
}
