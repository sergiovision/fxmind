# FXMind architecture

## Context

```text
 ┌────────────────────┐   Thrift (binary, :2011)   ┌──────────────────────────────────────────┐
 │ MetaTrader 4/5 EA  │ ─────────────────────────► │ fxmind-web (executable Spring Boot jar)  │
 │ (MQL + Thrift DLL) │                            │  ├─ Vaadin Flow admin console (:8080)    │
 └────────────────────┘                            │  ├─ Spring Security (form login, BCrypt) │
                                                   │  ├─ Quartz scheduler (provider jobs)     │
 ┌────────────────────┐   HTTPS (jsoup/HttpClient) │  └─ Actuator (health, info, metrics)     │
 │ ForexFactory, OANDA│ ◄───────────────────────── │                                          │
 │ MyFXBook, eToro,   │                            │ fxmind-core (library)                    │
 │ Exness             │                            │  ├─ domain + Spring Data repositories    │
 └────────────────────┘                            │  ├─ application services + port          │
                                                   │  ├─ data providers (Strategy + Registry) │
                                                   │  └─ Thrift adapter (SmartLifecycle)      │
                                                   └───────────────────┬──────────────────────┘
                                                                       │ JDBC (HikariCP)
                                                                       ▼
                                                              MySQL 8.4 (Flyway-managed)
```

## Modules

| Module | Directory | Responsibility |
|---|---|---|
| `fxmind-core` | `businesslogic/` | Domain model, persistence, use cases, data providers, Thrift adapter. No UI, no scheduler. |
| `fxmind-web` | `webclient/` | Spring Boot entry point, Vaadin console, security, Quartz scheduling, Actuator. |

The dependency direction is strictly `web → core`, enforced by ArchUnit (`WebArchitectureTest`).

## Core packages

| Package | Contents |
|---|---|
| `domain` | JPA entities (`NewsEvent`, `OpenPositionRatio`, `Person`, `JobDefinition`, …), value types (`Importance`, `Privilege`). Identity-based, proxy-safe equality in `BaseEntity`. |
| `repository` | Spring Data JPA interfaces; JPQL with constructor expressions for aggregates (`SentimentAverage`). |
| `service` | Transactional use cases (`NewsEventService`, `SentimentService`, `RegistrationService`, …) and `BrokerClock` for broker-time ↔ UTC conversion. |
| `port` | `MarketDataQueryPort` – the inbound port used by transport adapters. |
| `thrift` | `FxMindMqlHandler` (adapter onto the port) and `ThriftServerLifecycle`. |
| `provider` | `DataProvider` strategies, `ProviderRegistry`, `PageFetcher` abstraction. |
| `dto` / `mapper` | Java records and the MapStruct `DomainMapper`. |
| `exception` | Sealed `FxMindException` hierarchy. |
| `global` | **Generated** Thrift code (do not edit; see "Thrift" below). |

## Design patterns and why

| Pattern | Where | Why |
|---|---|---|
| **Ports & Adapters** | `MarketDataQueryPort` ← `FxMindMqlHandler` | The MQL wire contract (string maps, broker-time strings) stays at the edge; the application core is transport-agnostic and testable without sockets. |
| **Strategy** | `NewsProvider`, `PositionRatioProvider` implementations | Each data source has its own fetch/parse logic but the same contract; adding a source is one new `@Component`. |
| **Registry** | `ProviderRegistry` | Jobs reference providers by a stable key stored in the database, so schedules are data, not code. Duplicate keys fail fast at startup. |
| **Sealed hierarchy + exhaustive switch** | `DataProvider`, `FxMindException` | `DataIngestionService` routes providers with a pattern-matching `switch` the compiler checks for completeness. |
| **Template Method** | `AbstractJob` → `ProviderJob`, `JobSupervisor` | Timing, error translation, logging and status persistence are written once; subclasses implement only `doExecute`. A failing source never unschedules a job. |
| **Lifecycle component** | `ThriftServerLifecycle` (`SmartLifecycle`) | The Thrift server starts after the context is ready and stops gracefully; replaces the static server started from a Quartz job. |
| **Facade** | `SchedulerService` / `QuartzSchedulerService` | Views talk to one small interface instead of the Quartz API. |
| **Reconciliation loop** | `JobSynchronizer` | Desired state (`jobs` table) is periodically applied to Quartz, so console edits take effect without restarts. |
| **Mapper** | MapStruct `DomainMapper` | Compile-time, reflection-free entity → record mapping. |

## Data flow

1. **Ingestion.** Quartz fires `ProviderJob`, which looks up the `handler` key in the `ProviderRegistry`. `DataIngestionService` runs the provider, and `NewsEventService` or `SentimentService` resolves currencies and symbols, deduplicates the data and persists it in UTC.
2. **Queries from MetaTrader.** The EA calls `ProcessStringData` or `ProcessDoubleData` with a `func` parameter. `FxMindMqlHandler` validates the parameters and calls the port. `MarketDataQueryService` converts broker time to UTC and delegates to the services, and the results go back to the EA in broker time.

## Persistence

* The schema is owned by Flyway (`V1__schema.sql`, `V2__default_data.sql`), and Hibernate runs with `ddl-auto=validate`. A test therefore fails as soon as an entity and the schema drift apart.
* The DDL is portable between MySQL 8.4 and H2 in MySQL mode. That lets the same repository contract (`AbstractRepositoryTest`) run on H2 for fast feedback and on real MySQL through Testcontainers.
* All timestamps are stored as UTC (`hibernate.jdbc.time_zone=UTC`).

## Thrift

The IDL lives in `businesslogic/src/main/thrift/fxmind.thrift`. The generated Java code is committed, because MetaTrader clients are compiled against the same IDL.

```bash
brew install thrift                 # compiler version must match libthrift (0.25.0)
./mvnw -pl businesslogic -Pthrift-gen generate-sources
```

## Security

* Console users authenticate against the `person` table; passwords are BCrypt hashes (`{bcrypt}` prefix via `DelegatingPasswordEncoder`).
* Every Vaadin route must be annotated with `@RolesAllowed` or `@AnonymousAllowed` (enforced by ArchUnit).
* `/actuator/health` and `/actuator/info` are public, and every other actuator endpoint requires the `ADMIN` role.
* There are no credentials in the repository: everything comes from environment variables (see `.env.example`). The first administrator is created from `FXMIND_ADMIN_EMAIL` and `FXMIND_ADMIN_PASSWORD`.
