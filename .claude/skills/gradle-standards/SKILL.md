---
name: gradle-standards
description: Required Gradle build-code standards for this repo - lazy task configuration APIs, Property-based custom tasks, and the build-logic convention-plugin organization. Use whenever creating or modifying any Gradle build file (build.gradle, settings.gradle, build-logic plugins) or reviewing a PR that touches them.
---

# Gradle build code standards

All Gradle code in `samples/java` must use configuration-avoidance (lazy) APIs and
the build-logic convention-plugin pattern.

## Lazy task configuration (required)

- `tasks.register()`, never `tasks.create()`.
- `tasks.named('foo').configure {}`, never `tasks.getByName()`, `tasks.all {}`, or
  direct references like `tasks.foo`.
- Custom task classes use lazy properties (`Property<T>`, `ListProperty<T>`,
  `RegularFileProperty`, `DirectoryProperty`) with proper `@Input` / `@InputFile` /
  `@OutputDirectory` annotations — never plain fields.
- Never call `.get()` on a `Provider<T>` during the configuration phase — only
  inside `doFirst` / `doLast` / `@TaskAction`.
- Prefer `tasks.withType(SomeType).configureEach {}` over eager `tasks.withType(SomeType) {}`.

```gradle
// Good
tasks.register('generateOpenApi', GenerateOpenApiTask) {
    specFile.set(layout.projectDirectory.file('openapi/depot-lifecycle.yaml'))
    outputDir.set(layout.buildDirectory.dir('generated/openapi'))
}
tasks.named('build') { dependsOn('generateOpenApi') }

// Bad
tasks.create('generateOpenApi', GenerateOpenApiTask) { ... }  // eager creation
tasks.build.dependsOn generateOpenApi                          // eager realization
tasks.all { ... }                                              // realizes every task
```

Custom task classes:

```groovy
// Good
abstract class GenerateDocsTask extends DefaultTask {
    @InputFile
    abstract RegularFileProperty getSpecFile()

    @OutputDirectory
    abstract DirectoryProperty getOutputDir()

    @TaskAction
    void generate() {
        def spec = specFile.get().asFile   // .get() OK inside the action
    }
}

// Bad: plain fields
class GenerateDocsTask extends DefaultTask {
    @InputFile File specFile          // use RegularFileProperty
    @Input String apiVersion          // use Property<String>
}
```

## Build-logic organization (required)

Reusable or concern-specific build logic lives in convention plugins under
`samples/java/build-logic/src/main/groovy/`, one purpose per plugin:

- `org.iicl.gradle.compile` — Java compilation, annotation processing
- `org.iicl.gradle.lombok` — Lombok configuration and validation
- `org.iicl.gradle.app` — Micronaut application configuration, AOT, runtime
- `org.iicl.gradle.docker` — Docker image builds
- `org.iicl.gradle.publish` — OpenAPI spec publishing / index.html updates

The root `build.gradle` contains only plugin applications, project version/group,
and project-specific dependencies. To add new build logic:

1. Create `build-logic/src/main/groovy/org.iicl.gradle.<purpose>.gradle`
2. Keep it focused on a single concern; use lazy task APIs
3. Apply it in the root `build.gradle` plugins block

Never inline custom task logic in the root `build.gradle`, mix concerns across
plugins, or duplicate configuration between them.

## Review checklist for Gradle changes

- Reject `tasks.create()` — require `tasks.register()`
- Flag `tasks.getByName()`, direct task references (`tasks.foo`), eager `tasks.all {}`
- Require `Property<T>` APIs with annotations on custom task types
- Flag `.get()` on providers at configuration time
- Reject complex task logic in the root `build.gradle` — require extraction to a
  build-logic plugin
