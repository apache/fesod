# Apache Fesod (Incubating) Website

This website is built using [Docusaurus](https://docusaurus.io/), a modern static website generator.

## Requirements

- [Node.js](https://nodejs.org/en/download/) version 20.0 or above (which can be checked by running `node -v`). You can
  use [nvm](https://github.com/nvm-sh/nvm) for managing multiple Node versions on a single machine installed.
- When installing Node.js, you are recommended to check all checkboxes related to dependencies.

## Installation

```bash
pnpm install
```

## Local Development

Preview the English website locally:

```bash
pnpm start
```

Preview the Chinese website locally:

```bash
pnpm start --locale zh-cn
```

This command starts a local development server and opens up a browser window. Most changes are reflected live without
having to restart the server.

## Team Page

### Member

Update the member information in `src/pages/team/data/member.json` File.

### Avatar

```console
pnpm github-avatar
```

This command will fetch the base64 string of the GitHub avatar from file
`src/pages/team/data/member.json`, and store the result in the `src/pages/team/data/` directory. The operation might
take a little while.

## Internationalization

To write Chinese translation files, run:

```bash
pnpm write-translations --locale zh-cn
```

To write English translation files, run:

```bash
pnpm write-translations --locale en
```

## Build

```bash
pnpm build
```

This command generates static content into the `build` directory and can be served using any static contents hosting
service.

## Directory Structure

```bash
|-- community             # community directory
|-- docs                  # document directory
|-- i18n
|   `-- zh-cn             # internationalized chinese
|       |-- code.json
|       |-- docusaurus-plugin-content-docs
|       |-- docusaurus-plugin-content-community
|       `-- docusaurus-theme-classic
|-- src
|-- static
|   |-- img               # picture static resource
|   |   |-- blog          # blog picture
|   |   |-- docs          # document picture
|   |   |-- index         # homepage picture
|-- docusaurus.config.js
|-- sidebars.js           # document sidebar menu configuration
|-- sidebarsCommunity.js  # community document sidebar menu configuration
```
