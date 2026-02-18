package com.musicshop.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Map;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.musicshop", importOptions = ImportOption.DoNotIncludeTests.class)
class LayeredArchitectureTest {

    @ArchTest
    static final ArchRule layered_dependencies = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Controller").definedBy("com.musicshop.controller..")
            .layer("Application").definedBy("com.musicshop.application..")
            .layer("Service").definedBy("com.musicshop.service..")
            .layer("Repository").definedBy("com.musicshop.repository..")
            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Controller")
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Application", "Service")
            .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service");

    @ArchTest
    static final ArchRule controllers_must_not_access_repositories_directly = noClasses()
            .that().resideInAPackage("com.musicshop.controller..")
            .should().dependOnClassesThat().resideInAPackage("com.musicshop.repository..");

    @ArchTest
    static final ArchRule controllers_must_not_depend_on_services = noClasses()
            .that().resideInAPackage("com.musicshop.controller..")
            .should().dependOnClassesThat().resideInAPackage("com.musicshop.service..");

    @ArchTest
    static final ArchRule controllers_must_not_depend_on_model = noClasses()
            .that().resideInAPackage("com.musicshop.controller..")
            .should().dependOnClassesThat().resideInAPackage("com.musicshop.model..");

    @ArchTest
    static final ArchRule services_must_not_depend_on_controllers = noClasses()
            .that().resideInAPackage("com.musicshop.service..")
            .should().dependOnClassesThat().resideInAPackage("com.musicshop.controller..");

    @ArchTest
    static final ArchRule services_must_not_depend_on_application = noClasses()
            .that().resideInAPackage("com.musicshop.service..")
            .should().dependOnClassesThat().resideInAPackage("com.musicshop.application..");

    @ArchTest
    static final ArchRule repositories_must_not_depend_on_service_or_controller = noClasses()
            .that().resideInAPackage("com.musicshop.repository..")
            .should().dependOnClassesThat().resideInAnyPackage("com.musicshop.service..", "com.musicshop.controller..");

    @ArchTest
    static final ArchRule dto_must_not_depend_on_service_controller_or_repository = noClasses()
            .that().resideInAPackage("com.musicshop.dto..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "com.musicshop.service..",
                    "com.musicshop.controller..",
                    "com.musicshop.repository.."
            );

    @ArchTest
    static final ArchRule controllers_should_not_expose_model_types_in_signatures = methods()
            .that().areDeclaredInClassesThat().resideInAPackage("com.musicshop.controller..")
            .should(notUseModelTypesInSignature());

    @ArchTest
    static final ArchRule transactional_class_annotations_should_be_limited_to_app_and_service_layers = noClasses()
            .that().areNotAssignableTo(Throwable.class)
            .and().resideOutsideOfPackages(
                    "com.musicshop.service..",
                    "com.musicshop.application..",
                    "com.musicshop.data.seeder.."
            )
            .should().beAnnotatedWith(Transactional.class);

    @ArchTest
    static final ArchRule transactional_method_annotations_should_be_limited_to_app_and_service_layers = methods()
            .that().areAnnotatedWith(Transactional.class)
            .should().beDeclaredInClassesThat().resideInAnyPackage(
                    "com.musicshop.service..",
                    "com.musicshop.application..",
                    "com.musicshop.data.seeder.."
            );

    @ArchTest
    static final ArchRule no_cycles_in_core_packages = SlicesRuleDefinition.slices()
            .matching("com.musicshop.(*)..")
            .should().beFreeOfCycles();

    @ArchTest
    static final ArchRule mutating_controller_methods_should_be_preauthorized = methods()
            .that().areDeclaredInClassesThat().resideInAPackage("com.musicshop.controller..")
            .and().areDeclaredInClassesThat().resideOutsideOfPackage("com.musicshop.controller.auth..")
            .should(bePreAuthorizedIfMutating());

    @ArchTest
    static final ArchRule post_put_request_bodies_should_be_validated = methods()
            .that().areDeclaredInClassesThat().resideInAPackage("com.musicshop.controller..")
            .and().areDeclaredInClassesThat().resideOutsideOfPackage("com.musicshop.controller.auth..")
            .should(haveValidOnPostPutRequestBodyParameters());

