package com.fxmind.web.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fxmind.domain.Person;
import com.fxmind.domain.Privilege;
import com.fxmind.domain.Registration;
import com.fxmind.repository.PersonRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class PersonUserDetailsServiceTest {

    private final PersonRepository persons = mock(PersonRepository.class);
    private final PersonUserDetailsService service = new PersonUserDetailsService(persons);

    @Test
    void mapsPrivilegeToRoleAndActivationToEnabled() {
        var pending =
                new Person("new@example.com", "{bcrypt}x", Privilege.FREE, new Registration("t", Instant.EPOCH, null));
        var admin =
                new Person("admin@example.com", "{bcrypt}y", Privilege.ADMIN, Registration.preActivated(Instant.EPOCH));
        when(persons.findByEmailIgnoreCase("new@example.com")).thenReturn(Optional.of(pending));
        when(persons.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(admin));

        var user = service.loadUserByUsername("admin@example.com");
        assertThat(user.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
        assertThat(user.isEnabled()).isTrue();
        assertThat(service.loadUserByUsername("new@example.com").isEnabled()).isFalse();
    }

    @Test
    void unknownUserIsRejected() {
        assertThatThrownBy(() -> service.loadUserByUsername("ghost@example.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
