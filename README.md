# FxHubJava

[![CI](https://github.com/sergiovision/FxHubJava/actions/workflows/ci.yml/badge.svg)](https://github.com/sergiovision/FxHubJava/actions/workflows/ci.yml)
[![CodeQL](https://github.com/sergiovision/FxHubJava/actions/workflows/codeql.yml/badge.svg)](https://github.com/sergiovision/FxHubJava/actions/workflows/codeql.yml)
![Java 25](https://img.shields.io/badge/Java-25%20LTS-orange?logo=openjdk)
![Spring Boot 4](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?logo=springboot)
![Vaadin 25](https://img.shields.io/badge/Vaadin%20Flow-25-00B4F0?logo=vaadin)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

FxHubJava is a **Forex market-intelligence server for algorithmic traders**. It:

* collects economic-calendar events and retail crowd-sentiment (long/short positioning) from several public sources;
* normalises the data to UTC and stores it in MySQL;
* serves it in real time to **MetaTrader 4/5 expert advisors over Apache Thrift**;
* includes a secure **Vaadin web console** for operating the data pipeline.

> A .NET successor of this project is available as [FinCore](https://github.com/sergiovision/FinCore).
> This repository is the Java implementation, fully modernised to the 2026 Java stack.
>
> The project was formerly called FXMind. Technical identifiers keep the `fxmind` name so existing deployments and MetaTrader clients keep working: the Java packages (`com.fxmind`), Maven artifacts, the Thrift IDL (`FXMindMQL`), the `FXMIND_*` environment variables and the database name.

---

## Highlights

* **Java 25 LTS**, using:
  * records, sealed interfaces with exhaustive pattern-matching `switch`, and `var`;
  * text blocks, `Math.clamp` and virtual threads.
* **Spring Boot 4.1** (Spring Framework 7, Jakarta EE 11, Hibernate 7) with constructor injection everywhere and type-safe `@ConfigurationProperties`.
* **Clean architecture:** Ports & Adapters around the Thrift transport, Strategy + Registry for data sources, and a Template Method for jobs. Architecture rules are enforced by ArchUnit.
* **Zero secrets in the repository.** Configuration comes from the environment, passwords are BCrypt hashes, and actuator endpoints are locked down.
* **Production-grade delivery:**
  * Flyway migrations validated against the JPA mappings;
  * Testcontainers MySQL tests;
  * JaCoCo, Spotless and Maven Enforcer gates;
  * a multi-stage Docker image;
  * GitHub Actions CI, CodeQL and Dependabot.

## Architecture

```text
MetaTrader EA ──Thrift :2011──► FxMindMqlHandler ──► MarketDataQueryPort ──► NewsEventService / SentimentService
                                (adapter)            (inbound port)          (use cases, @Transactional)
                                                                                        │
Quartz ──► ProviderJob ──► ProviderRegistry ──► DataProvider strategies                 ▼
(AbstractJob template)     (key → strategy)     ForexFactory · Exness · OANDA      Spring Data JPA
                                                MyFXBook · eToro                    │
Vaadin console ──► SchedulerService / PersonService / SettingsService               ▼
(Flow, RolesAllowed)                                                          MySQL 8.4 (Flyway)
```

| Module | Directory | What it contains |
|---|---|---|
| `fxmind-core` | [`businesslogic/`](businesslogic) | Domain, repositories, services, data providers, Thrift adapter |
| `fxmind-web` | [`webclient/`](webclient) | Executable Boot app: Vaadin console, security, Quartz, Actuator |

[`docs/architecture.md`](docs/architecture.md) describes the design in detail: the patterns and why each was chosen, the data flow, persistence, Thrift code generation and security.

### Design patterns at a glance

| Pattern | Implementation | Benefit |
|---|---|---|
| Ports & Adapters | `MarketDataQueryPort` ← `FxMindMqlHandler` | Wire protocol isolated from use cases; core testable without sockets |
| Strategy + Registry | `DataProvider` beans indexed by `ProviderRegistry` | New data source = one `@Component`; schedules reference providers by key |
| Sealed types | `DataProvider`, `FxMindException` | Compiler-checked exhaustive routing and error handling |
| Template Method | `AbstractJob` → `ProviderJob`, `JobSupervisor` | Uniform timing, error translation and status persistence for every job |
| Reconciliation loop | `JobSynchronizer` | Cron edits in the console apply live, without restarts |
| Lifecycle component | `ThriftServerLifecycle` (`SmartLifecycle`) | Graceful start/stop; one virtual thread per MetaTrader connection |
| Facade / Mapper | `SchedulerService`, MapStruct `DomainMapper` | Thin UI layer and reflection-free DTO mapping |

## Technology stack

| Concern | Technology |
|---|---|
| Language / runtime | Java 25 LTS (virtual threads) |
| Framework | Spring Boot 4.1, Spring Framework 7, Spring Security 7 |
| UI | Vaadin Flow 25 (Lumo theme), `VaadinSecurityConfigurer` |
| Persistence | Spring Data JPA, Hibernate 7, HikariCP, MySQL 8.4 (`mysql-connector-j`), Flyway |
| Integration | Apache Thrift 0.25 (`libthrift`), JDK `HttpClient`, jsoup, Jackson 3 |
| Scheduling | Quartz via `spring-boot-starter-quartz` |
| Mail | Spring Mail + Thymeleaf templates |
| Observability | Spring Boot Actuator (health probes, info, metrics), SLF4J 2 / Logback |
| Testing | JUnit 5, AssertJ, Mockito, Testcontainers, Spring Security Test, ArchUnit |
| Quality gates | JaCoCo, Spotless (Palantir Java Format), Maven Enforcer (Java/Maven versions, dependency convergence, banned vulnerable libraries) |
| Delivery | Maven Wrapper, multi-stage Dockerfile, Docker Compose, GitHub Actions, CodeQL, Dependabot |

## Quick start

### Option A: Docker Compose (app + MySQL)

```bash
cp .env.example .env           # set DB passwords and FXMIND_ADMIN_EMAIL / FXMIND_ADMIN_PASSWORD
docker compose up --build
```

Open <http://localhost:8080> and sign in with the administrator credentials from `.env`.
MetaTrader clients connect to `localhost:2011`.

### Option B: Maven

You need JDK 25 and a MySQL 8.4 database named `fxmind`.

```bash
./mvnw verify                  # build, run all tests and quality gates
export $(grep -v '^#' .env | xargs)
./mvnw -pl webclient spring-boot:run
```

Production jar with the optimised Vaadin bundle:

```bash
./mvnw -Pproduction package -DskipTests
java -jar webclient/target/fxmind.jar
```

## Configuration

Everything is configured through environment variables; there are no secrets in the source tree.

| Variable | Default | Purpose |
|---|---|---|
| `FXMIND_DB_URL` / `FXMIND_DB_USER` / `FXMIND_DB_PASSWORD` | `jdbc:mysql://localhost:3306/fxmind` / `fxmind` / – | Database connection |
| `FXMIND_ADMIN_EMAIL` / `FXMIND_ADMIN_PASSWORD` | – | First administrator (created only if none exists; min. 12 chars) |
| `FXMIND_THRIFT_PORT` | `2011` | MetaTrader Thrift endpoint |
| `FXMIND_BROKER_TIME_ZONE` | `EET` | Time zone of the MetaTrader trade server |
| `FXMIND_PUBLIC_URL` | `http://localhost:8080` | Base URL used in activation links (the `/activate` page) |
| `SPRING_MAIL_HOST` (+ `_PORT`, `_USERNAME`, `_PASSWORD`) | unset | Optional SMTP for registration mails |
| `PORT` | `8080` | HTTP port |

Data-source schedules live in the `jobs` table and can be edited in the console under **Scheduled jobs**.

## MetaTrader integration

The Thrift IDL is [`businesslogic/src/main/thrift/fxmind.thrift`](businesslogic/src/main/thrift/fxmind.thrift). EAs call:

| `func` | Method | Returns |
|---|---|---|
| `NextNewsEvent` (`symbol`, `time`, `importance`) | `ProcessStringData` | `[currency, importance, brokerTime, name]` of the next event within 24h |
| `CurrentSentiments` (`symbol`, `time`) | `ProcessDoubleData` | `[avgLong%, avgShort%]` over the last 100 minutes, or `[-1, -1]` |
| `SentimentsArray` (`symbol`, `site`, bar times as input) | `ProcessDoubleData` | Average long % per bar; `-125` marks gaps |

All times are exchanged in broker time (`yyyy.MM.dd HH:mm`). To regenerate the Java bindings, run `./mvnw -pl businesslogic -Pthrift-gen generate-sources`; this needs a Thrift 0.25 compiler.

## Testing

```bash
./mvnw verify
```

* **Unit tests** cover the domain rules, services (Mockito), the Thrift handler and the Quartz jobs and synchroniser. They also check that every Vaadin view builds.
* **Offline parser tests** run every data provider against captured HTML/JSON fixtures, so no network access is needed.
* **Thrift round-trip test** runs a real socket, binary protocol and processor on an ephemeral port.
* **Repository contract tests:**
  * every Flyway migration is validated against the Hibernate mappings;
  * the tests run on H2 in MySQL mode for speed;
  * they also run on **real MySQL through Testcontainers**, which is skipped automatically when Docker isn't available.
* **Full application test** boots Vaadin, Security, Quartz, JPA and Flyway, then checks:
  * the administrator provisioning;
  * the actuator access rules for anonymous, regular and admin users;
  * the login redirect.
* **ArchUnit rules:**
  * the core never depends on the web module, and views never touch repositories;
  * the Thrift adapter only talks to the port;
  * there is no field injection, there are no package cycles, and every route declares its access.

## Security notes

This modernisation removed every dependency with a known critical CVE, including:

* log4j 1.x;
* Thrift 0.9.1, which had been vendored as source;
* commons-collections 3.2.1, Velocity 1.7 and Solr 4.8;
* mysql-connector 5.1 and the MD5 password hashing.

Maven Enforcer bans their reintroduction.

> **Action required for the repository owner:** earlier commits contained a plaintext SMTP password and database/Tomcat credentials in `fxmind.properties` and `pom.xml`.
> * **Rotate those credentials now.** Removing them from the current tree doesn't remove them from Git history.
> * Purge them from history with [`git filter-repo`](https://github.com/newren/git-filter-repo) or the BFG Repo-Cleaner, then force-push.

## Skills demonstrated

* **Legacy modernisation.** I migrated a Java 8 / Spring 5 XML / Vaadin 7 / Hibernate 5 application to Java 25, Spring Boot 4, Vaadin Flow 25 and Hibernate 7. The work covered the `javax` → `jakarta` move, removing the XML contexts and the vendored libraries, and replacing hand-written DAOs with Spring Data repositories.
* **Software design.** The code applies hexagonal architecture, GoF patterns (Strategy, Registry, Template Method, Facade), sealed type hierarchies and immutable records. ArchUnit enforces the layering.
* **Distributed systems integration.** A Thrift RPC server serves MetaTrader clients with graceful lifecycle management and virtual-thread concurrency. The data pipeline is resilient: failures are isolated per source and per job.
* **Persistence engineering.** The schema is versioned with Flyway and the migrations are kept portable across databases. JPQL aggregates use constructor projections, all data is UTC-normalised, and the same tests run against both H2 and real MySQL.
* **Security.** BCrypt password storage, role-based route protection and hardened actuator endpoints. Secrets come only from the environment, dependencies are scanned for vulnerabilities, and CodeQL analyses the code.
* **Testing and quality.** Offline fixture tests, wire-level integration tests, Testcontainers and full-context tests, with coverage and formatting gates in the build.
* **DevOps.** Maven Wrapper, a layered non-root Docker image, Docker Compose, and GitHub Actions for CI, CodeQL, dependency review and Dependabot.

## Project history

The project began in 2014 as a hobby project for learning Forex trading and building automated trading robots. The 2026 rewrite keeps its purpose and the MetaTrader wire contract, but replaces the implementation with a modern, secure and fully tested code base.

## License

[MIT](LICENSE) © Sergei Zhuravlev
