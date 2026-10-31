# Contributing to FxHubJava

Thanks for your interest in improving FxHubJava!

## Prerequisites

* JDK 25 (Temurin recommended)
* Docker (for the Testcontainers MySQL tests and the Docker image)

The Maven Wrapper (`./mvnw`) is bundled, so you don't need to install Maven.

## Workflow

1. Fork the repository and create a feature branch from `master`.
2. Make your change and add or update tests.
3. Run the full build locally. It runs the tests, ArchUnit rules, JaCoCo, Spotless and Maven Enforcer, just like CI:

   ```bash
   ./mvnw verify
   ```

   If Spotless complains, fix the formatting with:

   ```bash
   ./mvnw spotless:apply
   ```

4. Open a pull request against `master` that explains what you changed and why.

## Guidelines

* Follow the existing architecture (see [`docs/architecture.md`](docs/architecture.md)). The core module must not
  depend on the web module, views must not touch repositories, and the Thrift adapter only talks to its port.
* Use constructor injection. Field injection is rejected by the ArchUnit rules.
* Don't hand-edit the Thrift-generated code in `com.fxmind.global`. Change `businesslogic/src/main/thrift/fxmind.thrift`
  and regenerate it instead. The wire contract must stay compatible with existing MetaTrader clients.
* Schema changes go into a new Flyway migration. Never modify an applied migration.
* Never commit credentials. Configuration comes from environment variables.

## Reporting bugs and security issues

Report bugs through GitHub Issues. For security vulnerabilities, follow [SECURITY.md](SECURITY.md).
