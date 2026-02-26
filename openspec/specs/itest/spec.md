# itest Specification

## Purpose
Integration tests that validate the AsciiDoc converter works correctly inside a real Apache Karaf container, using PAX Exam to provision the container, deploy the feature, and inject OSGi services.

## Architecture
- **Package:** `hu.blackbelt.asciidoc.converter`
- **Test class:** `AsciidocConverterITest` — `@RunWith(PaxExam.class)` with `@ExamReactorStrategy(PerClass.class)`
- **Helper class:** `KarafFeatureProvider` — static utilities for Karaf container configuration (port allocation, VM options, config file injection, service lookup with timeout)
- **Container:** Apache Karaf 4.4.7, provisioned by PAX Exam with dynamic port allocation
- **Service timeout:** 30 seconds for OSGi service availability
- **Test resources:** `content.adoc` (sample document), `theme.yaml` (PDF theme), Karaf configs in `etc/`

## Requirements

### Requirement: Asciidoctor service SHALL be injectable in the test container
The PAX Exam test SHALL successfully inject the `org.asciidoctor.Asciidoctor` service after the `asciidoc-converter` Karaf feature is deployed.

#### Scenario: Service injection
- **GIVEN** a Karaf 4.4.7 container provisioned with the `asciidoc-converter` feature
- **WHEN** the `AsciidocConverterITest` class is instantiated by PAX Exam
- **THEN** the `@Inject Asciidoctor asciidoctor` field SHALL be non-null within the 30-second service timeout

### Requirement: PDF conversion SHALL produce output with custom themes
The integration test SHALL convert an AsciiDoc document to PDF using a custom theme and verify the conversion completes without errors.

#### Scenario: PDF generation with custom theme
- **GIVEN** the Asciidoctor service is injected and active
- **AND** a `theme.yaml` is available as a test resource
- **AND** a `content.adoc` sample document is available
- **WHEN** `asciidoctor.convert()` is called with `backend("pdf")`, custom `pdf-stylesdir`, and `pdf-style("custom")`
- **THEN** the conversion SHALL complete without throwing an `IOException`
- **AND** a PDF output SHALL be written to the configured output stream

### Requirement: Karaf container SHALL be configured for Java 21 compatibility
The test container SHALL include Java module system flags (`--add-reads`, `--add-exports`, `--add-opens`) to ensure PAX Exam and OSGi operate correctly under Java 21.

#### Scenario: Module system compatibility
- **GIVEN** the test is running on Java 21
- **WHEN** the Karaf container VM options are configured by `KarafFeatureProvider.configureVmOptions()`
- **THEN** the necessary `--add-reads`, `--add-exports`, and `--add-opens` flags SHALL be set for `java.base`, `java.xml`, and `java.rmi` modules
