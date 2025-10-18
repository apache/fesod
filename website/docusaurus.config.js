/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import {themes as prismThemes} from 'prism-react-renderer';

const branch = 'main';
const repoUrl = `https://github.com/apache/fesod`;

const config = {
    title: 'Apache Fesod (Incubating)',
    favicon: 'img/favicon.ico',
    url: 'https://fesod.apache.org',
    baseUrl: '/',
    trailingSlash: true,

    future: {
        v4: true,
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
                blog: {
                    showReadingTime: false,
                    postsPerPage: 15,
                    feedOptions: {
                        type: 'all',
                    },
                    editUrl: `${repoUrl}/edit/${branch}/website/`,
                    editLocalizedFiles: true,
                    blogSidebarCount: 'ALL',
                    authorsMapPath: "authors.json"
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
        'docusaurus-plugin-matomo'
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
                    position: 'right',
                    to: '/docs/',
                },
                {
                    label: 'Blog',
                    position: 'right',
                    to: '/blog/',
                },
                {
                    label: 'Download',
                    position: 'right',
                    to: '/docs/download',
                },
                {
                    label: 'Team',
                    position: 'right',
                    to: '/team',
                },
                {
                    label: 'Community',
                    position: 'right',
                    to: '/community',
                },
                {
                    label: 'ASF',
                    type: 'dropdown',
                    position: 'right',
                    items: [
                        {
                            label: 'Foundation',
                            to: 'https://www.apache.org/',
                        },
                        {
                            label: 'License',
                            to: 'https://www.apache.org/licenses/',
                        },
                        {
                            label: 'Events',
                            to: 'https://www.apache.org/events/current-event.html',
                        },
                        {
                            label: 'Privacy',
                            to: 'https://privacy.apache.org/policies/privacy-policy-public.html',
                        },
                        {
                            label: 'Security',
                            to: 'https://www.apache.org/security/',
                        },
                        {
                            label: 'Sponsorship',
                            to: 'https://www.apache.org/foundation/sponsorship.html',
                        },
                        {
                            label: 'Thanks',
                            to: 'https://www.apache.org/foundation/thanks.html',
                        },
                        {
                            label: 'Code of Conduct',
                            to: 'https://www.apache.org/foundation/policies/conduct.html',
                        },
                    ]
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
        blog: {
            sidebar: {
                groupByYear: true,
            },
        },
        metadata: [
            {
                name: 'keywords',
                content: 'apache, fesod, poi, opensource, fastexcel, easyexcel',
            }
        ],
        footer: {
            logo: {
                width: 200,
                src: '/img/apache-incubator.svg',
                href: 'https://incubator.apache.org/',
                alt: 'Apache Incubator logo'
            },
            links: [],
            copyright: `<br><p>Apache Fesod (Incubating) is an effort undergoing incubation at The Apache Software Foundation (ASF), sponsored by the Apache Incubator. Incubation is required of all newly accepted projects until a further review indicates that the infrastructure, communications, and decision making process have stabilized in a manner consistent with other successful ASF projects. While incubation status is not necessarily a reflection of the completeness or stability of the code, it does indicate that the project has yet to be fully endorsed by the ASF.</p><p>Copyright © ${new Date().getFullYear()} The Apache Software Foundation, Licensed under the Apache License, Version 2.0.</p><p>Apache, the names of Apache projects, and the feather logo are either registered trademarks or trademarks of the Apache Software Foundation in the United States and/or other countries. All other marks mentioned may be trademarks or registered trademarks of their respective owners.</p>`,
        },
        prism: {
            theme: prismThemes.github,
            darkTheme: prismThemes.oneDark,
            additionalLanguages: ['java', 'bash']
        },
        matomo: {
            matomoUrl: 'https://analytics.apache.org/',
            siteId: '83',
            phpLoader: 'matomo.php',
            jsLoader: 'matomo.js',
        }
    }),
    themes: [
        '@docusaurus/theme-mermaid',
        [
            require.resolve("@easyops-cn/docusaurus-search-local"),
            /** @type {import("@easyops-cn/docusaurus-search-local").PluginOptions} */
            ({
                hashed: true,
                indexDocs: true,
                indexBlog: true,
                indexPages: true,
                highlightSearchTermsOnTargetPage: false,
                explicitSearchResultPath: true,
                searchBarPosition: "right",
                searchBarShortcutHint: false,
                language: ["zh", "en"],
                hideSearchBarWithNoSearchContext: true,
            }),
        ],
    ],
    headTags: [],
    markdown: {
        format: 'md',
        mermaid: true,
    }
};

export default config;
