package com.fxmind.utils;


import com.fxmind.dto.PersonDTO;
import com.fxmind.entity.Person;

/**
 * @author Sergei Zhuravlev
 */
public class DTOConverter {
    public static PersonDTO convert(Person person) {
        PersonDTO personDTO = new PersonDTO();

        personDTO.setActivated(person.getRegistration().isActivated());
        personDTO.setUuid(person.getRegistration().getUuid());
        personDTO.setCreated(person.getRegistration().getCreated());
        personDTO.setRegIp(person.getRegistration().getRegIp());

        personDTO.setId(person.getId());
        personDTO.setBalance(person.getBalance());
        personDTO.setMail(person.getMail());
        personDTO.setPrivilege(person.getPrivilege());
        personDTO.setSubcriptionExpireDate(person.getSubcriptionExpireDate());

        return personDTO;
    }



}
