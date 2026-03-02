# impl Specification

## Purpose
Provides the OSGi Declarative Services component that bootstraps AsciidoctorJ with a JRuby runtime and registers an `org.asciidoctor.Asciidoctor` instance as an OSGi service. All AsciidoctorJ dependencies (PDF, diagrams, EPUB3) are embedded inline in the bundle.

## Architecture
- **Package:** `hu.blackbelt.asciidoc.converter.impl`
- **Key class:** `AsciidoctorServiceActivator` — `@Component(immediate = true)` DS component
- **Service registered:** `org.asciidoctor.Asciidoctor` (the AsciidoctorJ API)
- **JRuby integration:** `org.jruby.embed.osgi.OSGiScriptingContainer` provides classloader-isolated Ruby runtime
- **Bundle packaging:** Embeds `asciidoctorj`, `asciidoctorj-api`, `asciidoctorj-pdf`, `asciidoctorj-diagram`, `asciidoctorj-diagram-plantuml`, `asciidoctorj-diagram-ditaamini`, `asciidoctorj-epub3` inline
- **Exported packages:** `hu.blackbelt.asciidoc.converter`, `org.asciidoctor`, `org.asciidoctor.log`, `org.asciidoctor.ast`, `org.asciidoctor.extension`, `org.asciidoctor.syntaxhighlighter`, `org.asciidoctor.converter`

## Requirements

### Requirement: Asciidoctor service SHALL be registered on bundle activation
The `AsciidoctorServiceActivator` SHALL create an `Asciidoctor` instance and register it in the OSGi service registry when the bundle activates.

#### Scenario: Service registration on startup
- **GIVEN** the `osgi-asciidoc-converter-impl` bundle is installed in a Karaf container with SCR active
- **WHEN** the bundle transitions to the ACTIVE state
- **THEN** an `org.asciidoctor.Asciidoctor` service SHALL be registered in the OSGi service registry
- **AND** the Asciidoctor version SHALL be logged at INFO level

### Requirement: JRuby container SHALL use OSGi-aware classloader
The activator SHALL create an `OSGiScriptingContainer` using the bundle's own classloader to ensure JRuby can access the embedded AsciidoctorJ Ruby scripts.

#### Scenario: JRuby classloader isolation
- **GIVEN** the impl bundle is activating
- **WHEN** the `OSGiScriptingContainer` is created
- **THEN** it SHALL be initialized with `bundleContext.getBundle()` as the classloader source
- **AND** the Asciidoctor factory SHALL use `container.getOSGiBundleClassLoader()`

### Requirement: Resources SHALL be cleaned up on deactivation
The activator SHALL unregister the Asciidoctor service and terminate the JRuby scripting container when the bundle deactivates.

#### Scenario: Clean shutdown
- **GIVEN** the `Asciidoctor` service is registered and the `OSGiScriptingContainer` is running
- **WHEN** the bundle is stopped or uninstalled
- **THEN** `serviceRegistration.unregister()` SHALL be called
- **AND** `container.terminate()` SHALL be called to release JRuby resources

#### Scenario: Null-safe deactivation
- **GIVEN** the bundle activation failed or was incomplete
- **WHEN** `deactivate()` is called
- **THEN** it SHALL check for null before calling `unregister()` or `terminate()`

### Requirement: Bundle SHALL embed all AsciidoctorJ dependencies
The impl bundle SHALL inline all AsciidoctorJ libraries so that no additional bundles are required for document conversion (except JRuby).

#### Scenario: Self-contained bundle
- **GIVEN** the impl bundle JAR
- **WHEN** its contents are inspected
- **THEN** it SHALL contain classes from `asciidoctorj`, `asciidoctorj-api`, `asciidoctorj-pdf`, `asciidoctorj-diagram`, `asciidoctorj-diagram-plantuml`, `asciidoctorj-diagram-ditaamini`, and `asciidoctorj-epub3`

### Requirement: Bundle SHALL re-export Asciidoctor public APIs
The impl bundle SHALL export `org.asciidoctor` and its sub-packages so that consuming bundles can use the Asciidoctor API without directly depending on AsciidoctorJ JARs.

#### Scenario: API accessibility
- **GIVEN** the impl bundle is installed
- **WHEN** another bundle imports `org.asciidoctor`
- **THEN** it SHALL resolve against the impl bundle's exported packages
- **AND** classes like `Asciidoctor`, `Options`, `Attributes` SHALL be accessible