    @ArchTest
    static final ArchRule sensitive_id_endpoints_should_use_access_guard = methods()
            .that().areDeclaredInClassesThat().resideInAPackage("com.musicshop.controller..")
            .should(requireOwnershipGuardForSensitivePathVariables());

    @ArchTest
    static final ArchRule controllers_should_not_build_error_response_entities_directly = methods()
            .that().areDeclaredInClassesThat().resideInAPackage("com.musicshop.controller..")
            .should(notBuildErrorResponseEntitiesDirectly());

    @ArchTest
    static final ArchRule controllers_should_not_use_wildcard_response_entities = methods()
            .that().areDeclaredInClassesThat().resideInAPackage("com.musicshop.controller..")
            .should(notUseWildcardResponseEntityTypes());

    @ArchTest
    static final ArchRule request_bodies_should_not_use_untyped_map_or_object = methods()
            .that().areDeclaredInClassesThat().resideInAPackage("com.musicshop.controller..")
            .should(notUseUntypedRequestBodyContracts());

    private static ArchCondition<JavaMethod> notUseModelTypesInSignature() {
        return new ArchCondition<>("not use com.musicshop.model types in parameter or return signatures") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                JavaClass returnType = method.getRawReturnType();
                if (returnType.getPackageName().startsWith("com.musicshop.model")) {
                    events.add(SimpleConditionEvent.violated(
                            method,
                            method.getFullName() + " has model return type: " + returnType.getName()
                    ));
                }

                String fullReturnType = method.getReturnType().getName();
                if (fullReturnType.contains("com.musicshop.model.")) {
                    events.add(SimpleConditionEvent.violated(
                            method,
                            method.getFullName() + " has model type in return signature: " + fullReturnType
                    ));
                }

                method.getRawParameterTypes().forEach(parameterType -> {
                    if (parameterType.getPackageName().startsWith("com.musicshop.model")) {
                        events.add(SimpleConditionEvent.violated(
                                method,
                                method.getFullName() + " has model parameter type: " + parameterType.getName()
                        ));
                    }
                });

                method.getParameterTypes().forEach(parameterType -> {
                    String fullParameterType = parameterType.getName();
                    if (fullParameterType.contains("com.musicshop.model.")) {
                        events.add(SimpleConditionEvent.violated(
                                method,
                                method.getFullName() + " has model type in parameter signature: " + fullParameterType
                        ));
                    }
                });
            }
        };
    }

    private static ArchCondition<JavaMethod> bePreAuthorizedIfMutating() {
        return new ArchCondition<>("have @PreAuthorize when using mutating mapping annotations") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                boolean mutating = method.isAnnotatedWith(PostMapping.class)
                        || method.isAnnotatedWith(PutMapping.class)
                        || method.isAnnotatedWith(PatchMapping.class)
                        || method.isAnnotatedWith(DeleteMapping.class);

                if (mutating && !method.isAnnotatedWith(PreAuthorize.class)) {
                    events.add(SimpleConditionEvent.violated(
                            method,
                            method.getFullName() + " is mutating and must declare @PreAuthorize"
                    ));
                }
            }
        };
    }

    private static ArchCondition<JavaMethod> haveValidOnPostPutRequestBodyParameters() {
        return new ArchCondition<>("annotate @RequestBody parameters with @Valid on POST/PUT methods") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                boolean postOrPut = method.isAnnotatedWith(PostMapping.class) || method.isAnnotatedWith(PutMapping.class);
                if (!postOrPut) {
                    return;
                }

                method.getParameters().forEach(parameter -> {
                    boolean requestBody = parameter.isAnnotatedWith(RequestBody.class);
                    boolean hasValid = parameter.isAnnotatedWith(Valid.class);

                    if (requestBody && !hasValid) {
                        events.add(SimpleConditionEvent.violated(
                                method,
                                method.getFullName() + " has @RequestBody parameter without @Valid on POST/PUT"
                        ));
                    }
                });
            }
        };
    }

    private static ArchCondition<JavaMethod> requireOwnershipGuardForSensitivePathVariables() {
        return new ArchCondition<>("use @PreAuthorize with @accessGuard.canAccess for sensitive path variables") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                if (!hasSensitivePathVariable(method)) {
                    return;
                }

                if (!method.isAnnotatedWith(PreAuthorize.class)) {
                    events.add(SimpleConditionEvent.violated(
                            method,
                            method.getFullName() + " has sensitive path variable and must declare @PreAuthorize"
                    ));
                    return;
                }

                String expression = method.getAnnotationOfType(PreAuthorize.class).value();
                if (!expression.contains("@accessGuard.canAccess")) {
                    events.add(SimpleConditionEvent.violated(
                            method,
                            method.getFullName() + " must use accessGuard ownership check in @PreAuthorize"
                    ));
                }
            }
        };
    }

    private static boolean hasSensitivePathVariable(JavaMethod method) {
        return method.getAnnotations().stream()
                .map(Object::toString)
                .anyMatch(annotationDescription ->
                        annotationDescription.contains("{userId}")
                                || annotationDescription.contains("{detailId}")
                                || annotationDescription.contains("{notificationId}"));
    }

    private static ArchCondition<JavaMethod> notBuildErrorResponseEntitiesDirectly() {
        return new ArchCondition<>("not build error ResponseEntity directly in controllers") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                for (JavaMethodCall call : method.getMethodCallsFromSelf()) {
                    String ownerName = call.getTarget().getOwner().getName();
                    if (!"org.springframework.http.ResponseEntity".equals(ownerName)) {
                        continue;
                    }

                    String calledMethod = call.getTarget().getName();
                    boolean errorBuilderCall = "notFound".equals(calledMethod) || "badRequest".equals(calledMethod);
                    if (!errorBuilderCall && "status".equals(calledMethod)) {
                        String description = call.getDescription();
                        errorBuilderCall = description.contains("HttpStatus.BAD_REQUEST")
                                || description.contains("HttpStatus.NOT_FOUND")
                                || description.contains("HttpStatus.UNAUTHORIZED")
                                || description.contains("HttpStatus.FORBIDDEN")
                                || description.contains("HttpStatus.CONFLICT")
                                || description.contains("HttpStatus.PAYMENT_REQUIRED")
                                || description.contains("HttpStatus.INTERNAL_SERVER_ERROR");
                    }

                    if (errorBuilderCall) {
                        events.add(SimpleConditionEvent.violated(
                                method,
                                method.getFullName()
                                        + " builds error ResponseEntity directly; throw exceptions and let GlobalExceptionHandler map errors"
                        ));
                    }
                }
            }
        };
    }

    private static ArchCondition<JavaMethod> notUseWildcardResponseEntityTypes() {
        return new ArchCondition<>("not use wildcard ResponseEntity return types") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                String returnType = method.getReturnType().getName();
                if (returnType.equals("org.springframework.http.ResponseEntity<?>")
                        || returnType.contains("org.springframework.http.ResponseEntity<?>")) {
                    events.add(SimpleConditionEvent.violated(
                            method,
                            method.getFullName() + " uses wildcard ResponseEntity<?> return type"
                    ));
                }
            }
        };
    }

    private static ArchCondition<JavaMethod> notUseUntypedRequestBodyContracts() {
        return new ArchCondition<>("not use untyped @RequestBody payloads like Map or Object") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                method.getParameters().forEach(parameter -> {
                    if (!parameter.isAnnotatedWith(RequestBody.class)) {
                        return;
                    }

                    JavaClass rawType = parameter.getRawType();
                    String fullType = parameter.getType().getName();
                    boolean rawObject = "java.lang.Object".equals(rawType.getName());
                    boolean rawMap = Map.class.getName().equals(rawType.getName()) || fullType.startsWith("java.util.Map<");

                    if (rawObject || rawMap) {
                        events.add(SimpleConditionEvent.violated(
                                method,
                                method.getFullName() + " uses untyped @RequestBody contract: " + fullType
                        ));
                    }
                });
            }
        };
    }
}
