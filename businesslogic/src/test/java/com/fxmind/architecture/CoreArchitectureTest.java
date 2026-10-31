package com.fxmind.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.beans.factory.annotation.Autowired;

@AnalyzeClasses(packages = "com.fxmind", importOptions = ImportOption.DoNotIncludeTests.class)
class CoreArchitectureTest {

    @ArchTest
    static final ArchRule domainIsPersistenceIgnorantOfServices = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..service..", "..repository..", "..provider..", "..thrift..");

    @ArchTest
    static final ArchRule thriftAdapterUsesOnlyThePort = noClasses()
            .that()
            .resideInAPackage("..thrift..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..service..", "..repository..");

    @ArchTest
    static final ArchRule providersDoNotCallServices = noClasses()
            .that()
            .resideInAPackage("..provider..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..service..");

    @ArchTest
    static final ArchRule noFieldInjection = noFields()
            .should()
            .beAnnotatedWith(Autowired.class)
            .because("dependencies are injected through constructors");

    @ArchTest
    static final ArchRule noCycles =
            slices().matching("com.fxmind.(*)..").should().beFreeOfCycles();
}
