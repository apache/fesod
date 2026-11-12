---
id: 'contribution'
title: 'Contributor Guide'
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

Welcome every user and developer in the community to become contributors. Whether it's reporting issues, improving documentation, submitting code, or providing technical support, your participation will help make it better.

## Contribution Directions

There are many ways to contribute to this project:

- Fix typos
- Fix bugs
- Remove redundant code
- Add test cases
- Enhance functionality
- Add comments to improve code readability
- Optimize code structure
- Improve or refine documentation

### Principle

- Any pull request that contributes to the improvement of the project should be encouraged.
- Before contributing a new feature, please propose and discuss it in an `issue` or `discussion`. We will not merge features that have not been discussed and confirmed.

## Contribution Process

### Find tasks

Find the issue you are interested in! On our GitHub repo issue list, we often publish some issues with the label `good first issue` or `help wanted`. These issues welcome the help of contributors. Among them, `good first issues` tend to have low thresholds and are suitable for beginners.

Of course, if you have a good idea, you can also propose it directly on GitHub Discussion or contact with community.

### Discuss

Please refer to [Issue Feedback](./feedback.md) to create an issue or discussion and endeavour to reach consensus.

**Note**: To request an issue, please note that it is not just a "please assign it to me", you need to explain your understanding of the issue, and your design, and if possible, you need to provide your POC code.

### Implement

Implement the change according to the approach agreed upon in the issue.

- Only start working on the implementation if there is consensus on the approach (e.g. you are assigned to the ticket)
- If you are newer, can refer to [Code Contribution](./contribution/contribute-code.md) to setup a dev environment.

### Review

Create the pull request and work with the reviewer.

- Make sure no unrelated or unnecessary reformatting changes are included.
- After submitting the PR, the CI process will be triggered. Please make sure that it has passed the CI checks.
- Please ensure that the test passing.
- Please don't resolve conversation.

### Merge

- A committer of project checks if the contribution fulfills the requirements and merges the code to the codebase.

## Contribution Code

All improvements can be implemented through Pull Request (PR). Before submitting a Pull Request.
Please refer to [Code Contribution](./contribution/contribute-code.md) for information on how to contribute code.

## Contribution of Test Cases

Any contribution of test cases is encouraged, especially unit tests. It is recommended to create `XXXTest.java` files in the corresponding module's `test` directory, preferably using the `JUnit5` framework.

## Contribution Document

Documentation is an important component of the project official website and serves as a vital bridge between the project and the community. Please refer to [Docs Contribution](./contribution/contribute-doc.md) for information on how to contribute document.

## AI Policy

All AI-assisted contributions must adhere to our [AI Policy](./contribution/ai-policy.md).

## PR Adoption

To prevent long-stalled pull requests (PRs) from blocking project progress while respecting the original authors' hard work, the community supports adopting inactive PRs.

### Applicability

The PR adoption mechanism applies when **all** of the following conditions are met:

- **Review is progressing normally**: Reviewers have actively provided feedback and reviews; the stall is not due to a lack of community review capacity.
- **Long author inactivity**: The original author has been inactive for over **14 days** without addressing required items (e.g., unresolved CI failures, unanswered review comments, unmerged branch conflicts, or explicitly stating they have no time to proceed).
- **Adoption intent stated**: Another contributor has actively commented in the original PR expressing interest in taking it over.

### Workflow

1. **Polite Inquiry**: After the adopter leaves a comment in the original PR confirming their intent to take over, maintainers may add the `PR: waiting for feedback` label.
2. **Grace Period**: Give the original author a **7-day** grace period.
   - **Resume work**: If the original author resumes work with meaningful progress (e.g., addressing review questions, pushing code, or providing a clear schedule), the adoption process terminates, giving priority to the original author to finish.
   - **Early hand-off**: If the original author explicitly states they cannot continue, the new contributor may take over immediately without waiting for the 7 days to expire.
   - **Timeout**: If the author does not respond or show meaningful progress by the end of the 7-day window, the new contributor may proceed to work and submit a new PR.
3. **Link & Track**: At the beginning of the new PR description, please state `Related #<Original-PR-Number>` and apply the `PR: adopted` label. Maintainers will close the original PR with gratitude once the new PR is merged or the new approach is confirmed.

### Attribution

- **Code reuse**: The new PR **must preserve the original author's contribution**. Retain original commits whenever possible, and append the following to the end of the commit message: `Co-authored-by: Name <email>`
- **Full rewrite**: Preserving original commits is not required, but you **must link to the original PR in your PR description**.

### Anti-Poaching

- **Premature Takeover**: If someone opens a new PR prematurely before the grace period expires, maintainers will **suspend reviewing the new PR** until the waiting period ends.
- **Unwanted Takeover**: If the original author is still actively following up and a third party insists on opening a competing PR, maintainers **reserve the right to close the new PR immediately**.

## Other Ways to Contribute

Apart from directly contributing code, the following ways are also valuable support for Apache Fesod (Incubating):

- Answering other users' questions.
- Assisting in reviewing others' PRs.
- Providing improvement suggestions.
- Writing technical blogs to promote Apache Fesod (Incubating).
- Sharing project-related knowledge in the community.
