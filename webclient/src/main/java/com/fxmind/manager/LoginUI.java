package com.fxmind.manager;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


/**
  @author  Sergei Zhuravlev
 */
@SuppressWarnings("serial")
@Component
@Scope("prototype")
@Title("FXMind Web Client")
@Theme("webclient")
public class LoginUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            UI.getCurrent().getPage().setLocation(VaadinServlet.getCurrent().getServletContext().getContextPath() +
                    "/app/#!main");
        }

        setSizeFull();

        VerticalLayout viewLayout = new VerticalLayout();
        viewLayout.setSizeFull();
        viewLayout.setStyleName("login-view-layout");

        Label overview = new Label("FXMind Web Client");
        overview.setStyleName("title");

        CustomLayout loginForm = new CustomLayout("login");
        loginForm.setWidth("300px");

        viewLayout.addComponent(overview);
        viewLayout.addComponent(loginForm);
        viewLayout.setComponentAlignment(loginForm, Alignment.TOP_CENTER);

        viewLayout.setExpandRatio(overview, 1);
        viewLayout.setExpandRatio(loginForm, 2);

        setContent(viewLayout);
    }
}
