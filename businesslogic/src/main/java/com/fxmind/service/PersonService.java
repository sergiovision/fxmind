package com.fxmind.service;

import com.fxmind.domain.Privilege;
import com.fxmind.dto.PersonDto;
import com.fxmind.exception.BusinessRuleException;
import com.fxmind.exception.ResourceNotFoundException;
import com.fxmind.mapper.DomainMapper;
import com.fxmind.repository.PersonRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Administration of platform users. */
@Service
@Transactional(readOnly = true)
public class PersonService {

    private final PersonRepository persons;
    private final DomainMapper mapper;

    public PersonService(PersonRepository persons, DomainMapper mapper) {
        this.persons = persons;
        this.mapper = mapper;
    }

    public List<PersonDto> findAll() {
        return persons.findAll(Sort.by("email")).stream().map(mapper::toDto).toList();
    }

    public long count() {
        return persons.count();
    }

    @Transactional
    public PersonDto updateAccount(long id, Privilege privilege, BigDecimal balance, boolean activated) {
        var person = persons.findById(id).orElseThrow(() -> new ResourceNotFoundException("Person", id));
        var isActiveAdmin = person.getPrivilege() == Privilege.ADMIN && person.isActivated();
        var staysActiveAdmin = privilege == Privilege.ADMIN && activated;
        if (isActiveAdmin
                && !staysActiveAdmin
                && persons.findByPrivilegeAndRegistrationActivatedTrue(Privilege.ADMIN)
                                .size()
                        <= 1) {
            throw new BusinessRuleException("The last active administrator cannot be demoted or deactivated");
        }
        person.setPrivilege(privilege);
        person.setBalance(balance);
        person.getRegistration().setActivated(activated);
        return mapper.toDto(person);
    }
}
