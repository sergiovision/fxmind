package com.fxmind.service;


import com.fxmind.entity.Person;

/**
 * @author Sergei Zhuravlev
 */
public interface MailService {
    void sendRegistrationNotificationMail(Person person);
}
