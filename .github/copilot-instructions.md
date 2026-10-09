# Apache Fesod (Incubating) — Copilot Repository Instructions

## Project Overview

Apache Fesod (Incubating) is a Java library for processing spreadsheets (XLS/XLSX/CSV) without OOM, derived from Alibaba EasyExcel. It uses Apache POI under the hood with streaming SAX-based reading. The project is an Apache incubator project and must follow ASF conventions.

- **Language**: Java 8+ (source/target = 1.8; CI builds on JDK 8, 11, 17, 21, 25 — JDK 17+ is recommended for local development)
- **Build**: Maven 3.6.3 via `./mvnw` wrapper (project docs require 3.9+, but wrapper currently pins 3.6.3; both work)
- **CI matrix**: JDK 8, 11, 17, 21, 25

## Build & Test Commands

```bash
# Build (skips tests by default)
./mvnw clean install -DskipTests

# Run tests (must explicitly enable)
./mvnw clean package -Dmaven.test.skip=false -pl fesod-common,fesod-shaded,fesod-sheet

# Format check / auto-format
./mvnw spotless:check
./mvnw spotless:apply

# Run tests by tag
./mvnw test -Dtest.groups=unit          # fast unit tests only
./mvnw test -Dtest.excludedGroups=fuzz  # exclude slow fuzz tests

# Full install with javadoc (CI validation)
./mvnw install -B -V
./mvnw javadoc:javadoc
```

## Code Style & Conventions

- **Formatter**: Palantir Java Format (enforced by Spotless, no checkstyle)
- **Imports**: Static imports first, then regular imports; no unused imports (enforced by Spotless `importOrder` with `\#|` separator)
- **Indent**: 4 spaces
- **License header**: ASF Apache 2.0 header required on all `.java`, `.xml`, `.yml`, `.toml` files. Header template at `tools/spotless/license-header.txt`; enforced by the Hawkeye workflow (`.github/workflows/license-check.yml`), not Spotless
- **Lombok**: Allowed; config in `lombok.config` (`toString.callSuper = CALL`, `equalsAndHashCode.callSuper = CALL`)
- **Commits**: English, format `type: description` (types: `fix`, `feat`, `refactor`, `test`, `docs`, `chore`, `style`, `dependency`)

## Module Structure

```
(root)                 # Root POM (artifactId: fesod-parent), dependency & plugin management
├── fesod-common/      # Zero-dependency utilities (org.apache.fesod.common.util)
├── fesod-shaded/      # Relocated Spring ASM/cglib (org.apache.fesod.shaded)
├── fesod-bom/         # BOM for downstream consumers
└── fesod-sheet/       # Core library: read/write XLS/XLSX/CSV via POI
    ├── src/main/java/org/apache/fesod/sheet/
    │   ├── FesodSheet.java    # Main entry: FesodSheet.read() / FesodSheet.write()
    │   ├── analysis/          # Read pipeline (v03=XLS BIFF, v07=XLSX SAX, csv)
    │   ├── write/             # Write pipeline (builder, executor, handler chains)
    │   ├── metadata/          # Data models, builders, csv/ property/
    │   ├── converters/        # Type conversion framework (by Java type)
    │   └── util/              # DateUtils, NumberUtils, WorkBookUtil, etc.
    └── src/test/java/org/apache/fesod/sheet/
        └── testkit/           # Test infrastructure (NOT a separate module)
            ├── Tags.java      # @Tag constants: unit, round-trip, read, write, format, fuzz
            ├── base/          # AbstractExcelTest (round-trip base)
            ├── assertions/    # ExcelAssertions fluent API
            └── builders/      # TestDataBuilder

```

## Testing Conventions

- **JUnit 5** with `PER_CLASS` lifecycle and `ReplaceUnderscores` display name generator (configured globally in `fesod-sheet/src/test/resources/junit-platform.properties`, not via per-class annotations)
- **Tags** (use `@Tag(Tags.XXX)`):
  - `unit` — pure logic, no file I/O
  - `round-trip` — write-then-read via `AbstractExcelTest`
  - `read` / `write` — single-direction tests
  - `format` — CSV/charset/date format tests
  - `fuzz` — Jazzer property-based (slow, excluded in PR CI)
- **Real-file tests**: Prefer `AbstractExcelTest.createTempFile()` + `ExcelAssertions` for integration tests
- **Test data**: Use `TestDataBuilder.simpleData(n)` or model classes in `testkit/models/`

## CI Checks (must pass before merge)

1. `spotless:check` — format
2. License header (hawkeye)
3. Multi-JDK test matrix (8/11/17/21/25)
4. `mvn install` + `mvn javadoc:javadoc`
5. CodeQL security scan
