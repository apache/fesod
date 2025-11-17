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

// @ts-check
// This runs in Node.js - Don't use client-side code here (browser APIs, JSX...)
/**
 * Creating a sidebar enables you to:
 - create an ordered group of docs
 - render a sidebar for each doc of that group
 - provide next/previous navigation

 The sidebars can be generated from the filesystem, or explicitly defined here.

 Create as many sidebars as you want.

 @type {import('@docusaurus/plugin-content-docs').SidebarsConfig}
 */
const sidebars = {
    // By default, Docusaurus generates a sidebar from the docs folder structure
    docs: [
        {
            id: "introduce",
            type: "doc",
        }, {
            type: 'category',
            label: 'quickstart',
            items: [
                'quickstart/guide',
                'quickstart/simple-example'
            ]
        }, {
            type: 'category',
            label: 'fesod-sheet',
            items: [
                {
                    type: 'category',
                    label: 'read',
                    items: [
                        'sheet/read/simple',
                        'sheet/read/sheet',
                        'sheet/read/num-rows',
                        'sheet/read/csv',
                        'sheet/read/head',
                        'sheet/read/extra',
                        'sheet/read/exception',
                        'sheet/read/pojo',
                        'sheet/read/converter',
                        'sheet/read/spring'
                    ],
                },
                {
                    type: 'category',
                    label: 'write',
                    items: [
                        'sheet/write/simple',
                        'sheet/write/sheet',
                        'sheet/write/image',
                        'sheet/write/csv',
                        'sheet/write/head',
                        'sheet/write/extra',
                        'sheet/write/format',
                        'sheet/write/pojo',
                        'sheet/write/style',
                        'sheet/write/spring'
                    ]
                },
                {
                    type: 'category',
                    label: 'fill',
                    items: [
                        'sheet/fill/fill'
                    ]
                },
                {
                    type: 'category',
                    label: 'help',
                    items: [
                        'sheet/help/annotation',
                        'sheet/help/core-class',
                        'sheet/help/parameter',
                        'sheet/help/large-data',
                        "sheet/help/faq"
                    ]
                }
            ]
        }
    ]
};

export default sidebars;
