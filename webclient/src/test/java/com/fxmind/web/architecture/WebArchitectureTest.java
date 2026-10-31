package com.fxmind.web.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

/** Layering rules spanning both modules: web depends on core, never the other way round. */
@AnalyzeClasses(packages = "com.fxmind", importOptions = ImportOption.DoNotIncludeTests.class)
class WebArchitectureTest {

    @ArchTest
    static final ArchRule coreDoesNotDependOnWeb = noClasses()
            .that()
            .resideOutsideOfPackage("com.fxmind.web..")
            .and()
            .doNotHaveSimpleName("FxMindApplication")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("com.fxmind.web..");

    @ArchTest
    static final ArchRule viewsDoNotTouchPersistence = noClasses()
            .that()
            .resideInAPackage("com.fxmind.web.ui..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("com.fxmind.repository..");

    @ArchTest
    static final ArchRule everyRouteDeclaresAccess = classes()
            .that()
            .areAnnotatedWith(Route.class)
            .should()
            .beAnnotatedWith(RolesAllowed.class)
            .orShould()
            .beAnnotatedWith(AnonymousAllowed.class);

    @ArchTest
    static final ArchRule noFieldInjection = noFields()
            .should()
            .beAnnotatedWith(Autowired.class)
            .because("dependencies are injected through constructors");
}
