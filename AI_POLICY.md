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

# AI POLICY

We welcome contributors using AI as a tool for coding and learning. However, **using tools does not replace human thought and responsibility**. Every contributor remains ultimately responsible for the code and comments they submit.

A core value of open source is **human collaboration, knowledge sharing, and trust**. Maintainer review capacity is very limited; we cannot accept a dynamic where reviewers become proxies driving or debugging the contributor's AI tool. To ensure project quality and efficient community collaboration, please adhere to the following guidelines:

---

## Pull Request

- Before submitting a PR, the author must personally read the entire PR and **understand the core design and implementation end-to-end**. Authors must be prepared to justify and help reviewers understand their changes during review. An "AI dump"—submitting a PR composed largely of AI-generated code and descriptions that the author has not digested or understood—is unacceptable. Even if the code runs, it offloads the entire burden of comprehension onto reviewers, and the author gains nothing from the process.
- It is completely normal to not fully understand every detail of AI-generated code. Please proactively call out these unknowns and assumptions in your PR description so reviewers can work with you to address any concerns.
- **Using autonomous agents to create PRs, whether automatically or in bulk, is strictly prohibited**. We require a human in the loop; any automated contribution lacking meaningful human involvement will be closed immediately.

---

## Communication & Discussion

Code review is not just about merging code; it is about engaging with **you as a person** (rather than your AI tool) to help you grow and integrate into the community. Whether opening an issue, describing a PR, or answering questions during review, please express yourself in your own words. **Automated AI-generated comments and copy-pasted AI responses are strictly prohibited when communicating with reviewers.** Reviewers may collapse or hide comments they believe to be AI-generated.

Unreviewed AI replies place the burden of discernment entirely on the reviewer and deny you the opportunity to learn. Common examples of such unhelpful replies include mechanically summarizing the diff instead of addressing the actual question, listing local commands and checks that CI already covers, or evading design questions with generic descriptions of code changes.

We fully understand that non-native English speakers may use AI for reading, polishing, or translating comments. Using AI to assist with language is completely acceptable, provided the final text accurately reflects your own voice, ideas, and intent.

---

## AI-Assisted Reviews

The same standards apply when reviewing others' code with AI assistance: you must personally read and understand every comment you post. Raw AI review output is often verbose and cluttered with trivial details, placing a heavy cognitive load on others to figure out what is actually being asked. If you use AI to assist your reviews, please filter out irrelevant points or details you do not understand, and rephrase the rest in your own words so that each comment delivers clear, actionable, and specific feedback.

---

## Better Ways to Contribute

Rather than hastily submitting an unreviewed AI code patch, we strongly encourage opening a **high-quality issue** instead: describe the problem in clear terms and provide a **minimal, reproducible example (MRE)** focused on end-user-visible behavior. This significantly reduces the investigation burden on maintainers and lays a solid foundation for effective collaboration.
