# Contribution Guidelines

Welcome every user and developer in the community to become contributors. Whether it's reporting issues, improving
documentation, submitting code, or providing technical support, your participation will help make the Apache Fesod (
Incubating) better.

---

## Contribution Directions

There are many ways to contribute to Apache Fesod (Incubating):

- Fix typos
- Fix bugs
- Remove redundant code
- Add test cases
- Enhance functionality
- Add comments to improve code readability
- Optimize code structure
- Improve or refine documentation

**Principle**:

- Any pull request that contributes to the improvement of the project should be encouraged.
- Before contributing a new feature, please propose and discuss it in an `issue` or `discussion`. We will not merge
  features that have not been discussed and confirmed.

---

## Contributing Code

All improvements can be implemented through Pull Request (PR). Before submitting a Pull Request, please familiarise
yourself with the following guidelines:

### Workspace Preparation

To develop Apache Fesod (Incubating), you need **Maven 3.9 or above** and **JDK (Java Development Kit) 17 or above**.
However, you must
use **Java 1.8** compatible language features during compilation to ensure Apache Fesod (Incubating) can run in
environments with Java
1.8 or above.

> You can use tools such as [SDKMAN](https://sdkman.io/) to configure multiple versions of the Java toolchain.

### Fork the repository

Ensure that you have registered a GitHub account and follow the steps below to configure your local development
environment:

**Fork the repository**: Click the `Fork` button on the Apache Fesod (
Incubating) [GitHub page](https://github.com/apache/fesod) to copy the project to your GitHub account.

```bash
https://github.com/<your-username>/fesod 
```

**Clone Repository**: Run the following command to clone the forked project to your local machine:

```bash
git clone git@github.com:<your-username>/fesod.git
```

**Set Upstream Repository**: Set the official repository as `upstream` for easy synchronization of updates:

```bash
git remote add upstream git@github.com:apahce/fesod.git
git remote set-url --push upstream no-pushing
```

Running `git remote -v` can verify if the configuration is correct.

### Branch definition

In Apache Fesod (Incubating), all contributions should be based on the `main` development branch. Additionally, there
are the following branch types:

- **release branches**: Used for version releases (e.g., `1.1.0`, `1.1.1`).
- **feature branches**: Used for developing major features.
- **hotfix branches**: Used for fixing critical bugs.

When submitting a PR, please ensure that the changes are based on the `main` branch.

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

To help reviewers quickly understand the content and purpose of the PR, use
the [pull_request_template](https://github.com/apache/fesod/blob/main/.github/pull_request_template.md). A
detailed description greatly improves code review efficiency.

---

## Contribution of Test Cases

Any contribution of test cases is encouraged, especially unit tests. It is recommended to create `XXXTest.java` files in
the corresponding module's `test` directory, preferably using the `JUnit5` framework.

---

## Contribution Document

Documentation is an important component of the Apache Fesod (Incubating) official website and serves as a vital bridge
between the
project and the community.The Apache Fesod (Incubating) official website is built
using [Docusaurus](https://docusaurus.io/), and the
documentation is maintained in the [website](https://github.com/apache/fesod/tree/main/website) directory.

### Requirements

- [Node.js](https://nodejs.org/en/download/) version 18.0 or above (which can be checked by running `node -v`). You can
  use [nvm](https://github.com/nvm-sh/nvm) for managing multiple Node versions on a single machine installed.
- When installing Node.js, you are recommended to check all checkboxes related to dependencies.

### Directory Structure Description

Docusaurus supports I18n. The main documentation directory structure that needs to be maintained is as follows:

```bash
.
├── community      # Community(English)
├── docs           # Documentation(English)
└── i18n           # I18n
    └── zh-cn
        ├── docusaurus-plugin-content-docs
        │   └── current   # Documentation(Simplified Chinese)
        └── docusaurus-plugin-content-docs-community
            └── current   # Community(Simplified Chinese)
```

The directory structure for single-language documents is as follows:

```bash
.
├── quickstart     # 1. Quick Start
├── read           # 2. Read
├── write          # 3. Write
├── fill           # 4. Fill
├── community      # 5. Community
└── help           # 6. FAQ
```

### Documentation Writing Guidelines

- Use file paths with the `.md` extension

```markdown
[Example](docs/quickstart/example.md)
```

- Use paths relative to the docs/ directory

```markdown
[Example](docs/quickstart/example.md)
```

- Image files must be stored in the `static/img` directory and referenced using relative directory paths.

```markdown
[img](/img/docs/fill/listFill_file.png)
```

### Preview and generate static files

Enter the `website` directory and execute the command

#### Installation

```bash
yarn install
```

#### Local Development

```bash
# English
yarn start

# Simplified Chinese
yarn start --locale zh-cn
```

This command starts a local development server and opens up a browser window. Most changes are reflected live without
having to restart the server.

> Only one language version can be run at a time.

#### Build(Optional)

```bash
yarn build
```

This command generates static content into the `build` directory and can be served using any static contents hosting
service.

### Document Format Inspection

Apache Fesod (Incubating) uses [markdownlint-cli2](https://github.com/DavidAnson/markdownlint-cli2) to check document
formatting. After writing the relevant Markdown articles, you can run the following command locally to pre-check whether
the Markdown formatting meets the requirements:

```bash
cd website && yarn

yarn md-lint

# If the documentation is wrong, you can use the following command to attempt an automatic repair.
yarn md-lint-fix
```

- For formatting rules for Markdown articles you can refer
  to: [Markdown-lint-rules](https://github.com/DavidAnson/markdownlint/blob/main/doc/Rules.md)
- Markdown format configuration file in the
  project: [.markdownlint-cli2.jsonc](https://github.com/apache/fesod/blob/main/website/.markdownlint-cli2.jsonc)

---

## Other Ways to Contribute

Apart from directly contributing code, the following ways are also valuable support for Apache Fesod (Incubating):

- Answering other users' questions.
- Assisting in reviewing others' PRs.
- Providing improvement suggestions.
- Writing technical blogs to promote.
- Sharing project-related knowledge in the community.

---

## Code Style

Apache Fesod (Incubating) uses [Spotless](https://github.com/diffplug/spotless) as its code formatting tool. Please
ensure you run the following command to automatically format your code before submitting:

```bash
mvn spotless:apply
```

---

Finally, thank you for your support of Apache Fesod (Incubating)!
