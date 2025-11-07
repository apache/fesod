---
id: 'commit-format'
title: 'Commit Format Specification'
---

All improvements can be implemented through Pull Request (PR). Before submitting a Pull Request, please familiarise yourself with the following guidelines:

### Commit Rules

#### Commit Message

Please ensure that commit messages are clear and descriptive, use **English**, and do not exceed 100 characters.

The following types of commit messages are allowed and must follow the following format:

- **docs**: Update documentation, e.g., `docs: update README.md`
- **feature/feat**: New features, e.g., `feature: support for xxx`
- **bugfix/fix**: Bug fixes, e.g., `fix: fix NPE in the A class`
- **refactor**: Code refactoring (no functional changes), e.g., `refactor: optimise data processing logic`
- **style**: Code formatting, e.g., `style: update code style`
- **test**: Adding or improving tests, e.g., `test: add new test cases`
- **chore**: Changes to the build process or auxiliary tools, e.g., `chore: improve issue template`
- **dependency**: Modifications to third-party dependency libraries, e.g., `dependency: upgrade poi version to 5.4.1`

Avoid using vague commit messages like:

- ~~fixed issue~~
- ~~update code~~

For assistance, refer to [How to Write a Git Commit Message](http://chris.beams.io/posts/git-commit/).

#### Commit Content

Each commit should contain complete and reviewable changes, ensuring:

- Avoid committing overly large changes.
- Each commit content is independent and can pass CI tests.

Also, ensure the correct Git user information is configured when committing:

```bash
git config --get user.name
git config --get user.email
```

### PR Description

To help reviewers quickly understand the content and purpose of the PR, use the [pull_request_template](https://github.com/apache/fesod/blob/main/.github/pull_request_template.md). A detailed description greatly improves code review efficiency.
