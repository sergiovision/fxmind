package com.fxmind.service;

import com.fxmind.config.FxMindProperties;
import com.fxmind.domain.Person;
import jakarta.mail.MessagingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Renders HTML mails with Thymeleaf and sends them over SMTP. When no SMTP server is configured
 * ({@code spring.mail.host} unset) mails are logged and skipped instead of failing the use case.
 */
@Service
public class SmtpMailService implements MailService {

    static final String REGISTRATION_TEMPLATE = "mail/registration";

    private static final Logger log = LoggerFactory.getLogger(SmtpMailService.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final ITemplateEngine templateEngine;
    private final FxMindProperties.Mail settings;

    public SmtpMailService(
            ObjectProvider<JavaMailSender> mailSender, ITemplateEngine templateEngine, FxMindProperties properties) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.settings = properties.mail();
    }

    @Override
    public void sendRegistrationNotification(Person person) {
        var activationLink = settings.registrationLinkBase() + "?token="
                + URLEncoder.encode(person.getRegistration().getActivationToken(), StandardCharsets.UTF_8);
        var context = new Context(Locale.ENGLISH);
        context.setVariable("email", person.getEmail());
        context.setVariable("activationLink", activationLink);
        var body = templateEngine.process(REGISTRATION_TEMPLATE, context);

        var sender = mailSender.getIfAvailable();
        if (sender == null) {
            log.warn("SMTP is not configured; registration mail for {} was not sent", person.getEmail());
            return;
        }
        try {
            var message = sender.createMimeMessage();
            var helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setFrom(settings.from());
            helper.setTo(person.getEmail());
            helper.setSubject(settings.registrationSubject());
            helper.setText(body, true);
            sender.send(message);
        } catch (MessagingException | MailException e) {
            log.error("Failed to send registration mail to {}", person.getEmail(), e);
        }
    }
}
