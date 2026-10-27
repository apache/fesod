---
title: "Becoming an Apache Fesod Committer"
description: "Reflections on joining the Apache Fesod (Incubating) team as a committer, lessons learned along the way, and what's next."
authors: [nkuprins]
tags: [opensource, apache, committer, fesod]
---

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

![Apache Fesod New Committer](/img/blog/apache-fesod-new-committer.svg)

> I'm glad to share that I've become a Committer for Apache Fesod. I'm looking forward to contributing even more to the project and its community.

<!--truncate-->

## Acknowledgments

First and foremost, a sincere thank you to **@delei** and **@bengbengbalabalabeng**.

@delei reviewed my very first pull request and walked me through the ropes of Apache's workflow. @bengbengbalabalabeng patiently reviewed most of my subsequent contributions, offering feedback that consistently pushed my initial implementations to be cleaner and more robust.

Thank you as well to the rest of the PPMC and the broader community for placing their trust in me.

## How It Started

My background is in software performance and energy efficiency - during my studies, I researched how design choices affect a system's energy consumption. That curiosity about where software wastes resources is what eventually led me to Fesod. My first contribution was a small cleanup: stripping out a redundant buffer copy in a utility class.

The fix itself was only a few lines of code, but the process gave me a clear picture of how the project operates - discussing the issue upfront, agreeing on the approach, and submitting a tight, well-tested PR. Once that first commit was merged, I was hooked.

## What I've Worked On

Over the past few months, I focused primarily on correctness, testing, and developer experience:

* **Correctness:** Fixed a range of subtle data-integrity bugs in the read path - duplicate rows, stale cached styles, and lost or mangled cell content.
* **Testing:** Added regression tests to pin down existing behavior before touching sensitive parsing logic.
* **Documentation & Site:** Improved the developer guides and refreshed parts of the project website.
* **Refactoring:** Trimmed boilerplate, tightened encapsulation, and extracted logic into dedicated helpers for readability.
* **Code Review:** Reviewed pull requests from other contributors, helping shape converter additions and other feature work.

## Key Takeaways

Working on Fesod has shaped how I approach software design in general:

1. **Small fixes build trust faster than big ones.** My first PR was a tiny cleanup, not an impressive feature - but it's what opened the door to everything that came after.
2. **Review is a two-way skill.** Once I started reviewing other contributors' PRs, I learned what makes a change easy or hard to review from the other side of the process.
3. **Consistency beats personal taste.** A file written in a consistent style is easier for the next person than one written "better" by someone's individual preference.

## Looking Ahead

Becoming a committer isn't a destination - it's just a shift in responsibility.

Moving forward, alongside continuing to contribute to Fesod's codebase, I plan to spend more time reviewing pull requests, working through reported issues, and helping new contributors get their first PRs over the finish line.
