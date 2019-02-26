package com.fxmind.service.impl;

import com.fxmind.entity.Person;
import com.fxmind.entity.Settings;
import com.fxmind.service.AdminService;
import com.fxmind.service.MailService;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Sergey Zhuravlev
 */
@Service
public class MailServiceImpl implements MailService {
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String REGISTRATION_TEMPLATE_NAME = "emailRegistrationNotification.vm";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    AdminService adminService;

    @Value("${mail.general.templates.location}")
    private String templatesLocation;

    @Value("${mail.general.from}")
    private String mailFrom;

    @Value("${mail.general.subject}")
    private String mailSubject;

    @Value("${mail.general.prompt}")
    private String userHelloPrompt;



    @Override
    public void sendRegistrationNotificationMail(final Person person) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(person.getMail());
                message.setFrom(mailFrom);
                message.setSubject(mailSubject);
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("helloPrompt", userHelloPrompt);

                String mailReglink = adminService.getProperty(Settings.MAIL_REG_LINK_PATH);
                model.put("regLink", mailReglink + "?uuid=" + person.getRegistration().getUuid());
                String templateLocation = templatesLocation + REGISTRATION_TEMPLATE_NAME;

                VelocityContext context = new VelocityContext();
                context.put("helloPrompt", userHelloPrompt);
                context.put("regLink", mailReglink + "?uuid=" + person.getRegistration().getUuid());

                StringWriter stringWriter = new StringWriter();
                velocityEngine.mergeTemplate(templateLocation, "UTF-8", context, stringWriter);
                String mailBody = stringWriter.toString();
                // String mailBody = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateLocation, DEFAULT_ENCODING, model);
                message.setText(mailBody, true);
            }
        };
        this.mailSender.send(preparator);

    }
}
