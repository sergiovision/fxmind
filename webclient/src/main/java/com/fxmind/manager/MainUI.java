package com.fxmind.manager;

import com.fxmind.global.FXMindMQL;
import com.fxmind.manager.controller.Controller;
import com.fxmind.manager.view.*;
import com.fxmind.service.GlobalClient;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;
import ru.xpoft.vaadin.DiscoveryNavigator;

import java.util.*;

@org.springframework.stereotype.Component
@Scope("prototype")
@Title("FXMind Web Client")
@Theme("webclient")
@Configurable(preConstruction = true)
@Push(PushMode.MANUAL) //transport = Transport.STREAMING
public class MainUI extends UI {
    CssLayout root = new CssLayout();

    VerticalLayout loginLayout;

    CssLayout menu = new CssLayout();
    CssLayout content = new CssLayout();

    @Autowired
    private Controller controller;

    @Autowired
    GlobalClient globalClient;

    HashMap<String, Class<? extends View>> routes = new HashMap<String, Class<? extends View>>() {
        {
            put(MainPageView.VIEW_NAME, MainPageView.class);
            put(ScheduledJobsView.VIEW_NAME, ScheduledJobsView.class);
            put(PersonsView.VIEW_NAME, PersonsView.class);
            put(NewsView.VIEW_NAME, NewsView.class);
            put(SettingsView.VIEW_NAME, SettingsView.class);
        }

    };

    private Navigator navigator;


    public void TestMQL(String datetime) {

        try {
            FXMindMQL.Iface mql = globalClient.connectFXMindMQLClient();

            List<String> strInput = new ArrayList<>();
            Map<String, String> params = new HashMap<>();
            params.put("func", "NextNewsEvent");
            params.put("time", datetime);
            params.put("symbol", "EURUSD");
            params.put("importance", "1");

            strInput = mql.ProcessStringData(params, strInput);

            globalClient.closeFXMindMQLClient();

        } catch (TException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void init(VaadinRequest request) {
        getSession().setConverterFactory(new TypesConverterFactory());

        //TestMQL("2014.05.27 09:00");
        //TestMQL("2014.02.26 09:00");

        setLocale(Locale.US);

        setContent(root);
        root.addStyleName("root");
        root.setSizeFull();

        // Unfortunate to use an actual widget here, but since CSS generated
        // elements can't be transitioned yet, we must
        Label bg = new Label();
        bg.setSizeUndefined();
        bg.addStyleName("login-bg");
        root.addComponent(bg);

        buildMainView();
    }

    private void buildMainView() {

        navigator = new DiscoveryNavigator(this, content);

        root.addComponent(new HorizontalLayout() {
            {
                setSizeFull();
                addStyleName("main-view");
                addComponent(new VerticalLayout() {
                    // Sidebar
                    {
                        addStyleName("sidebar");
                        setWidth(null);
                        setHeight("100%");

                        // Branding element
                        addComponent(new CssLayout() {
                            {
                                addStyleName("branding");
                                Label logo = new Label(
                                        "FXMind Client",
                                        ContentMode.HTML);
                                logo.setSizeUndefined();
                                addComponent(logo);
                                // addComponent(new Image(null, new
                                // ThemeResource(
                                // "img/branding.png")));
                            }
                        });

                        // Main menu
                        addComponent(menu);
                        setExpandRatio(menu, 1);

                        // User menu
                        addComponent(new VerticalLayout() {
                            {
                                setSizeUndefined();
                                addStyleName("user");
                                Image profilePic = new Image(
                                        null,
                                        new ThemeResource("img/profile-pic.png"));
                                profilePic.setWidth("34px");
                                addComponent(profilePic);
                                Label userName = new Label(MssUserDetailsService.GetCurrentUserName());
                                userName.setSizeUndefined();
                                addComponent(userName);

                                Command cmd = new Command() {
                                    @Override
                                    public void menuSelected(
                                            MenuItem selectedItem) {
                                        Notification.show("Not implemented");
                                    }
                                };
                                MenuBar settings = new MenuBar();
                                MenuItem settingsMenu = settings.addItem("", null);
                                settingsMenu.setStyleName("icon-cog");
                                settingsMenu.addItem("Settings", cmd);
                                addComponent(settings);

                                Button exit = new NativeButton("Exit");
                                exit.addStyleName("icon-cancel");
                                exit.setDescription("Sign Out");
                                addComponent(exit);
                                exit.addClickListener(new ClickListener() {
                                    @Override
                                    public void buttonClick(ClickEvent event) {
                                        // Implement logout
                                        UI.getCurrent().getSession().close();
                                        UI.getCurrent().getPage().setLocation(VaadinServlet.getCurrent().getServletContext().getContextPath() +
                                                "/j_spring_security_logout");
                                    }
                                });
                            }
                        });
                    }
                });
                // Content
                addComponent(content);
                content.setSizeFull();
                content.addStyleName("view-content");
                setExpandRatio(content, 1);
            }

        });

        menu.removeAllComponents();

        for (final String view : routes.keySet()) {
            Button b = new NativeButton(view);

            //Button b = new NativeButton(view.substring(0, 1).toUpperCase()
            //        + view.substring(1).replace('-', ' '));
            //b.addStyleName("icon-" + view);
            b.addClickListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    clearMenuSelection();
                    event.getButton().addStyleName("selected");
                    if (!navigator.getState().equals(view))
                        navigator.navigateTo(view);
                }
            });

            menu.addComponent(b);
        }
        menu.addStyleName("menu");
        menu.setHeight("100%");

        navigator.navigateTo(MainPageView.VIEW_NAME);
        // menu.getComponent(0).addStyleName("selected");
    }

    private void clearMenuSelection() {
        for (int i = 0; i < menu.getComponentCount(); i++) {
            Component next = menu.getComponent(i);
            if (next instanceof NativeButton) {
                next.removeStyleName("selected");
            }
        }
    }

}
