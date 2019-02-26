package com.fxmind.manager.controller;

import com.fxmind.dao.PersonDao;
import com.fxmind.dto.PersonDTO;
import com.fxmind.entity.Person;
import com.fxmind.service.AdminService;
import com.fxmind.utils.DTOConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergei Zhuravlev
 */
@org.springframework.stereotype.Controller
public class Controller implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    @Autowired
    public AdminService adminService;

    @Autowired
    private PersonDao personDao;


    public List<PersonDTO> loadPersons() {
        List<PersonDTO> result = new ArrayList<PersonDTO>();
        List<Person> persons = adminService.loadAllPersons();
        for (Person person : persons) {
            result.add(DTOConverter.convert(person));
        }
        return result;
    }

    public void savePerson(PersonDTO personDTO) {
        Person person = adminService.findPersonById(personDTO.getId());
        person.setBalance(personDTO.getBalance());
        person.setPrivilege(personDTO.getPrivilege());
        person.getRegistration().setActivated(personDTO.getActivated());
        adminService.updatePerson(person);
    }

}
