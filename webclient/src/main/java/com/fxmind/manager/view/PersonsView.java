package com.fxmind.manager.view;

import com.fxmind.dto.PersonDTO;
import com.fxmind.entity.Person;
import com.fxmind.manager.controller.Controller;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import ru.xpoft.vaadin.VaadinView;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Sergei Zhuravlev
 */
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(PersonsView.VIEW_NAME)
public class PersonsView  extends Panel implements View {
    public static final String VIEW_NAME = "persons";

    private static final String TITLE = "Persons";

    private static final String COLUMN_EDIT_PROPERTY_ID = "Edit";
    private static final String COLUMN_BALANCE_PROPERTY_ID = "Balance";
    private static final String COLUMN_MAIL_PROPERTY_ID = "Mail";
    private static final String COLUMN_PRIVILEGE_PROPERTY_ID = "Privilege";
    private static final String COLUMN_UUID_PROPERTY_ID = "UUID";
    private static final String COLUMN_ACTIVATED_PROPERTY_ID = "Activated";
    private static final String COLUMN_CREATED_PROPERTY_ID = "SignedIn";

    @Autowired
    private Controller controller;

    private List<PersonDTO> persons;
    private IndexedContainer containerDataSource;
    private Table personsTable;

    @PostConstruct
    private void init() {
        persons = controller.loadPersons();

        containerDataSource = new IndexedContainer();

        containerDataSource.addContainerProperty(COLUMN_EDIT_PROPERTY_ID, Button.class, null);
        containerDataSource.addContainerProperty(COLUMN_BALANCE_PROPERTY_ID, String.class, null);
        containerDataSource.addContainerProperty(COLUMN_MAIL_PROPERTY_ID, String.class, null);
        containerDataSource.addContainerProperty(COLUMN_PRIVILEGE_PROPERTY_ID, String.class, null);
        containerDataSource.addContainerProperty(COLUMN_UUID_PROPERTY_ID, String.class, null);
        containerDataSource.addContainerProperty(COLUMN_ACTIVATED_PROPERTY_ID, Boolean.class, null);
        containerDataSource.addContainerProperty(COLUMN_CREATED_PROPERTY_ID, String.class, null);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        VerticalLayout personsContainerPanel = new VerticalLayout();
        personsContainerPanel.setSizeFull();

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidth("100%");
        toolbar.setSpacing(true);
        toolbar.setMargin(true);
        toolbar.addStyleName("toolbar");

        Label title = new Label(TITLE);
        title.addStyleName("h1");
        title.setSizeUndefined();
        toolbar.addComponent(title);
        toolbar.setComponentAlignment(title, Alignment.MIDDLE_LEFT);

        personsTable = new Table();
        personsTable.setSizeFull();
        personsTable.setSelectable(true);
        personsTable.setNullSelectionAllowed(false);
        personsTable.setImmediate(true);
        personsTable.setSortEnabled(false);
        personsTable.setContainerDataSource(containerDataSource);

        personsTable.setColumnHeader(COLUMN_EDIT_PROPERTY_ID, "Edit");
        personsTable.setColumnHeader(COLUMN_BALANCE_PROPERTY_ID, "Balance");
        personsTable.setColumnHeader(COLUMN_MAIL_PROPERTY_ID, "E-mail");
        personsTable.setColumnHeader(COLUMN_PRIVILEGE_PROPERTY_ID, "Privilege");
        personsTable.setColumnHeader(COLUMN_UUID_PROPERTY_ID, "UUID");
        personsTable.setColumnHeader(COLUMN_ACTIVATED_PROPERTY_ID, "Activated");
        personsTable.setColumnHeader(COLUMN_CREATED_PROPERTY_ID, "Signed in");

        fillTable();


        personsContainerPanel.addComponent(toolbar);
        personsContainerPanel.addComponent(personsTable);

        setContent(personsContainerPanel);
    }

