package com.fxmind.web.ui.view;

import com.fxmind.exception.FxMindException;
import com.fxmind.service.RegistrationService;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/** Target of the link in the registration e-mail ({@code /activate?token=...}). */
@Route("activate")
@PageTitle("Account activation | FxHubJava")
@AnonymousAllowed
public class ActivationView extends VerticalLayout implements BeforeEnterObserver {

    static final String TOKEN_PARAMETER = "token";

    private final RegistrationService registrationService;
    private final H2 title = new H2();
    private final Paragraph message = new Paragraph();

    public ActivationView(RegistrationService registrationService) {
        this.registrationService = registrationService;
        setAlignItems(Alignment.CENTER);
        add(title, message, new Anchor("login", "Go to sign in"));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var token = event.getLocation().getQueryParameters().getSingleParameter(TOKEN_PARAMETER);
        if (token.isEmpty() || token.get().isBlank()) {
            showFailure("The activation link is incomplete.");
            return;
        }
        try {
            var person = registrationService.activate(token.get());
            title.setText("Account activated");
            message.setText("Welcome, " + person.email() + ". You can now sign in.");
        } catch (FxMindException e) {
            showFailure("The activation link is invalid or has already been used.");
        }
    }

    String messageText() {
        return message.getText();
    }

    private void showFailure(String text) {
        title.setText("Activation failed");
        message.setText(text);
    }
}
