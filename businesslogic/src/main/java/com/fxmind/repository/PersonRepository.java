package com.fxmind.repository;

import com.fxmind.domain.Person;
import com.fxmind.domain.Privilege;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByEmailIgnoreCase(String email);

    Optional<Person> findByRegistrationActivationToken(String activationToken);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPrivilege(Privilege privilege);

    /** Locks the active accounts with the privilege so concurrent edits are serialised. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Person> findByPrivilegeAndRegistrationActivatedTrue(Privilege privilege);
}
