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

const sidebarsCommunity = {
    community: [
        {
            id: "community",
            type: "doc",
        },
        {
            id: "maturity",
            type: "doc",
        },
        {
            id: "brand",
            type: "doc",
        },
        {
            id: "feedback",
            type: "doc",
        },
        {
            type: 'category',
            label: 'Contribution',
            items: [
                'contribution',
                'contribution/commit-format',
                'contribution/contribute-code',
                'contribution/contribute-doc',
                'contribution/code-review-guide',
            ]
        },
        {
            type: 'category',
            label: 'Committers',
            items: [
                'committer/become-committer',
                'committer/icla',
                'committer/committer-onboarding',
            ]
        },
        {
            type: 'category',
            label: 'PPMC Members',
            items: [
                'pmc/become-pmc-member',
                'pmc/pmc-onboarding',
                'pmc/nominate-committer',
            ]
        },
        {
            type: 'category',
            label: 'Release',
            items: [
                'release/release-version',
                'release/verify-release',
            ]
        },
        {
            id: "security",
            type: "doc",
        },
    ]
};
export default sidebarsCommunity;
