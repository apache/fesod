// There are various equivalent ways to declare your Docusaurus config.
// See: https://docusaurus.io/docs/api/docusaurus-config
import {themes as prismThemes} from 'prism-react-renderer';

const branch = 'main';
const repoUrl = `https://github.com/fast-excel/fesod`;

const config = {
    title: 'Fesod',
    favicon: 'img/favicon.ico',

    // Set the production url of your site here
    url: 'https://fast-excel.github.io',
    // Set the /<baseUrl>/ pathname under which your site is served
    // For GitHub pages deployment, it is often '/<projectName>/'
    baseUrl: '/fesod/',

    // GitHub pages deployment config.
    organizationName: 'fast-excel',
    projectName: 'fesod',
    deploymentBranch: 'gh-pages',

    onBrokenLinks: 'throw',
    onBrokenMarkdownLinks: 'warn',

    future: {
        v4: true,
        // Improve compatibility with the upcoming Docusaurus v4
    },
    customFields: {
        repoUrl,
    },
    i18n: {
        defaultLocale: 'en',
        locales: ['en', 'zh-cn'],
        localeConfigs: {
            en: {
                label: 'English',
                htmlLang: 'en',
                path: 'en',
            },
            'zh-cn': {
                label: '简体中文',
                path: 'zh-cn',
            },
        },
    },
    presets: [
        [
            'classic',
            {
                docs: {
                    sidebarPath: './sidebars.js',
                    editUrl: `${repoUrl}/edit/${branch}/website/`,
                    editLocalizedFiles: true
                },
                theme: {
                    customCss: './src/css/custom.css'
                },
            }
        ],
    ],
    plugins: [
        [
            '@docusaurus/plugin-content-docs',
            {
                id: 'community',
                path: 'community',
                routeBasePath: 'community',
                sidebarPath: './sidebarsCommunity.js',
                editUrl: `${repoUrl}/edit/${branch}/website/`,
                editLocalizedFiles: true,
            },
        ],
    ],
    themeConfig: ({
        image: 'img/logo.svg',
        navbar: {
            title: '',
            logo: {
                alt: '',
                src: 'img/logo.svg',
            },
            items: [
                {
                    label: 'Docs',
                    position: 'left',
                    to: '/docs/',
                },
                {
                    label: 'Community',
                    position: 'left',
                    to: '/community/contact',
                },
                {
                    type: 'localeDropdown',
                    position: 'right',
                },
                {
                    href: repoUrl,
                    position: 'right',
                    className: 'header-github-link',
                },
            ],
        },
        metadata: [
            {
                name: 'keywords',
                content: 'fastexcel, fast-excel, excel, poi, opensource',
            }
        ],
        footer: {
            style: 'dark',
            links: [],
            copyright: `Copyright © ${new Date().getFullYear()} FastExcel, Licensed under the Apache License, Version 2.0.`,
        },
        prism: {
            theme: prismThemes.github,
            darkTheme: prismThemes.oneDark,
            additionalLanguages: ['java', 'bash']
        }
    }),
    themes: ['@docusaurus/theme-mermaid'],
    markdown: {
        format: 'md',
        mermaid: true,
    }
};

export default config;
