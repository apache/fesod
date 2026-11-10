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

# Source attribution review for issue #1322

This records the attribution corrections requested in
[issue #1322](https://github.com/apache/fesod/issues/1322), following
[Justin Mclean's LEGAL-737 review](https://issues.apache.org/jira/browse/LEGAL-737?focusedCommentId=18141589&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-18141589).
It supplements the decision tracked in [#1106](https://github.com/apache/fesod/issues/1106).
It does not declare LEGAL-737 resolved or establish graduation approval.

## Scope and evidence

The baseline is Fesod commit `8005480a579b5c7cc9ea4e037429c7364d3db3a6`.
The review inventories all 393 Java files carrying the Alibaba attribution at that commit:
9 in `fesod-common` and 384 in `fesod-sheet`, including 71 test files.

The comparison source is the archived EasyExcel commit
[`aae9c61ab603c04331333782eedd2896d7bc5386`](https://github.com/alibaba/easyexcel/tree/aae9c61ab603c04331333782eedd2896d7bc5386)
from 29 October 2024, also used in the example supplied to LEGAL-737.
[easyexcel-provenance.tsv](./easyexcel-provenance.tsv) records each baseline file,
its corresponding path in that snapshot, and its disposition. These are source
relationships, not a claim that Alibaba wrote every line of an upstream file.

Of the baseline files, 387 have a same-named upstream candidate. A comparison
excluding comments, whitespace, package/import declarations and package relocation
found 205 exact token matches. The remaining files contain later changes; name
matching and similarity were used to locate evidence, not to determine ownership.
Renames, copied helpers, heavily changed tests and upstream ASF headers were checked
against source contents and Git history separately.

The corrections remove 17 inappropriate Alibaba attribution blocks. Fifteen are
ASF-origin helpers, one is a replacement Fesod test implementation, and one contains
Guava code. The other 376 retain the accepted layout: ASF license header first,
then the separate Alibaba attribution block for retained EasyExcel-derived code.
No author tags are restored, no Java implementation changes are made, and LICENSE
is unchanged.

## Files identified in LEGAL-737

For each row below, the upstream path is under
[`easyexcel-core/src/main/java/com/alibaba/excel/`](https://github.com/alibaba/easyexcel/tree/aae9c61ab603c04331333782eedd2896d7bc5386/easyexcel-core/src/main/java/com/alibaba/excel).
Each upstream file carries an ASF license header. Passing through EasyExcel does
not make that ASF-origin code Alibaba-owned. Remove the added Alibaba block and
retain the ASF header, as requested in the review.

| Current file | Pinned upstream path | Source evidence |
| --- | --- | --- |
| `fesod-common/.../util/StringUtils.java` | `util/StringUtils.java` | String operations and Commons-style API/Javadocs |
| `fesod-common/.../util/BooleanUtils.java` | `util/BooleanUtils.java` | `isTrue`, `isFalse` and Boolean conversion helpers |
| `fesod-common/.../util/ListUtils.java` | `util/ListUtils.java` | Collection factory methods and their Javadocs |
| `fesod-common/.../util/MapUtils.java` | `util/MapUtils.java` | Map factories, expected-size calculations and bounded map |
| `fesod-common/.../util/MemberUtils.java` | `util/MemberUtils.java` | Reflection accessibility helpers |
| `fesod-sheet/.../util/Validate.java` | `util/Validate.java` | `isTrue`/`notNull` overloads and Commons validation Javadocs |
| `fesod-sheet/.../util/DateUtils.java` | `util/DateUtils.java` | Date conversion/cache implementation; upstream ASF header |
| `fesod-sheet/.../metadata/format/DataFormatter.java` | `metadata/format/DataFormatter.java` | POI-derived formatter implementation |
| `fesod-sheet/.../util/FileUtils.java` | `util/FileUtils.java` | File and temporary-storage helpers; upstream ASF header |
| `fesod-sheet/.../util/ClassUtils.java` | `util/ClassUtils.java` | Reflection and metadata resolution; upstream ASF header |
| `fesod-sheet/.../util/FieldUtils.java` | `util/FieldUtils.java` | Reflection field lookup helpers |
| `fesod-sheet/.../analysis/v07/handlers/sax/SharedStringsTableHandler.java` | `analysis/v07/handlers/sax/SharedStringsTableHandler.java` | SAX shared-string parsing; upstream ASF header |

The manifest supplies full current paths; `...` above abbreviates the package root.
Some of these files contain later EasyExcel/Fesod changes. This patch follows the
specific disposition requested for the named files rather than treating an
intermediate copy or a later modification as a new ownership determination.

## Additional corrections

- **`FesodTempFileCreationStrategy.java`:** the renamed
  [`EasyExcelTempFileCreationStrategy.java`](https://github.com/alibaba/easyexcel/blob/aae9c61ab603c04331333782eedd2896d7bc5386/easyexcel-core/src/main/java/com/alibaba/excel/util/EasyExcelTempFileCreationStrategy.java)
  also has an ASF header. Its directory constants, lock, constructors and file/directory
  creation methods derive from POI's
  [`DefaultTempFileCreationStrategy`](https://github.com/apache/poi/blob/REL_5_5_1/poi/src/main/java/org/apache/poi/util/DefaultTempFileCreationStrategy.java).
  Retain the ASF header and remove the Alibaba block.
- **`fesod-common/.../ValidateUtils.java`:** a copied/renamed version of the
  ASF-origin `Validate` above, not evidence of a separate Alibaba implementation.
  Git commit [`50da70eb`](https://github.com/apache/fesod/commit/50da70eb)
  records the move into common utilities. Its constants, validation overloads and
  Javadocs correspond to
  [Commons Lang `Validate`](https://github.com/apache/commons-lang/blob/LANG_3_4/src/main/java/org/apache/commons/lang3/Validate.java).
  Retain the ASF header and remove the Alibaba block.
- **`fesod-common/.../IoUtils.java`:** its size-limited `toByteArray` checks,
  allocation/read loop, EOF handling and copy-count overflow handling correspond to
  [Commons IO `IOUtils`](https://github.com/apache/commons-io/blob/commons-io-2.5/src/main/java/org/apache/commons/io/IOUtils.java).
  The current code corrects spelling in the premature-EOF message and inlines
  Commons IO's copy loop. Although EasyExcel's intermediary lacks an ASF header,
  the original implementation provides the provenance. Retain the ASF header and
  remove the Alibaba block.
- **`fesod-common/.../IntUtils.java`:** `MAX_POWER_OF_TWO`, `saturatedCast` and
  their Javadocs directly match
  [Guava 18.0 `Ints`](https://github.com/google/guava/blob/798803f026bb9517bcf4e0e9ab2d2e0345023182/guava/src/com/google/common/primitives/Ints.java).
  This is a third-party-origin correction, not an ASF-origin claim. Replace the
  Alibaba block with the original `Copyright (C) 2008 The Guava Authors` attribution.
  Guava's source is Apache-2.0 licensed; no additional license text is introduced.
- **`DateWindowingListener.java`:**
  [`fd575418` (#901)](https://github.com/apache/fesod/commit/fd575418)
  replaced the list/assertion/logging implementation of `SimpleDataSheetNameListener`
  with a new listener that captures `actualUse1904windowing`. The remaining common
  elements are the listener interface methods, which are API scaffolding; the
  upstream implementation is gone. Git's rename detection alone would incorrectly
  retain the old attribution. Keep the ASF header and remove the Alibaba block.

## Remaining upstream ASF headers and renamed files

The pinned EasyExcel snapshot has 14 Java files with an ASF header. All 13
production counterparts are covered above, including the renamed temporary-file
strategy. The fourteenth,
[`easyexcel-test/.../temp/dataformat/DataFormatter1.java`](https://github.com/alibaba/easyexcel/blob/aae9c61ab603c04331333782eedd2896d7bc5386/easyexcel-test/src/test/java/com/alibaba/easyexcel/test/temp/dataformat/DataFormatter1.java),
has no retained attributed counterpart in Fesod. It does not require a removal.

The other basename mismatches are genuine renames: `WriteSheetData` retains
`RepetitionData`, `TitleData` retains `MultipleSheetsData`, and `FesodSheetConstants`
retains `EasyExcelConstants`. They keep their attribution. The manifest identifies
their pinned paths. Heavily modernized tests were checked for retained implementation;
for example, `ConverterTest` still contains the upstream `95.62F` float-write
regression even though its overall token similarity is low.

Files introduced independently for FastExcel/Fesod that did not carry the Alibaba
block are outside the manifest and receive no new Alibaba notice. Later changes to
retained legacy implementations do not by themselves justify removing attribution.

## Copyright-year alignment

Keep the existing `2018-2024` span on applicable source attribution blocks and use
that same span in the root, `fesod-common`, and `fesod-sheet` NOTICE files.
EasyExcel's published history includes
[code in 2018](https://github.com/alibaba/easyexcel/tree/8484770413f3377bf0d98532f283e4e117d71169)
and the pinned 2024 snapshot. The range describes the legacy project's history;
it does not assert that every file was created in 2018 or claim Alibaba ownership
of later FastExcel/Fesod work. The earlier `2022-2024` NOTICE span excluded that
published early history. ASF, Maven Wrapper and Spring attribution years are unchanged.

## Verification and follow-up

For each changed Java file, compare the baseline and result after removing only
the exact attribution comments. The remaining bytes must be identical. Verify
that all 393 baseline files have a manifest entry, all 376 retained Alibaba blocks
still exist, the 15 ASF-origin files have only the ASF header, the replacement
listener has no Alibaba block, the Guava attribution is present, and all three
NOTICE files use the same legacy year range.

Run the repository's Hawkeye license check, Spotless, Maven validation/packaging
and Apache RAT. Comment-only changes require no new functional tests.

The remediation was checked with:

```sh
hawkeye check
./mvnw -B -pl fesod-common,fesod-shaded,fesod-sheet -DskipTests package spotless:check
./mvnw -B apache-rat:check
```

Hawkeye reported 619 files with no conflicts. Packaging, Checkstyle and Spotless
passed. RAT reported no unapproved or unknown licenses across all six reactor
projects. The generated common and sheet JARs both contain the aligned NOTICE.
An exact before/after comparison confirmed that all 17 changed Java files differ
only in the attribution comments described above. Functional tests were not rerun.

The remediation PR and this evidence should be linked from #1322 and #1106.
The PPMC should report the result to LEGAL-737 and request confirmation. This record
does not substitute for that confirmation or imply the open ticket is resolved.