    public void showPersonDialog(PersonDTO personDTO) {
        String caption = "Edit Person";
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setWidth("530px");
        dialogLayout.setMargin(true);
        dialogLayout.setSpacing(true);
        final Window editDialog = new Window(caption, dialogLayout);
        editDialog.setModal(true);
        editDialog.setResizable(false);
        editDialog.setDraggable(false);
        editDialog.addStyleName("dialog");
        editDialog.setClosable(false);

        CustomLayout editorLayout = new CustomLayout("personEditor");
        editorLayout.setSizeFull();

        PersonEditor personEditor = new PersonEditor(editorLayout);
        personEditor.setPersonDTO(personDTO);

        dialogLayout.addComponent(editorLayout);

        Button cancel = new Button("Cancel");
        cancel.addStyleName("small");
        cancel.addClickListener(event2 -> {
            editDialog.close();
        });

        final Button ok = new Button("Save");
        ok.addStyleName("default");
        ok.addStyleName("small");
        ok.addStyleName("wide");
        ok.setData(personDTO);
        ok.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (personEditor.apply()) {
                    editDialog.close();
                }
            }
        });

        ok.focus();

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSizeUndefined();
        buttonsLayout.setWidth("100%");
        buttonsLayout.addComponent(cancel);
        buttonsLayout.addComponent(ok);
        buttonsLayout.setComponentAlignment(cancel, Alignment.MIDDLE_LEFT);
        buttonsLayout.setComponentAlignment(ok, Alignment.MIDDLE_RIGHT);

        dialogLayout.addComponent(buttonsLayout);

        editDialog.addShortcutListener(new ShortcutListener("Cancel", ShortcutAction.KeyCode.ESCAPE, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                editDialog.close();
            }
        });
        editDialog.addShortcutListener(new ShortcutListener("Save", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                if (personEditor.apply()) {
                    editDialog.close();
                }
            }
        });

        getUI().addWindow(editDialog);
    }

    private void fillTable() {
        for (PersonDTO person : persons) {
            Item item = containerDataSource.addItem(person);
            if (item != null) {
                fillItem(item, person);
                personsTable.refreshRowCache();
            }
        }
    }

    private void fillItem(Item item, PersonDTO person) {
        BigDecimal balance = person.getBalance();
        String mail = person.getMail();
        String privilege = person.getPrivilege().name();
        String uuid = person.getUuid();
        Boolean activated = person.getActivated();
        Date created = person.getCreated();

        Button editButton = new NativeButton("Edit");
        editButton.addStyleName("small");
        editButton.setData(person);

        editButton.addClickListener( event -> {
            showPersonDialog((PersonDTO) event.getButton().getData());
        });

        item.getItemProperty(COLUMN_EDIT_PROPERTY_ID).setValue(editButton);
        item.getItemProperty(COLUMN_BALANCE_PROPERTY_ID).setValue(balance.toPlainString());
        item.getItemProperty(COLUMN_MAIL_PROPERTY_ID).setValue(mail);
        item.getItemProperty(COLUMN_PRIVILEGE_PROPERTY_ID).setValue(privilege);
        item.getItemProperty(COLUMN_UUID_PROPERTY_ID).setValue(uuid);
        item.getItemProperty(COLUMN_ACTIVATED_PROPERTY_ID).setValue(activated);

        // TODO: In Java8/Tomcat8 changed DateTime code and better to switch to Joda Time here !!!
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));

        // here NPE happens in Java8/Tomcat8
        item.getItemProperty(COLUMN_CREATED_PROPERTY_ID).setValue(dateFormat.format(created));
    }

    private class PersonEditor {
        private CustomLayout editorLayout;
        private TextField balanceTextField;
        private TextField emailTextField;
        private ComboBox privilegeField;
        private TextField uuidTextField;
        private CheckBox activatedField;
        private TextField signInField;

        private Label balanceErrorMessage;

        private PersonDTO personDTO;

        private PersonEditor(CustomLayout editorLayout) {
            this.editorLayout = editorLayout;
            initEditor();
        }

        public PersonDTO getPersonDTO() {
            return personDTO;
        }

        public void setPersonDTO(PersonDTO personDTO) {
            this.personDTO = personDTO;
            fillEditorFields();
        }

        private void fillEditorFields() {
            balanceTextField.setValue(personDTO.getBalance().toPlainString());

            emailTextField.setValue(personDTO.getMail());
            privilegeField.setValue(personDTO.getPrivilege());

            uuidTextField.setValue(personDTO.getUuid());
            uuidTextField.setEnabled(false);

            activatedField.setValue(personDTO.getActivated());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            signInField.setValue(dateFormat.format(personDTO.getCreated()));
            signInField.setEnabled(false);
        }

        public boolean apply() {
            clearErrors();
            if (validateForm()) {
                BigDecimal balance = new BigDecimal(balanceTextField.getValue());

                Boolean isActivated = activatedField.getValue();
                Person.Privilege privilege = (Person.Privilege) privilegeField.getValue();

                personDTO.setBalance(balance);
                personDTO.setActivated(isActivated);
                personDTO.setPrivilege(privilege);

                controller.savePerson(personDTO);
                personsTable.getItem(personDTO).getItemProperty(COLUMN_BALANCE_PROPERTY_ID).setValue(balance.toPlainString());
                personsTable.getItem(personDTO).getItemProperty(COLUMN_ACTIVATED_PROPERTY_ID).setValue(isActivated);
                personsTable.getItem(personDTO).getItemProperty(COLUMN_PRIVILEGE_PROPERTY_ID).setValue(privilege.name());

                return true;
            } else {
                return false;
            }
        }


        private void clearErrors() {
            balanceErrorMessage.setVisible(false);
        }

        //TODO: add email validation
        private boolean validateForm() {
            boolean result = true;

            try {
                new BigDecimal(balanceTextField.getValue());
            } catch (NumberFormatException e) {
                balanceErrorMessage.setVisible(true);
                result = false;
            }

            return result;
        }

        private void initEditor() {
            balanceTextField = new TextField();
            editorLayout.addComponent(balanceTextField, "balanceTextField");
            balanceTextField.setWidth("100%");

            balanceErrorMessage = new Label("Balance must be number");
            balanceErrorMessage.setVisible(false);
            editorLayout.addComponent(balanceErrorMessage, "balanceErrorMessage");

            emailTextField = new TextField();
            editorLayout.addComponent(emailTextField, "emailTextField");
            emailTextField.setWidth("100%");

            privilegeField = new ComboBox();
            privilegeField.setNullSelectionAllowed(false);
            for (Person.Privilege privilege : Person.Privilege.values()) {
                privilegeField.addItem(privilege);
            }

            editorLayout.addComponent(privilegeField, "privilegeField");
            privilegeField.setWidth("100%");

            uuidTextField = new TextField();
            editorLayout.addComponent(uuidTextField, "uuidTextField");
            uuidTextField.setWidth("100%");

            activatedField = new CheckBox();
            editorLayout.addComponent(activatedField, "activatedField");
            activatedField.setWidth("100%");

            signInField = new TextField();
            editorLayout.addComponent(signInField, "signInField");
            signInField.setWidth("100%");
        }
    }


}
