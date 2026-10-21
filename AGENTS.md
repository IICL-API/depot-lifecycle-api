# AGENTS.md

Guidance for AI agents (and humans) working in this repository.

## What this repository is

A depot-centric REST API specification for managing the interchange & repair
lifecycle of shipping containers, maintained by the IICL. It has two coupled halves:

- **Published output (repo root):** `depot-lifecycle-openapi-<version>.yaml` (the
  OpenAPI 3 spec) and `index.html` (the GitHub Pages entrypoint that renders the spec
  with Redoc). These are **generated artifacts** — never edit them by hand.
- **Source of truth (`samples/java`):** a Micronaut application (Java 25, Gradle)
  that is both a reference server and a reference client (Thymeleaf UI). The OpenAPI
  spec is generated from its Swagger/OpenAPI annotations and `application.yml`
  by the micronaut-openapi annotation processor.

To change the API, change the Java annotations/config — then regenerate.

## Layout

| Path | Purpose |
|---|---|
| `depot-lifecycle-openapi-<version>.yaml` | Generated OpenAPI spec (do not hand-edit) |
| `index.html` | GitHub Pages / Redoc entrypoint; references the spec filename + change-detection hash (auto-updated by Gradle) |
| `samples/java/src/main/java/depotlifecycle/domain/` | API models (JPA entities doubling as DTOs, annotated with `@Schema`) — grouped by API: `gate`, `redelivery`, `release`, `repair`, `equipment` |
| `samples/java/src/main/java/depotlifecycle/controllers/api/` | Reference **server** endpoints |
| `samples/java/src/main/java/depotlifecycle/controllers/client/` + `commands/` | Reference **client** (web UI form handling) |
| `samples/java/src/main/resources/views/` | Thymeleaf templates for the client UI |
| `samples/java/src/main/resources/application.yml` | Spec preamble & **changelog** (`depotlifecycle.documentation.application.description`) |
| `samples/java/src/main/java/depotlifecycle/Application.java` | Spec version (`@OpenAPIDefinition` → `version`) and model tag registry |
| `samples/java/build-logic/` | Gradle convention plugins, including the spec publishing tasks |

## Standard workflow for an API change

Work from `samples/java` (all Gradle commands run there):

1. **Edit the models** in `src/main/java/depotlifecycle/domain/` using `@Schema`
   annotations. Conventions:
   - Optional fields: `required = false, nullable = true` (plus a nullable `@Column`).
   - Enums: separate enum class annotated `@Schema(enumAsRef = true, ...)` so it
     becomes a shared component schema (see `GateRequestType`, `GateTransportType`).
   - Descriptions that enumerate codes use the `` `X` - meaning `` legend style.
   - Field order in the class = property order in the generated spec.
2. **Wire the reference implementations** so the change is exercised end to end:
   - Server (`controllers/api/...`): JPA persists new entity fields automatically;
     entity relations need explicit repository saves (see how `trucker` is handled).
   - Client: add the field to the matching `commands/...Command`, map it onto the
     request in `controllers/client/...`, and add a form control in
     `src/main/resources/views/...` (optional selects get an empty first `<option>`;
     enum values come from a model attribute registered in the client controller's
     `index()`).
3. **Add a changelog entry** in `application.yml` (see "Changelog" below). Every
   change needs one; the changelog is published inside the spec itself.
4. **Regenerate and publish the spec:**
   ```
   ./gradlew publishOpenapiDocs
   ```
   This builds, copies the generated spec from
   `build/classes/java/main/META-INF/swagger/` to the repo root with the correct
   filename, and updates the `index.html` reference/hash when the content changed.
   Do **not** hand-edit the root YAML or copy files manually. Component tasks
   (composed by `publishOpenapiDocs`): `copyOpenapiSpec`, `computeOpenapiHash`,
   `updateIndexHtml`, `cleanOldOpenapiSpecs`.
5. **Review `git diff`** on the root YAML — confirm it contains exactly the intended
   schema changes — and commit the regenerated artifacts together with the source.

### Changelog (required for every change)

Entries live in `application.yml` under
`depotlifecycle.documentation.application.description`, grouped by version. Each
entry is prefixed with the scopes it affects, comma-separated:

- **`api`** — changes to request/response shapes, paths, schemas, status codes,
  auth, validations, or semantics.
- **`client`** — changes to the sample client code, behavior, or UX.
- **`server`** — changes to the example server implementation.
- **`doc`** — documentation-only changes; no implementation impact.

Example: ``- (api, client, server) Add optional `transportType` to GateCreateRequest
and GateUpdateRequest to indicate how the shipping container was transported.``

Merging without a changelog entry is a broken PR.

### Version bumps

Bump `version` in `Application.java` (`@OpenAPIDefinition`) and start a new
changelog section in `application.yml`. `publishOpenapiDocs` derives the spec
filename from the generated version and updates `index.html`; run
`cleanOldOpenapiSpecs` to remove the previous root YAML, and update any references
to the old filename.

## Build & run

From `samples/java`:

- `./gradlew compileJava` — compiles and regenerates the spec into `build/` (fast check).
- `./gradlew run` — starts the app on port **8086**: server API at `/api`, client UI
  at `/client`, generated docs at `/redoc`. Client target server is configured via
  `LIFECYCLE_ENDPOINT` / `LIFECYCLE_AUTHORIZATION` env vars.
- `./gradlew publishOpenapiDocs` — regenerates and publishes the root spec (see above).

## Gradle build code

Any change to Gradle build files must follow the **`gradle-standards`** project
skill: lazy configuration APIs only (`tasks.register` / `tasks.named` /
`Property<T>`), and build logic organized into `build-logic/` convention plugins —
never inlined in the root `build.gradle`.

## PR checklist (Definition of Done)

- [ ] Code/config changes made in `samples/java` (not in generated artifacts)
- [ ] Changelog entry added with correct scope tags (`api` / `client` / `server` / `doc`)
- [ ] `./gradlew publishOpenapiDocs` run; regenerated root YAML (and `index.html`,
      if changed) committed alongside the source change
- [ ] Root YAML diff reviewed and matches the intended change exactly
- [ ] Build passes locally
- [ ] If Gradle files changed: follows the `gradle-standards` skill

## Pitfalls

- Hand-editing the root YAML: it drifts and gets overwritten on the next publish.
- Changing `samples/java` without regenerating: published docs go stale.
- Skipping the changelog entry: treat as an incomplete change.
- The spec's `info.description` (JSON/null-vs-absent rules, security examples,
  changelog) lives in `application.yml`, not in the YAML — edit it there.
