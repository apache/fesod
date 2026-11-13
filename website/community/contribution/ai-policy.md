---
id: 'ai-policy'
title: 'AI Policy'
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

# AI Policy

We welcome contributors using AI as a tool for coding and learning. However, **using tools does not replace human thought and responsibility**. Every contributor remains ultimately responsible for the code and comments they submit.

A core value of open source is **human collaboration, knowledge sharing, and trust**. Maintainer review capacity is very limited; we cannot accept a dynamic where reviewers become proxies driving or debugging the contributor's AI tool. To ensure project quality and efficient community collaboration, please adhere to the following guidelines:

## Pull Request

- Before submitting a PR, the author must personally read the code, understand the core design end-to-end, and be able to clearly explain their changes during review. Submitting unverified or undigested content is strictly prohibited; even if the code runs, an author who cannot explain its principles is essentially offloading the entire burden of comprehension and verification onto reviewers, and the author gains no growth from the process.
- It is completely normal to not fully understand every detail of AI-generated code. Please proactively call out these unknowns and assumptions in your PR description so reviewers can work with you to address any concerns.
- We strictly prohibit automatic, unattended, bot-owned, or bulk submissions that lack meaningful human review. We require every submission to have a human in the loop who genuinely understands the work produced by AI and takes personal responsibility for it.
- All contributions must strictly comply with the [ASF Generative Tooling Guidance](https://www.apache.org/legal/generative-tooling.html).

## Communication & Discussion

Code review is not just about merging code; it is about engaging with **you as a person** (rather than your AI tool) to help you grow and integrate into the community. Whether opening an issue, describing a PR, or answering questions during review, please express yourself in your own words, and never copy-paste unreviewed, AI-generated content directly.

Unreviewed AI replies place the burden of discernment entirely on the reviewer and deny you the opportunity to learn. Common examples of such unhelpful replies include mechanically summarizing the diff instead of addressing the actual question, listing local commands and checks that CI already covers, or evading design questions with generic descriptions of code changes.

We fully understand that non-native English speakers may use AI for reading, polishing, or translating comments. Using AI to assist with language is completely acceptable, provided the final text accurately reflects your own voice, ideas, and intent.

## AI-Assisted Reviews

The same standards apply when reviewing others' code with AI assistance: you must personally read and understand every comment you post. Raw AI review output is often verbose and cluttered with trivial details, placing a heavy cognitive load on others to figure out what is actually being asked. If you use AI to assist your reviews, please filter out irrelevant points or details you do not understand, and rephrase the rest in your own words so that each comment delivers clear, actionable, and specific feedback.

## Better Ways to Contribute

Rather than hastily submitting an unreviewed AI code patch, we strongly encourage opening a **high-quality issue** instead: describe the problem in clear terms and provide a **minimal, reproducible example (MRE)** focused on end-user-visible behavior. This significantly reduces the investigation burden on maintainers and lays a solid foundation for effective collaboration.
