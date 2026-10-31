package com.fxmind;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fxmind.domain.Privilege;
import com.fxmind.repository.PersonRepository;
import com.fxmind.web.scheduling.SchedulerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Boots the full application (Vaadin, security, Quartz, JPA, Flyway) against in-memory H2. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FxMindApplicationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    PersonRepository persons;

    @Autowired
    SchedulerService scheduler;

    @Test
    void provisionsBootstrapAdministratorWithHashedPassword() {
        var admin = persons.findByEmailIgnoreCase("admin@fxmind.test").orElseThrow();

        assertThat(admin.getPrivilege()).isEqualTo(Privilege.ADMIN);
        assertThat(admin.getPasswordHash()).startsWith("{bcrypt}");
        assertThat(admin.isActivated()).isTrue();
    }

    @Test
    void exposesSeededJobsThroughTheSchedulerFacade() {
        assertThat(scheduler.listJobs())
                .extracting("handler")
                .contains("forexfactory", "exness", "myfxbook", "oanda", "etoro");
    }

    @Test
    void healthProbeIsPublic() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    void sensitiveActuatorEndpointsRequireAuthentication() throws Exception {
        mvc.perform(get("/actuator/metrics"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(roles = "FREE")
    void sensitiveActuatorEndpointsAreForbiddenForNonAdmins() throws Exception {
        mvc.perform(get("/actuator/metrics")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void administratorsCanReadMetrics() throws Exception {
        mvc.perform(get("/actuator/metrics")).andExpect(status().isOk());
    }

    @Test
    void consoleRedirectsAnonymousUsersToLogin() throws Exception {
        mvc.perform(get("/users")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login"));
    }
}
