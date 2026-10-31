package com.fxmind.web.ui.view;

import com.fxmind.domain.Setting;
import com.fxmind.exception.FxMindException;
import com.fxmind.service.SettingsService;
import com.fxmind.web.ui.MainLayout;
import com.fxmind.web.ui.Notifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

/** Runtime settings with edit and reset-to-default. */
@Route(value = "settings", layout = MainLayout.class)
@PageTitle("Settings | FxHubJava")
@RolesAllowed("ADMIN")
public class SettingsView extends VerticalLayout {

    private final SettingsService settings;
    private final Grid<Setting> grid = new Grid<>();

    public SettingsView(SettingsService settings) {
        this.settings = settings;
        grid.addColumn(Setting::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(Setting::getValue).setHeader("Value").setAutoWidth(true);
        grid.addColumn(Setting::getDefaultValue).setHeader("Default").setAutoWidth(true);
        grid.addColumn(Setting::getDescription).setHeader("Description").setFlexGrow(1);
        grid.addComponentColumn(setting -> new Button("Edit", click -> edit(setting)))
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.setSizeFull();
        refresh();
        add(new H2("Settings"), grid);
        setSizeFull();
    }

    private void edit(Setting setting) {
        var value = new TextArea("Value", setting.getValue() == null ? "" : setting.getValue(), "");
        value.setWidthFull();
        var dialog = new Dialog(setting.getName());
        dialog.setWidth("32rem");
        dialog.add(new Paragraph(setting.getDescription()), value);
        var save =
                new Button("Save", click -> apply(dialog, () -> settings.update(setting.getName(), value.getValue())));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        var reset = new Button(
                "Reset to default", click -> apply(dialog, () -> settings.resetToDefault(setting.getName())));
        reset.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(reset, new Button("Cancel", click -> dialog.close()), save);
        dialog.open();
    }

    private void apply(Dialog dialog, Runnable action) {
        try {
            action.run();
            Notifications.success("Setting saved");
            dialog.close();
            refresh();
        } catch (FxMindException e) {
            Notifications.error(e.getMessage());
        }
    }

    private void refresh() {
        grid.setItems(settings.findAll());
    }
}
