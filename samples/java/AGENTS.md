# AGENTS.md

This repository is published API documentation **plus** a reference implementation. The two are intentionally coupled:

- **Source of truth:** `samples/java` (Micronaut based application that implements: API server + API client using Thymeleaf UI) via Swagger/OpenAPI annotations and config.
- **Published output:** the versioned OpenAPI spec in the repo root (`depot-lifecycle-openapi-*.yaml`) and the GitHub Pages entrypoint (`index.html`).

If you change *anything* in the sample app (docs, client, or API behavior), you must:
1) add a changelog entry to the preamble in the `application.yml` configuration file under the configuration key `depotlifecycle.client.documentation.application.description`
2) regenerate + commit the OpenAPI spec (and update the GitHub Pages reference/hash in index.html).

---

## Repository map

### Root (published docs)
- `index.html`  
  GitHub Pages entrypoint. It hard-codes the OpenAPI spec filename and includes a “rebuild/hash” comment that must change whenever the spec changes (so Pages rebuilds and/or caches bust correctly).
- `depot-lifecycle-openapi-<version>.yaml`  
  The **generated** OpenAPI document for the Depot Lifecycle API.
- `README.md`, `LICENSE`  
  Repo-level docs and license.

### `samples/java` (reference implementation + generator)
Micronaut app that:
- Implements the Depot Lifecycle API server using annotations that generate OpenAPI.
- Serves Thymeleaf views (documentation UI / example UI).
- Includes a client capable of talking to the server.
- Owns the changelog (categorized: `doc`, `client`, `api`).

> Policy: treat the root OpenAPI YAML as a **build artifact**. Don’t hand-edit it except for purely mechanical fixes that you immediately upstream into the generator (the Java annotations/config) and re-generate.

---

## Change taxonomy (required for changelog)

Every change must be labeled as one of:

- **`api`** — anything that changes request/response shapes, paths, schemas, status codes, auth, validations, or semantics.
- **`client`** — changes to the sample client code, client behavior, client UX, or how the client calls the server.
- **`doc`** — documentation text, examples, descriptions, etc.

If a PR touches more than one category, create separate entries (preferred) or a single entry that lists multiple tags.

---

## Gradle best practices (required)

All Gradle build code must follow current best practices to ensure optimal build performance and maintainability.

### Lazy task configuration (required)
Gradle's configuration avoidance APIs prevent unnecessary work during the configuration phase, dramatically improving build performance.

**Core principles:**
- **Always use lazy APIs:** `tasks.register()` instead of `tasks.create()`
- **Never use eager task realization:** avoid `tasks.getByName()`, `tasks.all {}`, or accessing `tasks.foo` directly
- **Use lazy properties:** `Property<T>`, `ListProperty<T>`, `MapProperty<T>`, `SetProperty<T>` instead of plain fields in custom tasks
- **Configure tasks lazily:** use `tasks.named('taskName').configure {}` blocks, not direct property assignment at configuration time
- **Avoid premature task graph resolution:** don't call `.get()` on `Provider<T>` during configuration phase

### Task configuration examples

**✅ Good (lazy):**
```gradle
tasks.register('generateOpenApi', GenerateOpenApiTask) {
    specFile.set(layout.projectDirectory.file('openapi/depot-lifecycle.yaml'))
    outputDir.set(layout.buildDirectory.dir('generated/openapi'))
    doFirst {
        logger.info("Generating OpenAPI spec from ${specFile.get()}")
    }
}

tasks.named('build') {
    dependsOn('generateOpenApi')
}

// Configuring existing task lazily
tasks.named('compileJava') {
    options.encoding = 'UTF-8'
}
```

**❌ Bad (eager):**
```gradle
// DON'T: creates task immediately
tasks.create('generateOpenApi', GenerateOpenApiTask) {
    specFile = file('openapi/depot-lifecycle.yaml')
    outputDir = file('build/generated/openapi')
}

// DON'T: realizes task immediately
tasks.getByName('generateOpenApi') {
    // configuration
}

// DON'T: accesses task directly
tasks.build.dependsOn generateOpenApi

// DON'T: configures all tasks immediately
tasks.all {
    // this runs for EVERY task, even unused ones
}
```

