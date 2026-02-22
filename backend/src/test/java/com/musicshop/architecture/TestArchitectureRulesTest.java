package com.musicshop.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.musicshop")
class TestArchitectureRulesTest {

    @ArchTest
    static final ArchRule unit_tests_should_mirror_production_package_and_name = classes()
            .that().resideInAPackage("com.musicshop..")
            .and().haveSimpleNameEndingWith("Test")
            .and().haveSimpleNameNotEndingWith("IntegrationTest")
            .and().haveSimpleNameNotContaining("Contract")
            .and().resideOutsideOfPackages("com.musicshop.architecture..", "com.musicshop.openapi..")
            .should(haveMirroredProductionClass());

    private static ArchCondition<JavaClass> haveMirroredProductionClass() {
        return new ArchCondition<>("have matching production class in same package") {
            @Override
            public void check(JavaClass testClass, ConditionEvents events) {
                String simpleName = testClass.getSimpleName();
                String productionSimpleName = simpleName.substring(0, simpleName.length() - "Test".length());
                String expectedProductionName = testClass.getPackageName() + "." + productionSimpleName;

                try {
                    Class.forName(expectedProductionName);
                } catch (ClassNotFoundException e) {
                    events.add(SimpleConditionEvent.violated(
                            testClass,
                            testClass.getName() + " does not mirror a production class named " + expectedProductionName
                    ));
                }
            }
        };
    }
}
