package com.fxmind.service;

import com.fxmind.domain.Person;

/** Outbound user notifications. */
public interface MailService {

    void sendRegistrationNotification(Person person);
}
