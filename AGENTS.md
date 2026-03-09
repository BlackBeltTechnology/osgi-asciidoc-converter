# OSGi AsciiDoc Converter - Project Documentation

## Project Overview


**Repository:** BlackBeltTechnology/osgi-asciidoc-converter
**License:** Apache License 2.0
**Java Version:** 21
**Build System:** Maven 3.9.4+ with Maven Wrapper (`mvnw`)

1. Wraps [AsciidoctorJ](https://github.com/asciidoctor/asciidoctorj) as an OSGi service, enabling AsciiDoc document conversion within Apache Karaf containers
2. Supports multiple output backends: PDF (with custom themes), EPUB3, HTML, and diagram rendering (PlantUML, Ditaa)
3. Uses JRuby's `OSGiScriptingContainer` for classloader-isolated Ruby runtime within the OSGi framework
4. Registers `org.asciidoctor.Asciidoctor` as an OSGi service via Declarative Services, making it injectable by any bundle
5. Packages everything as a Karaf feature for one-command deployment

## Code Instructions

1. First think through the problem, read the codebase for relevant files.
2. Before you make any major changes, check in with me and I will verify the plan.
3. Please every step of the way just give me a high level explanation of what changes you made.
4. Make every task and code change you do as simple as possible. We want to avoid making any massive or complex changes. Every change should impact as little code as possible. Everything is about simplicity.
5. Maintain a documentation file that describes how the architecture of the app works inside and out.
6. Never speculate about code you have not opened. If the user references a specific file, you MUST read the file before answering. Make sure to investigate and read relevant files BEFORE answering questions about the codebase. Never make any claims about code before investigating unless you are certain of the correct answer - give grounded and hallucination-free answers.
7. For implementation use TDD (Test-Driven Development): write or update tests first to define the expected behaviour, verify they fail, then write the minimal implementation to make them pass.
8. Use DRY (Don't Repeat Yourself): extract reusable logic into separate classes, utilities, or components. If the same pattern appears in multiple places, refactor it into a shared helper.

## Directory Structure

```
osgi-asciidoc-converter/
├── pom.xml                                    # Parent POM (aggregator + common config)
├── osgi-asciidoc-converter-api/               # Service interface (OSGi bundle)
├── osgi-asciidoc-converter-impl/              # AsciidoctorJ integration (OSGi bundle)
├── osgi-asciidoc-converter-karaf-features/    # Karaf feature descriptor
├── osgi-asciidoc-converter-itest/             # Integration tests (PAX Exam + Karaf)
├── .github/                                   # CI/CD workflows, labels, dependabot
├── .mvn/                                      # Maven wrapper + JVM config
├── openspec/                                  # Change management specs
├── logback-test.xml                           # Test logging configuration
└── LICENSE.txt                                # Apache 2.0
```

## Core Modules

### Service Layer

| Module | Type | Purpose |
|--------|------|---------|
| `osgi-asciidoc-converter-api/` | OSGi Bundle | Defines the `AsciidocConverterService` marker interface — the contract other bundles depend on |
| `osgi-asciidoc-converter-impl/` | OSGi Bundle | `AsciidoctorServiceActivator` creates an `Asciidoctor` instance via JRuby and registers it in the OSGi service registry. Embeds all AsciidoctorJ dependencies (PDF, diagrams, EPUB3) inline in the bundle JAR |

### Packaging & Testing

| Module | Type | Purpose |
|--------|------|---------|
| `osgi-asciidoc-converter-karaf-features/` | Karaf Feature | Generates `feature.xml` descriptor that bundles `scr` (prerequisite), `jruby-complete`, API, and impl for Karaf deployment |
| `osgi-asciidoc-converter-itest/` | JAR (test) | PAX Exam integration tests that spin up a real Karaf 4.4.7 container, deploy the feature, inject the `Asciidoctor` service, and verify PDF generation with custom themes |

## Technology Stack

### Core Technologies
- **AsciidoctorJ 2.5.2** — Java binding for Asciidoctor (Ruby-based document converter)
- **JRuby 9.2.19.0** — Ruby runtime for JVM; uses `OSGiScriptingContainer` for classloader isolation
- **OSGi R6** — Component model (`org.osgi.core`, `osgi.cmpn`, `org.osgi.annotation` all at 6.0.0)
- **Apache Karaf 4.4.7** — OSGi container runtime with feature-based deployment
- **Apache Felix maven-bundle-plugin 6.0.0** — Generates OSGi bundle manifests
- **Lombok 1.18.34** — `@Slf4j` and boilerplate reduction

### Build & Quality
- **Maven 3.9.4+** with wrapper (`./mvnw`); JVM configured with 2GB heap in `.mvn/jvm.config`
- **JUnit 5** (Jupiter 5.6.2) for unit tests, **JUnit 4** with **PAX Exam 4.13.5** for integration tests
- **Mockito 3.0.0** and **Hamcrest 2.1** for test assertions
- **JaCoCo 0.8.12** for code coverage (Sonar integration)
- **Flatten Maven Plugin 1.2.7** for CI-friendly `${revision}` versioning

## Build Commands

```bash
# Full build with all tests
./mvnw clean install

# Build without integration tests (faster iteration)
./mvnw clean install -pl !osgi-asciidoc-converter-itest

# Unit tests only
./mvnw clean test

# Integration tests only (requires prior install of other modules)
./mvnw clean verify -pl osgi-asciidoc-converter-itest

# Skip all tests
./mvnw clean install -DskipTests

# Custom version
./mvnw clean install -Drevision=1.2.0-SNAPSHOT
```

### Maven Profiles

| Profile | Purpose |
|---------|---------|
| `modules` | Activates all 4 submodules (default unless `-DskipModules=true`) |
| `sign-artifacts` | Signs artifacts with `simplify4u sign-maven-plugin` |
| `release-judong` | Deploys to Judo Nexus repository (`nexus.judo.technology`) |
| `release-central` | Deploys to Maven Central via OSS Sonatype |
| `generate-github-asciidoc-diagrams` | Generates PNG diagrams from AsciiDoc sources in `.github/` |
| `update-source-code-license` | Updates Apache v2 license headers on all source files |
| `verify-feature` | Validates Karaf feature descriptor (default in karaf-features module) |

## Key Configuration Files

| File | Purpose |
|------|---------|
| `pom.xml` | Parent POM — dependency versions, plugin management, profiles |
| `.mvn/jvm.config` | JVM heap settings for Maven (`-Xms1024m -Xmx2048m`) |
| `.mvn/extensions.xml` | Maven extensions (wagon, buildtime, profile-activator) |
| `logback-test.xml` | Logback configuration for test execution |
| `osgi-asciidoc-converter-karaf-features/src/main/feature/feature.xml` | Karaf feature descriptor template (filtered with Maven properties) |
| `osgi-asciidoc-converter-itest/src/test/resources/etc/` | Karaf test container configs (logging, Maven URL) |
| `.github/workflows/build.yml` | Main CI/CD pipeline (build, deploy, tag, release) |

## Development Environment

**Required:**
- Java 21 JDK
- Maven 3.9.4+ (or use the included `./mvnw` wrapper)
- Network access to Maven Central and Judo Nexus for dependency resolution

**Architecture awareness:**
- The impl bundle **embeds** all AsciidoctorJ JARs inline — changes to AsciidoctorJ dependencies require updating the `<Embed-Dependency>` and `<Export-Package>` configuration in the impl module's `pom.xml`
- Integration tests spawn a full Karaf container with dynamic port allocation — they require significant memory and time
- Java 21 module system flags (`--add-reads`, `--add-exports`, `--add-opens`) are configured in the itest POM for PAX Exam compatibility

## Git Workflow

- **Main Branch:** `develop`
- **Versioning:** `1.1.0-SNAPSHOT` using `${revision}` property with flatten-maven-plugin
- **Branch naming:** `feature/JNG-NUMBER_summary`, `bugfix/JNG-NUMBER_summary`, `release/X.Y.Z`
- **Commit rule:** Every commit must reference a JIRA ticket (`JNG-xxx`)
- **CI:** GitHub Actions on `judong` runner — builds, deploys to Nexus, creates tags and releases

## Important Notes

1. The `AsciidocConverterService` interface in the API module is currently an **empty marker interface** — the actual service registered is `org.asciidoctor.Asciidoctor`
2. The impl bundle uses `@Component(immediate = true)` — the Asciidoctor service activates as soon as the bundle starts, without waiting for consumer demand
3. JRuby's `OSGiScriptingContainer` must be properly terminated on deactivation to avoid resource leaks — the activator handles this in `@Deactivate`
4. All AsciidoctorJ sub-libraries are **inlined** into the impl bundle JAR (not separate bundles), so the bundle is large but self-contained
5. The Karaf feature requires the `scr` feature as a prerequisite — without Service Component Runtime, the `@Component` annotation won't be processed

## Related Documentation

- [CONTRIBUTING.md](CONTRIBUTING.md) — Development setup and submission guidelines
- [.github/CIFLOW.md](.github/CIFLOW.md) — Branching strategy, version numbering, and CI/CD workflow details
- [LICENSE.txt](LICENSE.txt) — Apache License 2.0
