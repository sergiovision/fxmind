package com.fxmind.manager.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.springframework.context.annotation.Scope;
import ru.xpoft.vaadin.VaadinView;

@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(MainPageView.VIEW_NAME)
public class MainPageView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "main";


    public MainPageView() {
        setSizeFull();
        addStyleName("dashboard-view");

        HorizontalLayout top = new HorizontalLayout();
        top.setWidth("100%");
        top.setSpacing(true);
        top.addStyleName("toolbar");
        addComponent(top);
        final Label title = new Label("FXMind Manager");
        title.setSizeUndefined();
        title.addStyleName("h1");
        top.addComponent(title);
        top.setComponentAlignment(title, Alignment.MIDDLE_LEFT);
        top.setExpandRatio(title, 1);

        /*
        Button notify = new Button("2");

        notify.setDescription("Notifications (2 unread)");
        // notify.addStyleName("borderless");
        notify.addStyleName("notifications");
        notify.addStyleName("unread");
        notify.addStyleName("icon-only");
        notify.addStyleName("icon-bell");
        notify.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                //((MainUI) getUI()).clearDashboardButtonBadge();
                event.getButton().removeStyleName("unread");
                event.getButton().setDescription("Notifications");

                if (notifications != null && notifications.getUI() != null)
                    notifications.close();
                else {
                    buildNotifications(event);
                    getUI().addWindow(notifications);
                    notifications.focus();
                    ((CssLayout) getUI().getContent())
                            .addLayoutClickListener(new LayoutClickListener() {
                                @Override
                                public void layoutClick(LayoutClickEvent event) {
                                    notifications.close();
                                    ((CssLayout) getUI().getContent())
                                            .removeLayoutClickListener(this);
                                }
                            });
                }

            }
        });
        top.addComponent(notify);
        top.setComponentAlignment(notify, Alignment.MIDDLE_LEFT);
        */

    }

    @Override
    public void enter(ViewChangeEvent event) {
       // DataProvider dataProvider = ((MainUI) getUI()).dataProvider;
       // schedTable.setContainerDataSource(dataProvider.getRevenueByTitle());



    }

    /*
    public static Window notifications;

    private void buildNotifications(ClickEvent event) {
        if (notifications != null)
            return;
        notifications = new Window("Notifications");
        VerticalLayout l = new VerticalLayout();
        l.setMargin(true);
        l.setSpacing(true);
        notifications.setContent(l);
        notifications.setWidth("300px");
        notifications.addStyleName("notifications");
        notifications.setClosable(false);
        notifications.setResizable(false);
        notifications.setDraggable(false);
        notifications.setPositionX(event.getClientX() - event.getRelativeX());
        notifications.setPositionY(event.getClientY() - event.getRelativeY());
        notifications.setCloseShortcut(KeyCode.ESCAPE, null);

        Label label = new Label(
                "<hr><b>" + MssUserDetailsService.GetCurrentUserName()
                          + " started music processing</b><br><span>25 minutes ago</span><br>", ContentMode.HTML);
        l.addComponent(label);

        label = new Label("<hr><b>" + MssUserDetailsService.GetCurrentUserName()
                + " changed jobs schedule</b><br><span>2 days ago</span><br>", ContentMode.HTML);
        l.addComponent(label);
    }

    public static void SendNotification( String message ) {
        if (notifications != null)  {
            VerticalLayout l  = (VerticalLayout)notifications.getContent();
            if (l!= null) {
                Label label = new Label(message, ContentMode.TEXT);
                l.addComponent(label);
            }
        }
    } */

}
