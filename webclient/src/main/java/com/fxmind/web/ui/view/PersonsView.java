package com.fxmind.web.ui.view;

import com.fxmind.domain.Privilege;
import com.fxmind.dto.PersonDto;
import com.fxmind.exception.FxMindException;
import com.fxmind.service.PersonService;
import com.fxmind.web.ui.Formats;
import com.fxmind.web.ui.MainLayout;
import com.fxmind.web.ui.Notifications;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import java.math.BigDecimal;

/** User administration: privilege, balance and activation status. */
@Route(value = "users", layout = MainLayout.class)
@PageTitle("Users | FXMind")
@RolesAllowed("ADMIN")
public class PersonsView extends VerticalLayout {

    private final PersonService persons;
    private final Grid<PersonDto> grid = new Grid<>();

    public PersonsView(PersonService persons) {
        this.persons = persons;
        grid.addColumn(PersonDto::email).setHeader("E-mail").setFlexGrow(1);
        grid.addColumn(PersonDto::privilege).setHeader("Privilege").setAutoWidth(true);
        grid.addColumn(PersonDto::balance).setHeader("Balance").setAutoWidth(true);
        grid.addColumn(person -> person.activated() ? "Yes" : "No")
                .setHeader("Active")
                .setAutoWidth(true);
        grid.addColumn(person -> Formats.utc(person.registeredAt()))
                .setHeader("Registered")
                .setAutoWidth(true);
        grid.addComponentColumn(person -> new Button("Edit", click -> edit(person)))
                .setAutoWidth(true)
                .setFlexGrow(0);
        grid.setSizeFull();
        refresh();
        add(new H2("Users"), grid);
        setSizeFull();
    }

    private void edit(PersonDto person) {
        var privilege = new ComboBox<Privilege>("Privilege");
        privilege.setItems(Privilege.values());
        privilege.setValue(person.privilege());
        var balance = new BigDecimalField("Balance");
        balance.setValue(person.balance());
        var activated = new Checkbox("Account activated", person.activated());

        var dialog = new Dialog("Edit " + person.email());
        dialog.add(new FormLayout(privilege, balance, activated));
        var save = new Button("Save", click -> {
            if (privilege.isEmpty()) {
                privilege.setInvalid(true);
                return;
            }
            try {
                persons.updateAccount(
                        person.id(),
                        privilege.getValue(),
                        balance.getValue() == null ? BigDecimal.ZERO : balance.getValue(),
                        activated.getValue());
                Notifications.success("User updated");
                dialog.close();
                refresh();
            } catch (FxMindException e) {
                Notifications.error("Could not update user: " + e.getMessage());
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.getFooter().add(new Button("Cancel", click -> dialog.close()), save);
        dialog.open();
    }

    private void refresh() {
        grid.setItems(persons.findAll());
    }
}
