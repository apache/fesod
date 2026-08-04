---
applyTo: "**/*.java"
---

# Code Review Instructions for Apache Fesod

## Priority Checks

### 1. Java 8 Source Compatibility

The project targets Java 8 (`source/target = 1.8`) but CI runs on JDK 8 through 25. Review for:

- **No Java 9+ APIs**: Methods like `List.of()`, `Map.of()`, `String.strip()`, `InputStream.readAllBytes()` do not exist in Java 8. Use `Collections.singletonList()`, `new HashMap<>()`, `String.trim()`, or Apache Commons equivalents instead.
- **`java.sql.Date.toInstant()` / `java.sql.Time.toInstant()`**: These throw `UnsupportedOperationException` on Java 9+ because the types are date-only/time-only. When converting `java.util.Date` subclasses, always use `instanceof` checks and call `toLocalDate()` / `toLocalTime()` instead. This applies to both `WriteCellData` and `CsvCell` code paths — keep them consistent.
- **No `var` keyword**: Java 8 does not support `var`. Always declare explicit types.

### 2. Immutability & Defensive Copies

Builder methods that accept or return mutable collections must protect internal state:

- **General rule**: Any builder method that stores a collection from an external source must copy it. Any getter that returns an internal collection should document whether it's modifiable.

### 3. Input Validation & Boundary Values

- **Negative values**: Validation logic must handle all invalid values, not just sentinel defaults. For example, `@FreezePane(leftmostColumn = -1)` uses `-1` as "use default", but `leftmostColumn = -5` must also fall back to default — check `value == null || value < 0`, not just `value == -1`.
- **Null safety**: Public API entry points must validate null inputs and fail fast with a clear `IllegalArgumentException` rather than NPE deep in the call stack.

### 4. Resource & Lifecycle Management

- **ThreadLocal cleanup**: When setting thread-local state (e.g., `Biff8EncryptionKey.setCurrentUserPassword()`), do not clear it prematurely in a `finally` block if the state is needed by a later phase. The cleanup must happen only after all consumers have finished — typically in a dedicated `clearXxx()` method called at the end of the overall operation.
- **Try-with-resources**: Always use try-with-resources for `ExcelWriter`, `ExcelReader`, `Workbook`, `OutputStream`, `Writer`. Never leave workbook resources unclosed.
- **File handles**: After writing Excel files via POI, ensure the workbook is fully closed before the file handle is released.

### 5. API Consistency Across Code Paths

The same operation may be implemented in multiple places. When fixing a bug in one path, check all parallel paths:

- `WriteCellData` vs `CsvCell`: Both handle date conversion. A fix in one must be mirrored in the other.
- `FesodSheet` vs `FastExcel` vs `EasyExcel`: These are aliases. A behavior change in the modern API must not break legacy aliases.
- `ExcelWriterBuilder` vs `ExcelReaderBuilder`: Symmetric builder patterns should stay symmetric.

### 6. Test Quality

- **Encoding consistency**: When tests write files, always specify the charset explicitly. Use `Files.newBufferedWriter(path, StandardCharsets.UTF_8)` instead of `new FileWriter(file)` (platform-default charset). The test must match the encoding configured in the production code (e.g., `CsvWorkbook` uses UTF-8).
- **Deprecated APIs in tests**: Avoid `new java.util.Date(year-1900, month, day)` — use `Calendar` or `java.time` types converted via `Date.from()`. Deprecated constructors generate warnings that pollute build output.
- **Tag selection**: Use the correct `@Tag`:
  - `@Tag(Tags.UNIT)` for pure logic (no file I/O)
  - `@Tag(Tags.ROUND_TRIP)` for write-then-read integration tests
  - `@Tag(Tags.FORMAT)` for CSV/charset/date format tests
  - `@Tag(Tags.WRITE)` for write-only tests
- **Real-file verification**: For bug fixes that affect file output, add at least one test that writes a real file and reads it back (via `ExcelAssertions` or POI `WorkbookFactory`). Code-level assertions alone may miss integration issues.
- **Regression evidence**: When fixing a bug, verify the test actually fails without the fix (revert source, run test, confirm failure, restore fix).

### 7. ASF & Project Conventions

- **License header**: Every new `.java` file must have the ASF Apache 2.0 header (see `tools/spotless/license-header.txt`). License headers are enforced by the Hawkeye workflow (`.github/workflows/license-check.yml`), not Spotless.
- **Package naming**: All code must be under `org.apache.fesod.*`. Never use `com.alibaba.*` or `cn.idev.*` (legacy packages from EasyExcel era).
- **Lombok**: `toString.callSuper = CALL` and `equalsAndHashCode.callSuper = CALL` are enforced via `lombok.config`. Subclasses must call super.
- **No wildcard imports**: Avoid using wildcard imports (e.g., `import java.util.*;`). Always import classes individually. This enhances code readability, makes dependencies explicit, and prevents class name collisions during library upgrades.
- **No checked exceptions in public API**: Wrap checked exceptions in `ExcelGenerateException` or `ExcelAnalysisException` rather than declaring `throws` on builder methods.

### 8. Common Pitfalls (from real bug fixes)

| Pattern | Problem | Fix |
|---------|---------|-----|
| `value.toInstant()` on `java.sql.Date`/`Time` | `UnsupportedOperationException` on Java 9+ | Use `instanceof` + `toLocalDate()`/`toLocalTime()` |
| `value == -1` validation | Misses other negative values | Check `value == null` or `value < 0` |
| `try { setThreadLocal(x); } finally { setThreadLocal(null); }` | Clears state needed by later phase | Remove premature cleanup; clean up at end of full operation |
| `new FileWriter(file)` in tests | Platform-default charset | Use `Files.newBufferedWriter(path, StandardCharsets.UTF_8)` |
| `new FileOutputStream(file)` outside try-with-resources | Resource leak on exception | Wrap in `try (FileOutputStream fos = new FileOutputStream(file)) { ... }` |
