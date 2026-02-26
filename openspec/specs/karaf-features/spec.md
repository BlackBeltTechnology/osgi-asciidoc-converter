# karaf-features Specification

## Purpose
Generates a Karaf feature descriptor (`feature.xml`) that packages the AsciiDoc converter bundles for one-command deployment into an Apache Karaf container.

## Architecture
- **Feature name:** `asciidoc-converter`
- **Feature file:** `src/main/feature/feature.xml` (Maven-filtered)
- **Packaging:** `feature` type via `karaf-maven-plugin`
- **Contents:** prerequisite `scr` feature + three bundles (jruby-complete, API, impl)
- **Validation:** Feature descriptor is verified during build via the `verify-feature` profile

## Requirements

### Requirement: Feature SHALL declare SCR as a prerequisite
The `asciidoc-converter` feature SHALL require the `scr` (Service Component Runtime) feature as a prerequisite, ensuring Declarative Services is available before the converter bundles are loaded.

#### Scenario: SCR prerequisite
- **GIVEN** the generated `feature.xml`
- **WHEN** Karaf resolves the `asciidoc-converter` feature
- **THEN** it SHALL first install and start the `scr` feature
- **AND** only then proceed to install the converter bundles

### Requirement: Feature SHALL include all required bundles
The feature SHALL bundle `jruby-complete`, `osgi-asciidoc-converter-api`, and `osgi-asciidoc-converter-impl` with correct Maven coordinates and versions.

#### Scenario: Complete bundle list
- **GIVEN** the `asciidoc-converter` feature is installed in Karaf
- **WHEN** the bundle list is inspected
- **THEN** `org.jruby:jruby-complete:${jruby-version}` SHALL be present
- **AND** `hu.blackbelt:osgi-asciidoc-converter-api:${project.version}` SHALL be present
- **AND** `hu.blackbelt:osgi-asciidoc-converter-impl:${project.version}` SHALL be present

### Requirement: Feature descriptor SHALL pass Karaf verification
The generated feature descriptor SHALL pass the `karaf-maven-plugin:verify` goal, confirming all bundle dependencies can be resolved.

#### Scenario: Feature verification
- **GIVEN** the feature.xml is generated from the template with resolved Maven properties
- **WHEN** `mvn verify` runs with the `verify-feature` profile
- **THEN** the Karaf feature verifier SHALL report no unresolvable imports or missing dependencies
