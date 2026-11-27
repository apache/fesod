<!--
- Licensed to the Apache Software Foundation (ASF) under one or more
- contributor license agreements.  See the NOTICE file distributed with
- this work for additional information regarding copyright ownership.
- The ASF licenses this file to You under the Apache License, Version 2.0
- (the "License"); you may not use this file except in compliance with
- the License.  You may obtain a copy of the License at
-
-   http://www.apache.org/licenses/LICENSE-2.0
-
- Unless required by applicable law or agreed to in writing, software
- distributed under the License is distributed on an "AS IS" BASIS,
- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
- See the License for the specific language governing permissions and
- limitations under the License.
-->

# Proposal: Lightweight Streaming XLSX-to-PDF Conversion

Related: [#1290](https://github.com/apache/fesod/issues/1290).

**Status: design proposal only.** This document does not add a PDF converter,
a Maven module, dependencies, or public APIs. The approach and supported scale
remain subject to community discussion and proof-of-concept validation.
Implementation should follow the project's [contribution guidelines](CONTRIBUTING.md)
after agreement on the feature.

## Motivation

Applications may need to turn large spreadsheets into paginated PDFs without
Microsoft Office, LibreOffice, or an external conversion process. Fesod's
streaming reader provides a starting point, but low-memory conversion also
requires bounded layout buffers and an appropriate PDF generation backend.

[MiniPdf](https://github.com/mini-software/MiniPdf) can serve as a reference for
layout behavior. Accepting input and output streams alone does not establish
streaming memory behavior, so the proposal is not to wrap a full-workbook
converter.

## Proposed Initial Scope

| Area | Initial scope |
| --- | --- |
| Input | XLSX files and input streams |
| Worksheets | Selected sheets or visible sheets in workbook order |
| Values | Text, numbers, dates, booleans, and cached formula results |
| Formatting | Font family and size, bold text, font colors, solid fills, common borders, and alignment |
| Dimensions | Column widths, row heights, hidden rows and columns, and text wrapping |
| Pagination | Basic page sizes, orientation, margins, and vertical pagination |
| Wide sheets | An explicit scale-to-one-page-width option rather than arbitrary horizontal pagination |
| Merged cells | Same-row horizontal merges |
| Output | PDF files and output streams |

XLS, CSV, formula recalculation, rich text, conditional formatting, images,
charts, shapes, cross-row merges, and cross-page merges are outside the initial
scope. The converter should not claim full Excel print fidelity.

Unsupported features and missing formula results or font glyphs must be
reported explicitly, rather than silently omitted. The warning and failure
policy is an open design question.

## Proposed Architecture

### Optional Module

Add an optional `fesod-pdf` module depending on `fesod-sheet` and the selected
PDF backend. Existing spreadsheet users should not acquire PDF dependencies
unless they opt into the new module.

Any reader extensions should expose format-neutral metadata and be opt-in.
PDF-specific types should stay outside the spreadsheet reader. Public API names
and signatures are not committed by this proposal.

### Input Preparation and Metadata Pre-scan

Use a file-backed input for repeatable access. A non-repeatable input stream
would be copied incrementally to a temporary file, not to a whole-file byte
array. Temporary disk usage is part of the resource budget.

Pre-scan the workbook relationships and selected worksheet XML for column
definitions, merge ranges, page settings, and relevant style and theme data.
Some of this information can occur after the row data, so discovering it only
through ordinary row callbacks is too late for layout.

The scan should not materialize all cells or retain all row attributes. Style
and merge metadata still consume resources; their limits or disk-backed
representation must be defined before claiming a supported scale.

### Row Reading and Layout

Reuse Fesod's SAX reader for cell values. Proposed opt-in metadata includes
cell style references and the current row's height, hidden state, and default
style. Resolve cell, row, and column style inheritance consistently.

Keep ordinary reader defaults unchanged. The conversion path must explicitly
preserve blank-row geometry and whitespace rather than applying data-oriented
header or trimming assumptions.

Reuse existing number and date formatting behavior, including locale and the
1900/1904 date systems. Use cached formula values without evaluating formulas
or accessing external workbooks.

Measure and lay out rows incrementally, retaining only the active row and page
buffers required by the supported features. Define behavior for a wrapped row
that is taller than the usable page height.

### PDF Backend Evaluation

Apache PDFBox 3.x is a candidate, not a selected dependency. Its
[stream cache support](https://pdfbox.apache.org/3.0/migration.html)
allows PDF content streams to use temporary files.

A disk-backed content stream cache does not prove constant total heap usage.
Page trees, resource objects, fonts, and final serialization must also be
measured. Avoid accumulating rendered pages or the complete PDF in byte arrays.
Fonts should be reused within a conversion rather than loaded for every page.

Select the backend only after a proof of concept measures both page generation
and final save. If it cannot satisfy the agreed budget, revisit the backend or
supported scale instead of describing the result as fully bounded streaming.
Writing one PDF per page and merging them later is not sufficient evidence,
because the merge can reintroduce the same memory problem.

The initial contract need not provide a PDF that is readable before conversion
and finalization have completed.

## Resource and Error Handling

- Define ownership of caller-provided streams; always close internally owned resources.
- Release input packages, shared-string caches, PDF resources, and conversion-owned temporary files on success and failure.
- Preserve the original cause of invalid input, disk exhaustion, and output failures.
- Do not report successful conversion after a required stage fails.
- Publish a destination file only after successful finalization; document that an output stream can contain partial data on failure.
- Reject conflicting input and output paths.
- Keep font configuration conversion-scoped and report missing glyphs.
- Validate XML and ZIP inputs without enabling external entity or relationship resolution.

## Proposed Validation

These are proposed acceptance criteria, not measured performance results:

1. Establish a baseline of 100,000 rows by 20 columns in a separate JVM with
   `-Xmx256m`. Use cell text of at most 64 Unicode code points, at most 128
   styles, and at most four configured fonts.
2. Test both repeated strings and large shared-string tables with unique values.
3. Include final PDF save and font subsetting in peak-memory measurements.
4. Record peak heap, process memory, temporary disk usage, elapsed time, page
   count, and final PDF size. Allocation totals alone do not prove bounded
   memory.
5. Evaluate 1,000,000 rows as a follow-up target, without promising that scale
   before measurement.
6. Verify text, row ordering, column positions, page dimensions, dates, formula
   caches, whitespace, Unicode, and supported formatting with real files.
7. Compare small visual fixtures using explicitly configured fonts and page
   sizes rather than relying on platform-installed fonts.
8. Exercise malformed inputs, output failures, insufficient temporary storage,
   oversized rows, and Windows file-handle cleanup.
9. Verify that opt-in reader metadata does not change existing XLSX, XLS, or CSV
   behavior, and test applicable code on the project's supported JDK matrix.

## Suggested Implementation Sequence

1. Agree on feature scope, error policy, and the memory contract in #1290.
2. Build a minimal multi-page PDF proof of concept and evaluate the backend.
3. Add the opt-in reader metadata with regression coverage.
4. Implement the supported formatting and pagination incrementally.
5. Add API and limitation documentation, resource tests, and memory benchmarks.
6. Wire the new module into the parent POM, BOM, and explicit CI module lists.
   Review dependency and font redistribution licenses before release.

## Questions for Community Feedback

- Does an optional `fesod-pdf` module fit Fesod's intended scope?
- Is repeatable, file-backed input with a metadata pre-scan acceptable?
- Which formatting features are essential for the first release?
- Should unsupported features fail conversion by default or produce explicit warnings?
- Is a measured low-memory budget sufficient, or must retained heap remain
  independent of both row count and page count?
- What should happen when a single row exceeds the usable page height?
