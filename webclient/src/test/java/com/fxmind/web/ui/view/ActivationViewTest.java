package com.fxmind.web.ui.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fxmind.domain.Privilege;
import com.fxmind.dto.PersonDto;
import com.fxmind.exception.ResourceNotFoundException;
import com.fxmind.service.RegistrationService;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ActivationViewTest {

    private final RegistrationService registration = mock(RegistrationService.class);
    private final ActivationView view = new ActivationView(registration);

    @Test
    void activatesAccountFromMailLink() {
        when(registration.activate("tok"))
                .thenReturn(
                        new PersonDto(1L, "a@example.com", Privilege.FREE, BigDecimal.ZERO, true, null, null, null));

        view.beforeEnter(eventWith(Map.of("token", "tok")));

        assertThat(view.messageText()).contains("a@example.com");
    }

    @Test
    void reportsUnknownOrMissingToken() {
        when(registration.activate("bad")).thenThrow(new ResourceNotFoundException("Registration", "bad"));

        view.beforeEnter(eventWith(Map.of("token", "bad")));
        assertThat(view.messageText()).contains("invalid");

        view.beforeEnter(eventWith(Map.of()));
        assertThat(view.messageText()).contains("incomplete");
    }

    private static BeforeEnterEvent eventWith(Map<String, String> parameters) {
        var event = mock(BeforeEnterEvent.class);
        when(event.getLocation()).thenReturn(new Location("activate", QueryParameters.simple(parameters)));
        return event;
    }
}
