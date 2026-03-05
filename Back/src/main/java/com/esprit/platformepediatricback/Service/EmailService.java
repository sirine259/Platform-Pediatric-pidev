package com.esprit.platformepediatricback.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.from.email}")
    private String fromEmail;

    @Value("${spring.mail.from.name}")
    private String fromName;

    @Value("${spring.mail.templates.forgot-password.subject}")
    private String forgotPasswordSubject;

    @Value("${spring.mail.templates.password-reset.subject}")
    private String passwordResetSubject;

    /**
     * Envoyer un email simple
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    /**
     * Envoyer un email HTML avec template
     */
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);

            // Traiter le template Thymeleaf
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }

    /**
     * Envoyer l'email de mot de passe oublié
     */
    public void sendForgotPasswordEmail(String to, String resetToken, String userName) {
        Map<String, Object> variables = Map.of(
            "userName", userName,
            "resetToken", resetToken,
            // Le lien pointe vers le composant Angular forgot-password avec le token en query param
            "resetLink", "http://localhost:4200/auth/forgot-password?token=" + resetToken,
            "expiryHours", "24"
        );

        sendHtmlEmail(to, forgotPasswordSubject, "forgot-password", variables);
    }

    /**
     * Envoyer l'email de confirmation de réinitialisation
     */
    public void sendPasswordResetConfirmationEmail(String to, String userName) {
        Map<String, Object> variables = Map.of(
            "userName", userName,
            "loginLink", "http://localhost:4200/auth/login"
        );

        sendHtmlEmail(to, passwordResetSubject, "password-reset", variables);
    }

    /**
     * Envoyer l'email de bienvenue
     */
    public void sendWelcomeEmail(String to, String userName) {
        Map<String, Object> variables = Map.of(
            "userName", userName,
            "loginLink", "http://localhost:4200/auth/login"
        );

        sendHtmlEmail(to, "Bienvenue sur la Plateforme Pédiatrique", "welcome", variables);
    }

    /**
     * Envoyer l'email de notification de transplantation
     */
    public void sendTransplantNotificationEmail(String to, String patientName, String transplantDate) {
        Map<String, Object> variables = Map.of(
            "patientName", patientName,
            "transplantDate", transplantDate,
            "dashboardLink", "http://localhost:4200/dashboard"
        );

        sendHtmlEmail(to, "Notification de Transplantation", "transplant-notification", variables);
    }
}
