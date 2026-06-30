# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run the app in development (serves on http://localhost:8080)
mvn spring-boot:run          # or ./mvnw spring-boot:run

# Build the WAR
mvn clean package            # output: target/ProjectManagement-0.0.1-SNAPSHOT.war

# Run tests
mvn test
mvn test -Dtest=ProjectManagementApplicationTests   # single test class
```

Requires **Java 21**, **Maven 3.6+**, and a running **MySQL** server. On startup Hibernate
auto-creates the `projectmanagement` database/schema (`createDatabaseIfNotExist=true`,
`hbm2ddl.auto=update`), so no manual migrations are needed.

## Architecture

Spring Boot 3.4.0 MVC app packaged as a **WAR** (deployable to embedded or external Tomcat;
`ServletInitializer` provides the external-deployment hook). Views are server-rendered **JSP +
JSTL** under `src/main/webapp/WEB-INF/views/`, resolved via the prefix/suffix in
`application.properties`.

The most important thing to understand is the **persistence layer, which deliberately does NOT
use Spring Data JPA or Spring-managed transactions:**

- DB credentials and entity mappings live in `src/main/resources/hibernate.cfg.xml`, **not** in
  `application.properties`. Change connection URL/username/password there.
- `dao/Dao.java` is an abstract base that builds its **own** `SessionFactory` from
  `hibernate.cfg.xml` via a `static` initializer, and manages Hibernate `Session`s through a
  `ThreadLocal`. Transactions are handled manually (`begin()`/`commit()`/`rollback()`) inside
  each `saveObject`/`updateObject`/`deleteObject`. Spring's `@Transactional` and DI of the
  SessionFactory are **not** in play — the `@Autowired`/`@Repository` annotations on the DAOs are
  largely cosmetic since session management is static.
- Concrete DAOs (`UserDao`, `ProjectDao`, `TaskDao`) extend `Dao<T>` and add HQL query methods
  (e.g. `UserDao.authenticate`, `getUserById`). When adding queries, follow the existing
  parameterized-HQL pattern.

**Request flow:** `controllers/*` (`@Controller`, returning JSP view names) → DAO → Hibernate.
Controllers use `@ModelAttribute` form binding plus a manual `validator/*` call against a
`BindingResult`; on errors they re-render the same JSP. The shared `CommonValidator.regexValidate`
is the building block for field validators.

**Authentication & sessions:** login (`LoginController`) authenticates against the DB and stores
`userId` in the `HttpSession`. `interceptor/SessionInterceptor` (registered in `config/WebConfig`)
guards every route via `addPathPatterns("/**")`, redirecting unauthenticated requests to `/login`.
Public routes are the `excludePathPatterns` in `WebConfig`: `/login`, `/new-user`, `/user-added`,
`/logout`. **Add any new public/unauthenticated route to that exclude list** or it will be blocked.

**Domain model** (`models/`): `User` 1—* `Task`, and `Project` relates to `Task`. Entities are
JPA-annotated (`@Entity`/`@Table`); note passwords are currently stored and compared in plaintext.

## Conventions

- New entities must be registered as `<mapping class="..."/>` in `hibernate.cfg.xml` (the
  SessionFactory is built only from that file, not from classpath scanning).
- New controller routes that require login need no extra wiring; routes that must be public must be
  added to `WebConfig`'s exclude list.
