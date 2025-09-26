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

// There are various equivalent ways to declare your Docusaurus config.
// See: https://docusaurus.io/docs/api/docusaurus-config
import { themes as prismThemes } from 'prism-react-renderer';

const branch = 'main';
const repoUrl = `https://github.com/apache/fesod`;

const config = {
    title: 'Fesod',
    favicon: 'img/favicon.ico',

    url: 'https://fesod.apache.org',
    baseUrl: "/",

    trailingSlash: true,

    future: {
        // Improve compatibility with the upcoming Docusaurus v4
        v4: true,
    },

    markdown: {
        hooks: {
            onBrokenLinks: 'throw',
            onBrokenMarkdownLinks: 'throw',
        }
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
                content: 'apache, fesod, poi, opensource',
            }
        ],
        footer: {
            style: 'dark',
            links: [],
            copyright: `Copyright © ${new Date().getFullYear()} The Apache Software Foundation, Licensed under the Apache License, Version 2.0.`,
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
