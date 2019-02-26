package com.fxmind.manager;


import com.fxmind.dao.PersonDao;
import com.fxmind.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author Sergei Zhuravlev
 */
@Component
public class MssUserDetailsService implements UserDetailsService {

    @Autowired
    private PersonDao personDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = personDao.findByMail(username);
        if (person == null) {
            throw new UsernameNotFoundException("User with username: " + username + " not found");
        }
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(person.getPrivilege().name());
        return new User(person.getMail(), person.getCredential(), Arrays.asList(grantedAuthority));
    }

    public static String GetCurrentUserName()
    {
        String userName = "";
        try {
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if ( principal == null )
                return userName;
            return principal.getUsername();
        } catch (Exception e) {

        }
        return userName;
    }

}
