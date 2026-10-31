package com.fxmind.web.security;

import com.fxmind.repository.PersonRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Authenticates console users against the {@code person} table; inactive accounts cannot log in. */
@Service
public class PersonUserDetailsService implements UserDetailsService {

    private final PersonRepository persons;

    public PersonUserDetailsService(PersonRepository persons) {
        this.persons = persons;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        return persons.findByEmailIgnoreCase(username)
                .map(person -> User.withUsername(person.getEmail())
                        .password(person.getPasswordHash())
                        .authorities(person.getPrivilege().authority())
                        .disabled(!person.isActivated())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user"));
    }
}