### Custom task types

When creating custom Gradle task classes, always use Property APIs:

**✅ Good:**
```groovy
abstract class GenerateDocsTask extends DefaultTask {
    @InputFile
    abstract RegularFileProperty getSpecFile()
    
    @OutputDirectory
    abstract DirectoryProperty getOutputDir()
    
    @Input
    abstract Property<String> getApiVersion()
    
    @TaskAction
    void generate() {
        def spec = specFile.get().asFile
        def output = outputDir.get().asFile
        // task logic
    }
}
```

**❌ Bad:**
```groovy
class GenerateDocsTask extends DefaultTask {
    @InputFile
    File specFile  // DON'T: use RegularFileProperty instead
    
    @OutputDirectory
    File outputDir  // DON'T: use DirectoryProperty instead
    
    @Input
    String apiVersion  // DON'T: use Property<String> instead
}
```

### Code review guidelines

When reviewing PRs that touch Gradle build files:
- **Reject** any use of `tasks.create()` — require `tasks.register()` instead
- **Flag** direct task references like `tasks.foo` or `foo` — require `tasks.named('foo')`
- **Flag** `tasks.getByName()` — require `tasks.named()`
- **Flag** `tasks.all {}` or `tasks.withType(SomeType) {}` without lazy equivalents
- **Require** custom task types use `Property<T>` APIs with proper annotations
- **Ensure** no `.get()` calls on `Provider<T>` during configuration phase (outside `doFirst`/`doLast`/`@TaskAction`)

### Performance impact

Lazy configuration matters:
- **Configuration time:** improves by avoiding unnecessary task instantiation (critical for large builds with hundreds of tasks)
- **IDE responsiveness:** faster project sync in IntelliJ/Android Studio
- **Gradle daemon efficiency:** less memory pressure, better caching

