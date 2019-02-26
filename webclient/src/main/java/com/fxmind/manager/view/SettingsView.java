package com.fxmind.manager.view;

import com.fxmind.entity.Settings;
import com.fxmind.service.AdminService;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import ru.xpoft.vaadin.VaadinView;

import java.util.List;

/**
 *
 * This view edits Site settings values.
 * Add/Delete new settings commented, because properties control application business logic. And now can be added only manually to DB.
 */
@SuppressWarnings("serial")
@org.springframework.stereotype.Component
@Scope("prototype")
@VaadinView(SettingsView.VIEW_NAME)
public class SettingsView extends VerticalLayout implements View {
    public static final String VIEW_NAME = "settings";

    private static final String COL_PROP_NAME = "Name";
    private static final String COL_PROP_VALUE = "Value";
    private static final String COL_EDIT = "Edit";
    private static final String COL_DELETE = "Delete";

    private static final long serialVersionUID = 1L;

    @Autowired
    private AdminService adminService;

    protected Table propTable;

    protected void InitPropsTable() {
        propTable = new Table();
        propTable.setCaption("Settings");
        propTable.setSizeFull();
        propTable.setNullSelectionAllowed(false);
        propTable.setImmediate(true);
        //propTable.addStyleName("plain");
        propTable.addStyleName("borderless");
        propTable.setSelectable(true);
        propTable.setColumnCollapsingAllowed(true);
        propTable.setColumnReorderingAllowed(true);
        propTable.setFooterVisible(false);
        propTable.setMultiSelect(false);

        propTable.setPageLength(0);
        //propTable.setRowHeaderMode(Table.RowHeaderMode.INDEX);

        propTable.addContainerProperty(COL_EDIT, Button.class, null, COL_EDIT, null, Table.Align.CENTER);
        //propTable.addContainerProperty(COL_DELETE, Button.class, null, COL_DELETE, null, Table.Align.CENTER);
        propTable.addContainerProperty(COL_PROP_NAME, String.class, null, COL_PROP_NAME, null, Table.Align.LEFT);
        propTable.addContainerProperty(COL_PROP_VALUE, String.class, null, COL_PROP_VALUE, null, Table.Align.LEFT);
    }

    public void LoadProps()
    {
        propTable.removeAllItems();

        List<Settings> listset = adminService.getAllProperties();

        for ( Settings set: listset) {

            Button editButton = new NativeButton(COL_EDIT);
            editButton.addStyleName("small");
            editButton.setData(set);

            editButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                showSettingDialog((Settings) event.getButton().getData());
                }
            });

            //Button deleteButton = new NativeButton(COL_DELETE);
            //deleteButton.addStyleName("small");
            //deleteButton.setData(set);

            //deleteButton.addClickListener(new Button.ClickListener() {
            //    @Override
            //    public void buttonClick(Button.ClickEvent event) {
            //       DeleteSetting((Settings)event.getButton().getData());
            //    }
            //});

            propTable.addItem(new Object[]{ editButton, /*deleteButton,*/ set.getName(), set.getValue()}, set);
        }
        propTable.sort(new Object[]{COL_PROP_NAME}, new boolean[]{true});
        propTable.setColumnWidth(COL_PROP_NAME, 250);
        propTable.setColumnWidth(COL_EDIT, 100);
        //propTable.setColumnWidth(COL_DELETE, 100);
        propTable.setWidth("100%");
    }

    @Override
    public void enter(ViewChangeEvent event) {

        setSizeFull();
        addStyleName("dashboard-view");

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidth("100%");
        toolbar.setSpacing(true);
        toolbar.setMargin(true);
        toolbar.addStyleName("toolbar");
        addComponent(toolbar);

        Label title = new Label("Site Settings");
        title.addStyleName("h1");
        title.setSizeUndefined();
        toolbar.addComponent(title);
        toolbar.setComponentAlignment(title, Alignment.MIDDLE_LEFT);

        //Button addnew = new Button("Add New Setting");
        //addnew.addClickListener(new Button.ClickListener() {
        //    @Override
        //    public void buttonClick(Button.ClickEvent event) {
        //        showSettingDialog(null);
        //    }
        //});
        //addnew.addStyleName("h1");
        //toolbar.addComponent(addnew);
        //toolbar.setComponentAlignment(addnew, Alignment.MIDDLE_LEFT);
        HorizontalLayout rowRunning = new HorizontalLayout();
        rowRunning.setSizeFull();
        //rowRunning.setMargin(new MarginInfo(true, true, false, true));
        rowRunning.setSpacing(true);
        addComponent(rowRunning);
        setExpandRatio(rowRunning, 1.0f);

        InitPropsTable();
        LoadProps();
        rowRunning.addComponent(createPanel(propTable));

    }

    public void showSettingDialog(Settings set) {
        String caption = "Edit Setting";
        //boolean bNew = false;
        //if (set == null ) {
        //    caption = "New Setting";
        //    bNew = true;
        //    set = new Settings();
        //}
        VerticalLayout l = new VerticalLayout();
        l.setWidth("400px");
        l.setMargin(true);
        l.setSpacing(true);
        final Window editDialog = new Window(caption, l);
        editDialog.setModal(true);
        editDialog.setResizable(false);
        editDialog.setDraggable(false);
        editDialog.addStyleName("dialog");
        editDialog.setClosable(false);

        Label message = new Label("Name: " + set.getName());
        l.addComponent(message);

        final TextField value = new TextField("Value: ");
        value.setValue(set.getValue());
        value.focus();
        value.selectAll();
        value.setWidth("370px");
        l.addComponent(value);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setWidth("100%");
        buttons.setSpacing(true);
        l.addComponent(buttons);

        Button cancel = new Button("Cancel");
        cancel.addStyleName("small");
        cancel.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                editDialog.close();
            }
        });
        buttons.addComponent(cancel);

        final Button ok = new Button("Save");
        ok.addStyleName("default");
        ok.addStyleName("small");
        ok.addStyleName("wide");
        ok.setData(set);
        ok.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Settings set0 = (Settings) event.getButton().getData();
                set0.setValue(value.getValue());
                saveSetting(set0);
                editDialog.close();
                LoadProps();
            }
        });
        buttons.addComponent(ok);
        ok.focus();

        Button resetToDefault = new Button("Reset Default");
        resetToDefault.addStyleName("small");
        resetToDefault.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                adminService.resetSettingToDefaultValue((Settings) ok.getData());
                editDialog.close();
                LoadProps();
            }
        });
        buttons.addComponent(resetToDefault);

        editDialog.addShortcutListener(new ShortcutListener("Cancel", ShortcutAction.KeyCode.ESCAPE, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                editDialog.close();
            }
        });
        editDialog.addShortcutListener(new ShortcutListener("Save", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                Settings set0 = (Settings)ok.getData();
                set0.setValue(value.getValue());
                saveSetting(set0);
                editDialog.close();
                LoadProps();
            }
        });

        getUI().addWindow(editDialog);

        LoadProps();
    }

    public void saveSetting(Settings settings) {
        if (settings != null){
            adminService.saveProperty(settings);
        }
    }

    private CssLayout createPanel(Component content) {
        CssLayout panel = new CssLayout();
        panel.addStyleName("layout-panel");
        panel.setSizeFull();
        panel.addComponent(content);
        return panel;
    }

}
