package com.fxmind.service;

import com.fxmind.domain.Person;
import com.fxmind.domain.Privilege;
import com.fxmind.domain.Registration;
import com.fxmind.dto.PersonDto;
import com.fxmind.exception.RegistrationException;
import com.fxmind.exception.ResourceNotFoundException;
import com.fxmind.mapper.DomainMapper;
import com.fxmind.repository.PersonRepository;
import java.util.Locale;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Self-service sign-up with e-mail activation, plus first-run administrator provisioning. */
@Service
@Transactional(readOnly = true)
public class RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);
    private static final int MIN_PASSWORD_LENGTH = 12;

    private final PersonRepository persons;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final BrokerClock clock;
    private final DomainMapper mapper;

    public RegistrationService(
            PersonRepository persons,
            PasswordEncoder passwordEncoder,
            MailService mailService,
            BrokerClock clock,
            DomainMapper mapper) {
        this.persons = persons;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.clock = clock;
        this.mapper = mapper;
    }

    @Transactional
    public PersonDto register(String email, String rawPassword, String clientIp) {
        var normalizedEmail = normalize(email);
        validatePassword(rawPassword);
        if (persons.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new RegistrationException("E-mail is already registered");
        }
        var registration = new Registration(UUID.randomUUID().toString(), clock.now(), clientIp);
        var person = persons.save(
                new Person(normalizedEmail, passwordEncoder.encode(rawPassword), Privilege.FREE, registration));
        mailService.sendRegistrationNotification(person);
        return mapper.toDto(person);
    }

    /**
     * Activates the account behind a mail link. Tokens are single-use: once consumed they no longer
     * resolve, so a user deactivated by an administrator cannot re-enable themselves with the old link.
     */
    @Transactional
    public PersonDto activate(String token) {
        var person = persons.findByRegistrationActivationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", token));
        person.activate();
        return mapper.toDto(person);
    }

    /** Creates the initial administrator when none exists. Returns {@code true} if an account was created. */
    @Transactional
    public boolean provisionAdministrator(String email, String rawPassword) {
        if (persons.existsByPrivilege(Privilege.ADMIN)) {
            return false;
        }
        validatePassword(rawPassword);
        persons.save(new Person(
                normalize(email),
                passwordEncoder.encode(rawPassword),
                Privilege.ADMIN,
                Registration.preActivated(clock.now())));
        log.info("Provisioned initial administrator account {}", normalize(email));
        return true;
    }

    private static String normalize(String email) {
        if (email == null || !email.contains("@")) {
            throw new RegistrationException("A valid e-mail address is required");
        }
        return email.strip().toLowerCase(Locale.ROOT);
    }

    private static void validatePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new RegistrationException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
    }
}
