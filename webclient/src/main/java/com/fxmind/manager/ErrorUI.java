package com.fxmind.manager;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Andrey.Charnamys
 */
@Component
@Scope("prototype")
@Theme("webclient")
@Title("Error")
public class ErrorUI extends UI {

    private Logger logger = LoggerFactory.getLogger(ErrorUI.class);

    private static String errorTitle = "Unknown Error";
    private static String errorMessage = "We are sorry, something went wrong. Please, try again later or contact your system administrator.";
    private static String backHomeMessage = "Back to login page";

    @Override
    protected void init(VaadinRequest request) {
        HttpServletResponse servletResponse = ((VaadinServletRequest) request).getService().getCurrentResponse().getHttpServletResponse();

        // for some reason vaadin UI is not loaded when http status is error
        servletResponse.setStatus(200);

        // it should be RequestDispatcher.ERROR_EXCEPTION instead of javax.servlet.error.exception
        // how are we still using 2.5 servlet api?
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");

        // check status code if needed
        //Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

        // check for different exception types if needed
        if (throwable != null) {
            logger.error(throwable.getMessage(),throwable);
        }

        setSizeFull();

        Page.getCurrent().setTitle(errorTitle);

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setSizeFull();
        contentLayout.setStyleName("error-layout");

        Label overview = new Label(errorTitle);
        overview.setStyleName("title");
        overview.setHeight("100%");

        HorizontalLayout formContainer = new HorizontalLayout();
        formContainer.setHeight("345px");
        formContainer.setWidth("50%");
        formContainer.setStyleName("error-container");

        VerticalLayout errorMessageVerticalLayout = new VerticalLayout();
        errorMessageVerticalLayout.setWidth("100%");

        Label errorLabel = new Label(errorMessage);
        errorLabel.setHeight("100%");
        errorLabel.setStyleName("error-message");

        Link backHomeLink = new Link(backHomeMessage, new ExternalResource("/auth"));
        backHomeLink.setHeight("100%");

        Panel emptyPanel = new Panel();
        emptyPanel.setHeight("100%");

        formContainer.addComponent(errorMessageVerticalLayout);
        errorMessageVerticalLayout.addComponent(errorLabel);
        errorMessageVerticalLayout.addComponent(backHomeLink);
        contentLayout.addComponent(overview);
        contentLayout.addComponent(formContainer);
        contentLayout.addComponent(emptyPanel);

        contentLayout.setComponentAlignment(overview, Alignment.MIDDLE_CENTER);
        contentLayout.setComponentAlignment(formContainer, Alignment.MIDDLE_CENTER);
        errorMessageVerticalLayout.setComponentAlignment(backHomeLink, Alignment.TOP_LEFT);

        setContent(contentLayout);
    }
}
