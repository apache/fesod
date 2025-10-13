---
id: 'code-review-guide'
title: 'Code Review Guidelines'
---

This guide is for all committers and contributors that want to help with reviewing code contributions. Thank you for your effort - good reviews are one of the most important and crucial parts of an open source project. This guide should help the community to make reviews such that:

- Contributors have a good contribution experience.
- Our reviews are structured and check all important aspects of a contribution.
- We make sure to keep a high code quality in Apache Fesod (Incubating).
- We avoid situations where contributors and reviewers spend a lot of time refining a contribution that gets rejected later.

## Code Review Guidelines

- Always maintain a high standard of review so that the quality of the entire product can be better guaranteed.
- Changes to the architecture or the user interface need to be fully discussed by the community. This can be either in a mail group or in a GitHub issue.
- Test coverage. The added logic needs to be covered by a corresponding test. When the old code for which there is no test is changed, this requirement can be appropriately relaxed.
- Documentation. Newly added features must be documented, otherwise such code is not allowed.
- Readability of the code. If reviewers are not very clear about the logic of the code, then you can ask the contributor to explain the logic. And writing sufficient comments in the code to explain the logic is encouraged.
- Try to give a clear conclusion at the end of your comments, "approve" or "change request". If it's a minor issue, you can just leave a comment.
- Recommend to submit a "+1 Comment" rather than a "+1 Approve" to indicate that it looks good to me but I am not sure whether this part of the function is correct. It needs someone else's approve.
- Respect each other and learn from each other. Maintain a polite tone when commenting, try to give reasons for the suggestions.

## PR Review Guidelines

- Check whether the contribution is sufficiently well-described to support a good review
- The reviewer needs to perform a code-level review.
- Once a reviewer has commented on a PR, they need to keep following up on subsequent changes to that PR.
- A PR must get at least a +1 approved from committer who is not the author.
- After the first +1 to the PR, wait at least one working day before merging. The main purpose is to wait for the rest of the community to come to review.
- For architecture or user interface changes, a PR needs to get at least 3 +1's to merge.
- Regression cases and CI tests should pass before merging.
- Select "squash and merge" to merge.
- When there is a disagreement about a modification, try to discuss the resolution. If the discussion doesn't work out, it can be resolved by a vote in `dev@fesod.apache.org` by the majority rules.
