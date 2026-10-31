package com.fxmind.web.ui;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.lumo.Lumo;

/** Application shell: Lumo theme (dark variant) and PWA metadata for the admin console. */
@StyleSheet(Lumo.STYLESHEET)
@StyleSheet(Lumo.UTILITY_STYLESHEET)
@PWA(name = "FXMind Console", shortName = "FXMind")
public class AppShell implements AppShellConfigurator {}
