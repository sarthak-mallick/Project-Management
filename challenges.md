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

## 10. Editing a stale task 500'd; PRG / navigation audit

**Severity:** Medium

**File:** `src/main/java/com/projectmanagement/controllers/TaskController.java`

**Problem:** `editTaskForm` dereferenced `getTaskById(task.getId())` before its
`try` block with no null check. Submitting a stale edit form — the task was
deleted in the meantime, a browser back/forward replayed the POST, or the id was
tampered — produced an uncaught NullPointerException (HTTP 500).

**Fix:** Guard for a missing task or project and fall back to
`redirect:/all-projects` so navigation never breaks.

**Audit context:** This came out of a review of the Post-Redirect-Get pattern
and refresh/back/forward behavior. Findings:

- Every state-changing POST already redirects on success (`/new-user`,
  `/login`, `/new-project`, `/new-task`, `/edit-task`), so refreshing or
  navigating onto a result page re-runs an idempotent GET. PRG is correct.
- Validation errors intentionally forward (return the view) rather than
  redirect, so refreshing a page that just showed field errors prompts the
  browser's "resubmit form?" dialog. Conventional trade-off; left as-is at the
  time — since fixed in item 11 below.
- Deletes use GET links but are safe to replay: `delete-project` is protected
  by its null guard and `delete-task` by its try/catch, so a back-button replay
  is a no-op redirect rather than a duplicate delete or a 500. (Replay safety
  was the only thing checked here; the wider problem with delete-by-GET is
  item 13.)

---

## 11. Validation errors broke Post-Redirect-Get (resubmit-on-refresh)

**Severity:** Medium

**Files:** `src/main/java/com/projectmanagement/util/FormFlash.java` (new),
`src/main/java/com/projectmanagement/controllers/LoginController.java`,
`src/main/java/com/projectmanagement/controllers/ProjectController.java`,
`src/main/java/com/projectmanagement/controllers/TaskController.java`

**Problem:** PRG was only half-applied. Success paths redirected, but all four
validation-error paths (`/new-user`, `/login`, `/new-project`, `/new-task`)
returned the view name directly, so the error page was rendered *as the response
to the POST*. The browser's URL stayed on the POST, which meant refreshing —
or navigating back onto — a page that had just shown field errors re-triggered
the "Confirm form resubmission" dialog and replayed the POST. On `/new-project`
and `/new-task` that replay is a real write: correct the field, submit, go back,
refresh, and the row is created twice.

`POST /login` had a second variant of the same problem: a wrong password
redirected to `/login` with no error attached at all, so the user was silently
bounced back to an empty form with no idea why.

**Fix:** Every error path now redirects, carrying the rejected form across the
redirect in flash attributes:

- Added `FormFlash.flashErrors(...)`, which flashes both the bound object and its
  `BindingResult` (under `BindingResult.MODEL_KEY_PREFIX + name`, the key the
  `<form:...>` tags look up).
- The four GET form handlers dropped their model-attribute parameter and now seed
  an empty object only when the model does not already hold one
  (`if (!modelMap.containsAttribute("project"))`). This is the crux: Spring's
  `RequestMappingHandlerAdapter` merges the input flash map into the
  `ModelAndViewContainer` *before* the handler runs, so on the redirect the
  flashed object and errors are already in the model — but a
  `@ModelAttribute`-typed parameter would rebind the (parameter-less) GET request
  and overwrite the flashed `BindingResult` with an empty one, silently dropping
  every error message. Without the parameter, nothing overwrites them.
- `POST /login` now rejects the `password` field with "Invalid email or password"
  on a failed authentication, so the redirect target explains itself.
- Both password-bearing forms null the password on the object before flashing.
  `rejectValue` snapshots the current field value as the error's rejected value,
  hence the clear happens *before* the reject — this keeps the plaintext password
  out of the session-backed flash map and out of the re-rendered input.

**Behavior change:** after a validation error on the signup or login form, the
password field comes back empty and must be retyped. The other fields keep their
submitted values, as before.

**Left as-is at the time:** deletes are still `GET` links (`delete-task`,
`delete-project`). They are idempotent and guarded, so a replay is a no-op
redirect, but they remain non-conforming to the "state change only via POST" half
of the pattern — since fixed in item 13 below.

---

## 12. Edit-task accepted any effort value and 400'd on a non-numeric one

**Severity:** Medium

**Files:** `src/main/java/com/projectmanagement/validator/TaskEditValidator.java`
(new), `src/main/resources/messages.properties` (new),
`src/main/java/com/projectmanagement/controllers/TaskController.java`,
`src/main/webapp/WEB-INF/views/edit-task.jsp`

**Problem:** `POST /edit-task` was the only form handler with no validation and
no `BindingResult` parameter at all. Two consequences:

- `min="0"` on the effort inputs is client-side only, so a crafted or
  devtools-edited request stored negative effort. `Task.getPercentComplete()`
  then returned a negative percentage, which the task list renders as a progress
  bar.
- With no `BindingResult` parameter declared, a value that cannot bind to the
  `double` fields (a non-numeric string, or a cleared input) made Spring throw
  `BindException` instead of recording a field error — an empty HTTP 400 with no
  message. Clearing an effort box and saving was enough to trigger it.

**Note on scope:** `TaskValidator` is *not* the right validator here. The edit
form submits only `id`, `effortEstimate` and `effortLogged`, and
`TaskDao.saveTaskEdit` copies only the two effort fields — the name is neither
editable nor persisted on this form. Running `TaskValidator` against the bound
object would reject the (never-submitted) name on every request, and
`CommonValidator.regexValidate` would then throw an NPE on
`Pattern.matcher(null)`.

