package com.fxmind.service.impl;

import com.fxmind.dao.PersonDao;
import com.fxmind.entity.Person;
import com.fxmind.entity.Registration;
import com.fxmind.service.MailService;
import com.fxmind.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * @author Sergei Zhuravlev
 */
@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private PersonDao personDao;

    @Autowired
    private org.springframework.security.crypto.password.MessageDigestPasswordEncoder md5PasswordEncoder;

    @Autowired
    private MailService mailService;

    @Override
    public boolean isExistByMail(String mail) {
        Person person = personDao.findByMail(mail);
        return person != null;
    }

    @Transactional
    @Override
    public Person activateUser(String uuid) {
        Person person = personDao.findByUuid(uuid);
        if (person == null) {
            return null;
        }

        Boolean activated = person.getRegistration().isActivated();
        if (activated == null || !activated) {
            person.getRegistration().setActivated(true);
        }
        return person;
    }

    @Override
    public boolean isUserActivated(String uuid) {
        Person person = personDao.findByUuid(uuid);
        return person != null && (person.getRegistration().isActivated() != null ? person.getRegistration().isActivated() : false);
    }

    @Transactional
    @Override
    public void registerUser(String email, String password, String userIp) {
        Person person = createUser(email, password, userIp);

        mailService.sendRegistrationNotificationMail(person);
    }

    private Person createUser(String email, String password, String userIp) {
        Person person = new Person();
        person.setCredential(password);
        person.setMail(email);

        Registration registration = new Registration();
        registration.setCreated(new Date());
        registration.setUuid(UUID.randomUUID().toString());
        registration.setActivated(false);
        registration.setRegIp(userIp);
        person.setRegistration(registration);

        String credential = person.getCredential();
        credential = md5PasswordEncoder.encode(credential); //, person.getMail());
        person.setCredential(credential);
        person.setPrivilege(Person.Privilege.FREE);

        person.setBalance(new BigDecimal(0));
        person.setLanguageId(0);

        personDao.persist(person);

        return person;
    }
}
