# Challenges & Fixes

This document records the bugs found during a full-codebase review of the Project
Management application and the fixes applied for each.

---

## 1. Invalid JSP package import → 500 on the two main pages

**Severity:** High

**Files:** `src/main/webapp/WEB-INF/views/all-tasks.jsp`,
`src/main/webapp/WEB-INF/views/all-projects.jsp`

**Problem:** Both pages declared
`<%@page import="com.projectManagement.models.Project"%>` — the wrong package
(the real one is `com.projectmanagement`, lowercase `m`). Jasper compiles
`<%@page import%>` directives into the generated servlet, so this invalid import
caused a JSP compilation failure (`package com.projectManagement.models does not
exist`) and an HTTP 500 on the application's two primary pages.

**Fix:** Removed the invalid `Project` import and the unused `java.util.List`
import from both pages. Neither was referenced in a scriptlet — the pages access
data only through EL (`${...}`), which needs no imports.

---

## 2. NullPointerException when deleting a non-existent project

**Severity:** Medium

**File:** `src/main/java/com/projectmanagement/controllers/ProjectController.java`

**Problem:** `deleteProject` called `projectDao.getProjectById(projectId)` and
then `projectDao.deleteProject(project, userId)` without checking the result. A
bogus or already-deleted `?id=` made `getProjectById` return `null`, which was
then dereferenced → NPE / HTTP 500.

**Fix:** Added a null guard so deletion is only attempted when the project
exists.

---

## 3. Wasted database work before redirects

**Severity:** Low (efficiency)

**File:** `src/main/java/com/projectmanagement/controllers/TaskController.java`

**Problem:** Three handlers (new-task POST, delete-task, edit-task POST) called
`addModelsToAllProjects(...)` — which runs `getAllTasksByProjectId` +
`getAllUsers` and builds a per-task user map — immediately before returning a
`redirect:`. The populated model is discarded on a redirect, and the redirect
target (`/all-tasks`) recomputes it anyway, so this was pure wasted DB work on
every task mutation.

**Fix:** Removed the three redundant calls. The helper remains in use by the
`/all-tasks` GET handler, which actually renders the view.

---

## 4. Hibernate session / connection leak across requests

**Severity:** High

**Files:** `src/main/java/com/projectmanagement/dao/Dao.java`,
`src/main/java/com/projectmanagement/filter/SessionCleanupFilter.java` (new)

**Problem:** `Dao.getSession()` opened a Hibernate `Session` and bound it to a
`ThreadLocal`, but nothing ever released it. Because Tomcat reuses pooled
threads, each thread's session — and its underlying DB connection and
first-level cache — leaked and grew across requests, eventually exhausting the
connection pool and serving stale cached data.

The reason closing had never been wired up: the old `close()` closed the session
but left it bound to the `ThreadLocal`, so the next request on that thread would
reuse a *closed* session and fail.

**Fix:**
- `close()` now reads the session directly from the thread-local, closes it only
  if open, and calls `remove()` so the thread no longer holds a dead session.
- `getSession()` reopens a fresh session if the bound one is closed (defensive).
- Added `SessionCleanupFilter`, a servlet `Filter` registered for all URLs that
  calls `Dao.close()` in a `finally` after each request. It runs after view
  (JSP) rendering, so lazy-loaded associations still resolve, and it covers the
  auth-excluded routes (`/login`, `/new-user`, etc.) that the interceptor skips.

---

## 5. NullPointerException when reassigning a task to an unknown task/user

**Severity:** Medium

**Files:** `src/main/java/com/projectmanagement/dao/TaskDao.java`,
`src/main/java/com/projectmanagement/controllers/TaskController.java`

**Problem:** `addTaskForUser` called `getTaskById` / `getUserById` and then
`user.addTask(task)` without null checks. A crafted request with an unknown
`taskId` or `userId` triggered an NPE and a bare HTTP 500 with no body.

