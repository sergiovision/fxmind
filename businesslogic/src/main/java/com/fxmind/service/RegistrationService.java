package com.fxmind.service;

import com.fxmind.entity.Person;

/**
 * @author Sergei Zhuravlev
 */
public interface RegistrationService {

    boolean isExistByMail(String mail);

    Person activateUser(String uuid);

    boolean isUserActivated(String uuid);

    void registerUser(String email, String password, String userIp);
}