### Reference
- [Gradle Task Configuration Avoidance](https://docs.gradle.org/current/userguide/task_configuration_avoidance.html)
- [Lazy Configuration](https://docs.gradle.org/current/userguide/lazy_configuration.html)

---

## Build organization: build-logic subproject (required)

This project uses the **build-logic** pattern to organize Gradle configuration by purpose, keeping the root `build.gradle` clean and focused.

### Pattern overview

All reusable build logic is organized into convention plugins in the `build-logic/src/main/groovy/` directory. Each plugin handles a specific concern:

- **`org.iicl.gradle.compile`** — Java compilation settings, annotation processing
- **`org.iicl.gradle.lombok`** — Lombok configuration and validation
- **`org.iicl.gradle.app`** — Micronaut application configuration, AOT, runtime settings
- **`org.iicl.gradle.docker`** — Docker image build configuration
- **`org.iicl.gradle.publish`** — OpenAPI spec publishing, index.html updates

The root `build.gradle` then applies these plugins and declares project-specific dependencies:

```gradle
plugins {
    id 'groovy'
    id 'org.iicl.gradle.compile'
    id 'org.iicl.gradle.lombok'
    id 'org.iicl.gradle.app'
    id 'org.iicl.gradle.docker'
    id 'org.iicl.gradle.publish'
}

version "4.0.0"
group "depotlifecycle"

dependencies {
    // project-specific dependencies only
}
```

### When to add/modify build logic

**✅ Add to build-logic plugins when:**
- The logic is reusable or addresses a specific concern (compilation, publishing, Docker, etc.)
- You're configuring a plugin that could benefit other projects
- You're creating custom tasks that encapsulate a specific workflow (e.g., `publishOpenApi`)
- The logic would clutter the root build file

**✅ Keep in root build.gradle when:**
- Declaring project-specific dependencies
- Setting project version and group
- Project-specific configuration that's unlikely to be reused

**❌ Never:**
- Put custom task logic directly in root `build.gradle` — extract to appropriate build-logic plugin
- Mix concerns across plugins — keep each plugin focused on one purpose
- Duplicate configuration — use build-logic plugins for shared settings

### Creating a new build-logic plugin

When adding new build logic:

1. **Create the plugin file:** `build-logic/src/main/groovy/org.iicl.gradle.[purpose].gradle`
2. **Add focused configuration:**
   ```gradle
   plugins {
       id 'some-plugin'
   }
   
   // Configuration for this concern
   somePlugin {
       // settings
   }
   
   // Custom tasks for this concern (using lazy APIs)
   tasks.register('myTask') {
       // task configuration
   }
   ```
3. **Apply in root build.gradle:** `id 'org.iicl.gradle.[purpose]'`

### Example: Adding a new validation plugin

**Bad (all in root build.gradle):**
```gradle
// DON'T put custom task logic in root build.gradle
task validateApi {
    doLast {
        // complex validation logic
    }
}
```

**Good (extract to build-logic):**

Create `build-logic/src/main/groovy/org.iicl.gradle.validation.gradle`:
```gradle
/**
 * API validation and quality checks
 */
tasks.register('validateApi') {
    description = 'Validates API endpoints and schema'
    group = 'verification'
    
    doLast {
        // validation logic
        logger.lifecycle("API validation passed")
    }
}
```

Then apply in root `build.gradle`:
```gradle
plugins {
    // ...existing plugins...
    id 'org.iicl.gradle.validation'
}
```

### Code review guidelines

When reviewing PRs that touch Gradle build files:
- **Reject** complex logic added directly to root `build.gradle` — require extraction to build-logic
- **Require** new functionality be organized into appropriate convention plugins
- **Ensure** each plugin maintains a single, clear purpose
- **Flag** duplication across plugins — consolidate shared logic
- **Verify** new plugins are properly applied in root `build.gradle`

### Benefits of this pattern

- **Separation of concerns:** Each plugin has a clear, focused purpose
- **Reusability:** Plugins can be shared across projects
- **Maintainability:** Easier to find and modify specific build logic
- **Clean root build file:** Project-specific configuration is immediately visible
- **Testability:** Convention plugins can be tested independently

### Reference
- [Sharing Build Logic: Convention Plugins](https://docs.gradle.org/current/userguide/sharing_build_logic_between_subprojects.html#sec:convention_plugins)
- [Structuring Projects with Gradle](https://docs.gradle.org/current/userguide/organizing_gradle_projects.html)

---

## Standard workflow (do this for every PR)

### 1) Make the code/config change in `samples/java`
Common targets:
- **API behavior / models:** controllers, DTOs/models, validation, error responses, auth filters, etc.
- **Docs text embedded in OpenAPI:** OpenAPI annotations and the config-driven description value:
  - `depotlifecycle.documentation.application.description` in `application.yml` (this feeds the OpenAPI `info.description`).
- **Thymeleaf UI:** templates + view models + controller routes.
- **Client behavior:** the client module/package/classes inside the sample app.

**Guardrails**
- Avoid editing the generated root YAML directly.
- Prefer updating annotations/config so the generator produces the correct spec.

---

### 2) Update the changelog (required)
Add an entry for *every* change.

**Where:** the changelog file within `samples/java` (follow existing file name + format used there).

**What to write:** a short, user-facing summary, prefixed by type tag.

**Recommended format**
- Add to the top under an “Unreleased” (or current version) section.
- Use imperative voice and include scope.

Examples:
- `api: Add endpoint to submit customer approval for an estimate revision.`
- `client: Display server error details when a request fails validation.`
- `doc: Clarify null vs absent behavior and add an example payload.`

**Failure mode to avoid:** merging without a changelog entry. Treat that as a broken PR.

---

### 3) Regenerate the OpenAPI spec (required)
The OpenAPI YAML in the repo root must reflect the current `samples/java` source.

**Build tool**
- Use the wrapper present in `samples/java` (Gradle wrapper `./gradlew` or Maven wrapper `./mvnw`).
- Run at least: clean + test/build so annotation processing runs and the OpenAPI artifact is generated.

**Goal**
- Produce the generated OpenAPI YAML from Micronaut OpenAPI/Swagger annotation processing.

**Locate the generated spec**
Micronaut OpenAPI commonly emits to a build output directory (paths differ by build tool and configuration). Use a search approach instead of guessing:

- Search for Swagger/OpenAPI output folders (often `META-INF/swagger/`) in the build output.
- Identify the generated YAML that corresponds to the Depot Lifecycle API.

**Copy into repo root**
- Copy the generated YAML into `/` and name it:
  - `depot-lifecycle-openapi-<version>.yaml`
- Ensure `<version>` matches your versioning convention (and, if applicable, the `info.version` inside the spec).

> If the version is being bumped: rename the root YAML file accordingly and update any internal references that mention the old filename.

---

### 4) Update `index.html` to point at the new spec AND bump the rebuild/hash
In `index.html`:

1. Update the hard-coded OpenAPI spec filename reference to the current `depot-lifecycle-openapi-<version>.yaml`.
2. Update the “hash” comment directly above/near that reference.

**Hash rule**
- The hash value is treated as a “change detector.” If the OpenAPI YAML changes, the hash must change in the same commit.
- Use a deterministic hash of the YAML contents (recommended) or follow the repo’s existing convention.

**Suggested deterministic approach**
- Compute SHA-256 of the root YAML file and paste it into the comment.

---

### 5) Local verification (minimum bar)
Before committing:
- Build the Java sample app successfully.
- Confirm the OpenAPI YAML in the root changed exactly as expected (diff review).
- Confirm `index.html` references the correct YAML filename and has an updated hash comment.
- Confirm changelog entry exists and matches the change type(s).

If you can run the app locally:
- Start the server and load the docs UI.
- Confirm Swagger UI loads the updated spec without errors.
- Spot-check at least one endpoint you touched (or a representative one).

---

## Pull request checklist (Definition of Done)

A PR is "done" only if it includes:

- [ ] Code/config changes in `samples/java`
- [ ] Changelog entry (`doc` / `client` / `api`)
- [ ] Regenerated root OpenAPI YAML committed
- [ ] `index.html` updated:
  - [ ] spec filename reference updated (if needed)
  - [ ] rebuild/hash comment updated
- [ ] Build passes locally (or in CI)
- [ ] **If Gradle build files changed:**
  - [ ] Uses `tasks.register()` (never `tasks.create()`)
  - [ ] Uses `tasks.named()` (never `tasks.getByName()` or direct access)
  - [ ] Custom tasks use `Property<T>` APIs with proper annotations
  - [ ] No eager configuration or premature task realization
  - [ ] **Build logic properly organized:**
    - [ ] Reusable/concern-specific logic extracted to `build-logic/` plugins
    - [ ] Root `build.gradle` contains only project-specific dependencies and version
    - [ ] New plugins follow single-purpose principle
    - [ ] No complex task logic in root `build.gradle`

---

## Common pitfalls / failure modes

- **Forgetting the changelog:** results in undocumented changes and version confusion.
- **Editing the root YAML by hand:** it will drift from the generator and be overwritten later.
- **Spec regenerated but `index.html` not updated:** GitHub Pages may continue to serve old docs or fail to load the spec.
- **Hash not updated:** changes to the spec might not trigger the desired rebuild/caching behavior.
- **Description changes only in `application.yml` but spec not regenerated:** published docs won’t reflect the new narrative text.

---

## Alternative framing (recommended long-term improvement)

If you find the manual steps error-prone, consider automating:

1) A script in `samples/java` (or repo root) that:
- builds the sample app,
- copies the generated OpenAPI YAML to the root with the right name,
- updates the `index.html` hash comment.

2) A CI check that fails if:
- `samples/java` changed but the root YAML didn’t, or
- the YAML changed but the `index.html` hash didn’t, or
- changelog wasn’t updated.

This repo is deliberately “docs-as-output,” so automation is worth it.

