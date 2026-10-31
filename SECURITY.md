# Security policy

## Supported versions

Only the latest release on the `master` branch receives security fixes.

| Version | Supported |
|---------|-----------|
| 2.x (Java 25 / Spring Boot 4) | Yes |
| < 2.0 (legacy Java 8 stack) | No |

## Reporting a vulnerability

Please **do not open a public issue** for security problems.

Report vulnerabilities privately through
[GitHub Security Advisories](https://github.com/sergiovision/FxHubJava/security/advisories/new).
Include:

* a description of the issue and its impact;
* steps to reproduce, or a proof of concept;
* the affected version or commit.

You should get an acknowledgement within 7 days. Once the issue is confirmed, a fix is released and
the advisory is published, crediting you unless you prefer to stay anonymous.

## Hardening notes

* Never commit secrets. All credentials come from the environment (see `.env.example`).
* Dependencies are monitored by Dependabot and scanned by CodeQL. Maven Enforcer bans known vulnerable libraries.
