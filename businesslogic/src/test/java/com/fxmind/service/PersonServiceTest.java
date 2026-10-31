package com.fxmind.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fxmind.domain.Person;
import com.fxmind.domain.Privilege;
import com.fxmind.domain.Registration;
import com.fxmind.exception.BusinessRuleException;
import com.fxmind.mapper.DomainMapper;
import com.fxmind.repository.PersonRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class PersonServiceTest {

    private final PersonRepository persons = mock(PersonRepository.class);
    private final PersonService service = new PersonService(persons, Mappers.getMapper(DomainMapper.class));
    private final Person admin =
            new Person("admin@example.com", "{bcrypt}x", Privilege.ADMIN, Registration.preActivated(Instant.EPOCH));
    private final Person otherAdmin =
            new Person("other@example.com", "{bcrypt}y", Privilege.ADMIN, Registration.preActivated(Instant.EPOCH));

    @Test
    void lastActiveAdministratorCannotBeDemotedOrDeactivated() {
        when(persons.findById(1L)).thenReturn(Optional.of(admin));
        when(persons.findByPrivilegeAndRegistrationActivatedTrue(Privilege.ADMIN))
                .thenReturn(List.of(admin));

        assertThatThrownBy(() -> service.updateAccount(1L, Privilege.FREE, BigDecimal.ZERO, true))
                .isInstanceOf(BusinessRuleException.class);
        assertThatThrownBy(() -> service.updateAccount(1L, Privilege.ADMIN, BigDecimal.ZERO, false))
                .isInstanceOf(BusinessRuleException.class);
        assertThat(service.updateAccount(1L, Privilege.ADMIN, BigDecimal.TEN, true)
                        .balance())
                .isEqualTo(BigDecimal.TEN);
    }

    @Test
    void administratorCanBeDemotedWhenAnotherRemains() {
        when(persons.findById(1L)).thenReturn(Optional.of(admin));
        when(persons.findByPrivilegeAndRegistrationActivatedTrue(Privilege.ADMIN))
                .thenReturn(List.of(admin, otherAdmin));

        assertThat(service.updateAccount(1L, Privilege.FREE, BigDecimal.ZERO, true)
                        .privilege())
                .isEqualTo(Privilege.FREE);
    }

    @Test
    void adminActivationDecisionsInvalidateTheMailToken() {
        var user =
                new Person("user@example.com", "{bcrypt}z", Privilege.FREE, new Registration("T", Instant.EPOCH, null));
        when(persons.findById(2L)).thenReturn(Optional.of(user));

        service.updateAccount(2L, Privilege.FREE, BigDecimal.ZERO, true);
        service.updateAccount(2L, Privilege.FREE, BigDecimal.ZERO, false);

        assertThat(user.getRegistration().getActivationToken())
                .as("token T no longer resolves, so /activate?token=T cannot re-enable the account")
                .isNull();
        assertThat(user.isActivated()).isFalse();
    }
}
