package com.fxmind.web.config;

import com.fxmind.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Creates the first administrator from environment-supplied credentials; never from hard-coded ones. */
@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final AdminProperties admin;
    private final RegistrationService registrationService;

    public AdminBootstrap(AdminProperties admin, RegistrationService registrationService) {
        this.admin = admin;
        this.registrationService = registrationService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!admin.isConfigured()) {
            log.debug("No bootstrap administrator configured (FXMIND_ADMIN_EMAIL / FXMIND_ADMIN_PASSWORD)");
            return;
        }
        if (!registrationService.provisionAdministrator(admin.email(), admin.password())) {
            log.debug("Administrator already exists; bootstrap credentials ignored");
        }
    }
}
