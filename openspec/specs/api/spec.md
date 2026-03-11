# api Specification

## Purpose
Defines the service contract interface (`AsciidocConverterService`) that other OSGi bundles depend on for AsciiDoc conversion capabilities. Currently serves as a marker interface.

## Architecture
- **Package:** `hu.blackbelt.asciidoc.converter.api`
- **Key type:** `AsciidocConverterService` — empty marker interface
- **Packaging:** OSGi bundle via Apache Felix maven-bundle-plugin
- **Consumers:** The impl module implements/activates the service; downstream bundles can depend on this API module for type-safe service lookups

## Requirements

### Requirement: Service contract interface SHALL be available as an OSGi-exported package
The API module SHALL export the `hu.blackbelt.asciidoc.converter.api` package so that other bundles can import and reference the `AsciidocConverterService` type.

#### Scenario: Bundle resolves API package
- **GIVEN** the `osgi-asciidoc-converter-api` bundle is installed in a Karaf container
- **WHEN** another bundle declares an `Import-Package` for `hu.blackbelt.asciidoc.converter.api`
- **THEN** the OSGi resolver SHALL successfully wire the import to this bundle's export

### Requirement: API module SHALL have no implementation dependencies
The API module SHALL depend only on the Java standard library and OSGi framework — no implementation-specific libraries (AsciidoctorJ, JRuby, etc.).

#### Scenario: Clean dependency tree
- **GIVEN** the `osgi-asciidoc-converter-api` module's `pom.xml`
- **WHEN** the dependency tree is analyzed
- **THEN** it SHALL contain no compile-scope dependencies beyond those inherited from the parent POM's common dependencies (SLF4J, Lombok, Guava)