**Fix:**
- Added `TaskEditValidator`, which rejects negative effort values. It skips a
  field that already carries an error, since a field that failed conversion binds
  as `0.0` and would otherwise collect a second, misleading message.
- `POST /edit-task` now takes a `BindingResult` (so conversion failures become
  field errors rather than a `BindException`), validates, and on errors flashes
  the form via `FormFlash` and redirects back to
  `/edit-task?id=…&projectId=…` — the same PRG path as item 11. The name is
  copied from the stored row before flashing so the page heading survives the
  re-render.
- `GET /edit-task` picks up the item 11 pattern: seed from the DB only when the
  model has no flashed task, and redirect to `/all-projects` when the id matches
  no row (the GET counterpart of the null guard item 10 added to the POST).
- `edit-task.jsp` gained `<form:errors>` slots for both fields, and the redundant
  `value="${task.effortEstimate}"` / `value="${task.effortLogged}"` attributes
  were removed: `path` already renders the bound value, and the hardcoded
  attribute (written before the tag's own `value`, so the browser honours it)
  would have shown the stored value instead of what the user typed.
- Added `messages.properties` so binding failures read "Effort estimate must be a
  number" rather than Spring's default `typeMismatch` text, which names Java
  types. Spring Boot auto-configures a `MessageSource` from this default
  `messages` basename; validators that pass a message inline are unaffected,
  since an unresolvable code falls back to that message.

---

## 13. Deletes were GET links, so following a link could destroy data

**Severity:** High (security)

**Files:** `src/main/java/com/projectmanagement/controllers/ProjectController.java`,
`src/main/java/com/projectmanagement/controllers/TaskController.java`,
`src/main/webapp/WEB-INF/views/all-projects.jsp`,
`src/main/webapp/WEB-INF/views/all-tasks.jsp`

**Problem:** `delete-project` and `delete-task` were `@GetMapping` handlers
reached through plain `<a href>` links. GET is defined as a safe method, so
anything that follows a URL is entitled to issue one:

- A logged-in user clicking a link from anywhere — email, chat, a forum post —
  pointing at `…/delete-project?id=5` deleted that project. This is the vector
  that survives modern cookie defaults: `SameSite=Lax` (the Chromium default; no
  cookie policy is configured here) blocks cross-site *subresource* GETs such as
  `<img src=…>`, but by design still sends the session cookie on top-level
  navigations.
- Link prefetchers, crawlers, browser extensions and security scanners fetch URLs
  they find in a page; same-origin requests carry the session cookie.
- Back/forward history replays the request.

The `onclick="return confirm(...)"` on both links gated a human click, not a
request. What limited the damage was incidental rather than by design: the null
guard on `delete-project` and the try/catch on `delete-task` make a *replayed*
delete a no-op redirect, but aimed at a row that still exists, all of the above
delete it.

**Fix:** Both handlers are now `@PostMapping`, and both links became inline
`method="post"` forms carrying the ids as hidden inputs, with the confirm moved
from `onclick` to `onsubmit`. Neither list page has an enclosing `<form>`, so
nesting is not an issue. The handlers already redirected after deleting, so
post-delete behavior is unchanged and the redirect keeps them refresh-safe.

**Still missing:** there is no CSRF token, so a hostile page can still auto-submit
a cross-site POST. `SameSite=Lax` blocks that in Chromium, but a token is the
real defense; this app has no Spring Security filter chain to inherit one from,
so it would have to be hand-rolled (session-stored token rendered into every
form and checked in an interceptor).

**Related, left as-is:** `/logout` is also a `@GetMapping` that changes state
(`session.invalidate()`), reached from a `<a href="/logout">` in `navbar.jspf`, so
a prefetcher or a crafted link can sign a user out. The impact is an annoyance
rather than data loss, and converting it means turning the navbar link into a
form or button, so it was left out of this change.

---

## Verification status

Items 1–10 were validated through the IDE language server (no compile errors; the
new BCrypt imports resolve).

Items 11–12 were additionally checked by compiling every file under
`src/main/java` with `javac` against the jars already in `~/.m2` (clean, apart
from the pre-existing raw-`ThreadLocal` and redundant-cast warnings), and by
running MockMvc round trips — invalid POST, then the follow-up GET with the
resulting flash map — that assert the redirect status and target, that the
`BindingResult` and the rejected field values survive into the GET model, that a
clean GET still renders a blank form, that a wrong password comes back as a
message with the password input empty and the plaintext absent from the flash
map, that a non-numeric effort redirects instead of returning 400, and that a
valid edit is still written. Those checks ran against controllers mirroring the
new handlers, because the real ones take DAO parameters whose static
`SessionFactory` needs a live MySQL; they were scratch files and are not part of
the checkout.

Item 13 was checked by recompiling and re-running those suites (unaffected, all
passing), by confirming the two delete routes are the only state-changing
handlers left outside `@PostMapping` apart from `/logout`, that no `href="delete…"`
links remain, and that the new forms are balanced and unnested.

A full `mvn package` was **not** run in this environment — there is no system
Maven and the `.mvn` wrapper directory is absent from the checkout. Neither was
the app exercised end-to-end against MySQL, so the JSP changes in items 11–13
(the new `<form:errors>` slots and the two delete forms) are unrendered; a real
build and a manual pass over the four forms and both delete buttons are
recommended.

## Pre-existing issues left untouched (out of scope)

These warnings predate the review and were not introduced by the fixes: unused
imports across several files, a raw-typed `ThreadLocal` in `Dao`, and unused
`userId` locals in `TaskController`.
