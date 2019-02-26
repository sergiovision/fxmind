package com.fxmind.manager;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Sergei Zhuravlev
 */
@Component
@Scope("prototype")
@Title("FXMind Web Client")
@Theme("webclient")
public class AccessDeniedUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setSizeFull();

        VerticalLayout viewLayout = new VerticalLayout();
        viewLayout.setSizeFull();
        viewLayout.setStyleName("access-denied-view-layout");

        Label overview = new Label("Music Streaming Service Admin");
        overview.setStyleName("title");

        CustomLayout accessDeniedLayout = new CustomLayout("accessDenied");
        Label accessDeniedMessage = new Label("Access Denied");
        accessDeniedMessage.setStyleName("access-denied-message");
        accessDeniedLayout.addComponent(accessDeniedMessage, "accessDeniedMessage");

        Button logoutButton = new Button("Logout");
        logoutButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().getSession().close();
                UI.getCurrent().getPage().setLocation(VaadinServlet.getCurrent().getServletContext().getContextPath() +
                        "/j_spring_security_logout");
            }
        });
        logoutButton.setWidth("100%");
        accessDeniedLayout.addComponent(logoutButton, "logoutButton");

        accessDeniedLayout.setWidth("300px");

        viewLayout.addComponent(overview);
        viewLayout.addComponent(accessDeniedLayout);
        viewLayout.setComponentAlignment(accessDeniedLayout, Alignment.TOP_CENTER);

        viewLayout.setExpandRatio(overview, 1);
        viewLayout.setExpandRatio(accessDeniedLayout, 2);

        setContent(viewLayout);
    }
}