**Fix:** `addTaskForUser` now returns `null` when the task or user does not
exist, and the `updateTaskAssignee` endpoint responds with
`400 Bad Request` ("Unknown task or user") instead of crashing.

---

## 6. Passwords stored and compared in plaintext

**Severity:** High (security)

**Files:** `pom.xml`, `src/main/java/com/projectmanagement/util/PasswordUtil.java`
(new), `src/main/java/com/projectmanagement/dao/UserDao.java`,
`src/main/java/com/projectmanagement/controllers/LoginController.java`

**Problem:** Registration saved the raw password, and `authenticate` matched
`email` + plaintext `password` directly in an HQL query. Passwords were never
hashed.

**Fix:**
- Added the `spring-security-crypto` dependency (version managed by the Spring
  Boot parent BOM).
- Added `PasswordUtil`, wrapping a `BCryptPasswordEncoder` with `hash(...)` and
  `matches(...)`.
- Registration now stores `PasswordUtil.hash(rawPassword)`.
- `authenticate` now loads the user by email and verifies the password with
  `PasswordUtil.matches(...)`.

**Migration note:** Existing accounts have plaintext passwords that will not
match BCrypt, so they can no longer log in. Because the schema is auto-created
(`hbm2ddl.auto=update`), the simplest path is to re-register those users (or
truncate the `users` table). There is no automatic migration.

---

## 7. Responsive layout broken — missing viewport meta tag

**Severity:** Medium (UI)

**Files:** all JSP views (now centralized in
`src/main/webapp/WEB-INF/views/fragments/head.jspf`)

**Problem:** None of the pages declared
`<meta name="viewport" content="width=device-width, initial-scale=1">`, so the
Bootstrap responsive grid never engaged on mobile — pages rendered zoomed-out at
desktop width.

**Fix:** As part of a full UI redesign (Bootstrap 4.5 → 5.3, shared `head.jspf` /
`navbar.jspf` fragments to remove per-page duplication, card-based layouts,
empty states, and a vanilla-`fetch` assignee update replacing jQuery), the
viewport tag is now emitted on every page through the shared head fragment.

---

## 8. Editing a task threw a NullPointerException (missing task id)

**Severity:** High

**File:** `src/main/webapp/WEB-INF/views/edit-task.jsp`

**Problem:** The edit-task form never rendered the task id, so the POST bound a
`Task` with `id = 0`. `editTaskForm` then called `getTaskById(0)`, got `null`,
and threw an NPE on `taskInDb.getProject()` (HTTP 500). Editing any task failed.

**Fix:** Added `<form:hidden path="id"/>` so the id is submitted; the existing
controller already reads `task.getId()`, so no backend change was needed.

---

## 9. new-task validation errors dropped the project context

**Severity:** Medium

**File:** `src/main/java/com/projectmanagement/controllers/TaskController.java`

**Problem:** The redesigned new-task page builds its form action and back/cancel
links from `${projectId}`. The `POST /new-task` handler only placed `projectId`
in the model on the success path; on a validation error it returned the
`new-task` view without it, so the re-rendered links lost the id and a resubmit
would 400.

**Fix:** Add `projectId` to the model at the top of the handler so the
re-rendered form stays bound to the right project. Also removed now-dead code
in the same controller: the `task` model attribute (the redesigned list page no
longer binds a task form object) and three unused `userId` locals.

---

## Verification status

The fixes were validated through the IDE language server (no compile errors; the
new BCrypt imports resolve). A full `mvn package` was **not** run in this
environment — there is no system Maven and the `.mvn` wrapper directory is absent
from the checkout. A real build (which needs network access the first time to
download `spring-security-crypto`) is recommended to confirm end-to-end.

## Pre-existing issues left untouched (out of scope)

These warnings predate the review and were not introduced by the fixes: unused
imports across several files, a raw-typed `ThreadLocal` in `Dao`, and unused
`userId` locals in `TaskController`.
