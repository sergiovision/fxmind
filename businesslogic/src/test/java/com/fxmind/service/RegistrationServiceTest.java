package com.fxmind.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fxmind.domain.Person;
import com.fxmind.domain.Privilege;
import com.fxmind.domain.Registration;
import com.fxmind.exception.RegistrationException;
import com.fxmind.exception.ResourceNotFoundException;
import com.fxmind.mapper.DomainMapper;
import com.fxmind.repository.PersonRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class RegistrationServiceTest {

    private final PersonRepository persons = mock(PersonRepository.class);
    private final MailService mail = mock(MailService.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);
    private final RegistrationService service = new RegistrationService(
            persons, encoder, mail, NewsEventServiceTest.brokerClock(), Mappers.getMapper(DomainMapper.class));

    @Test
    void registersWithHashedPasswordAndSendsActivationMail() {
        when(persons.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var dto = service.register(" Trader@Example.com ", "correct horse battery", "10.0.0.1");

        assertThat(dto.email()).isEqualTo("trader@example.com");
        assertThat(dto.privilege()).isEqualTo(Privilege.FREE);
        assertThat(dto.activated()).isFalse();
        verify(mail).sendRegistrationNotification(any(Person.class));
        verify(persons)
                .save(org.mockito.ArgumentMatchers.argThat(
                        person -> encoder.matches("correct horse battery", person.getPasswordHash())
                                && person.getRegistration().getActivationToken() != null));
    }

    @Test
    void rejectsDuplicateEmailAndWeakPassword() {
        when(persons.existsByEmailIgnoreCase("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.register("taken@example.com", "long enough password", null))
                .isInstanceOf(RegistrationException.class);
        assertThatThrownBy(() -> service.register("new@example.com", "short", null))
                .isInstanceOf(RegistrationException.class);
        verify(mail, never()).sendRegistrationNotification(any());
    }

    @Test
    void activatesByToken() {
        var person = new Person("a@example.com", "hash", Privilege.FREE, new Registration("token", null, null));
        when(persons.findByRegistrationActivationToken("token")).thenReturn(Optional.of(person));

        assertThat(service.activate("token").activated()).isTrue();
        assertThat(person.getRegistration().getActivationToken()).isNull();
        when(persons.findByRegistrationActivationToken("token")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.activate("token"))
                .as("a consumed token cannot re-activate an account")
                .isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> service.activate("missing")).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void provisionsAdministratorOnlyOnce() {
        when(persons.existsByPrivilege(Privilege.ADMIN)).thenReturn(false, true);

        assertThat(service.provisionAdministrator("admin@example.com", "a-very-strong-secret"))
                .isTrue();
        assertThat(service.provisionAdministrator("admin@example.com", "a-very-strong-secret"))
                .isFalse();
    }
}
