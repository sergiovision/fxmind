package com.fxmind.web.ui;

import com.fxmind.web.ui.view.DashboardView;
import com.fxmind.web.ui.view.JobsView;
import com.fxmind.web.ui.view.NewsView;
import com.fxmind.web.ui.view.PersonsView;
import com.fxmind.web.ui.view.SettingsView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

/** Shell of the console: header with the signed-in user and a side navigation drawer. */
@PermitAll
public class MainLayout extends AppLayout {

    public MainLayout(AuthenticationContext authentication) {
        var title = new H1("FXMind");
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        var user = new Span(authentication.getPrincipalName().orElse(""));
        user.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL);
        var logout = new Button("Log out", VaadinIcon.SIGN_OUT.create(), click -> authentication.logout());
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        var header = new HorizontalLayout(new DrawerToggle(), title, user, logout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(title);
        header.setWidthFull();
        header.addClassNames(LumoUtility.Padding.Horizontal.MEDIUM);
        addToNavbar(header);

        var nav = new SideNav();
        nav.addItem(
                new SideNavItem("Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create()),
                new SideNavItem("News calendar", NewsView.class, VaadinIcon.CALENDAR.create()),
                new SideNavItem("Scheduled jobs", JobsView.class, VaadinIcon.CLOCK.create()),
                new SideNavItem("Users", PersonsView.class, VaadinIcon.USERS.create()),
                new SideNavItem("Settings", SettingsView.class, VaadinIcon.COG.create()));
        addToDrawer(nav);
        setPrimarySection(Section.DRAWER);
    }
}
