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

import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import HomepageFeatures from '@site/src/components/HomepageFeatures';
import Translate from '@docusaurus/Translate';

import Heading from '@theme/Heading';
import styles from './index.module.css';

const COLUMN_COUNT = 120;
const ROW_COUNT = 18;

function columnLetter(index) {
    let n = index + 1;
    let label = '';
    while (n > 0) {
        const remainder = (n - 1) % 26;
        label = String.fromCharCode(65 + remainder) + label;
        n = Math.floor((n - 1) / 26);
    }
    return label;
}

function HomepageHeader() {
    const {siteConfig} = useDocusaurusContext();
    return (
        <header className={clsx('hero hero--primary', styles.heroBanner)}>
            <div className={styles.gridOverlay} aria-hidden="true" />
            <div className={styles.colLabels} aria-hidden="true">
                {Array.from({length: COLUMN_COUNT}, (_, i) => (
                    <span key={i}>{columnLetter(i)}</span>
                ))}
            </div>
            <div className={styles.rowLabels} aria-hidden="true">
                {Array.from({length: ROW_COUNT}, (_, i) => (
                    <span key={i}>{i + 1}</span>
                ))}
            </div>
            <div className="container">
                <Heading as="h1" className="hero__title">
                    {siteConfig.title}
                </Heading>
                <p className="hero__subtitle">
                    <Translate>site.description</Translate>
                </p>
                <div className={styles.buttons}>
                    <Link
                        className={clsx("button button--secondary button--lg", styles.buttonWidth)}
                        to="/docs/quickstart/guide">
                        <Translate>quickstart</Translate>
                    </Link>

                    <Link
                        className={clsx("button button--secondary button--lg", styles.buttonWidth, styles.buttonWithIcon)}
                        to="https://github.com/apache/fesod">
                        <img
                            src="img/github_icon.svg"
                            alt="GitHub"
                            className={styles.buttonIcon}
                        />
                        <Translate>github</Translate>
                    </Link>
                </div>
            </div>
        </header>
    );
}

export default function Home() {
    const {siteConfig} = useDocusaurusContext();
    return (
        <Layout
            title=""
            description="Apache Fesod (Incubating) Official Documentation | Fast, concise, Java tool for processing spreadsheet files that solves memory overflow issues with large files <head />">
            <HomepageHeader/>
            <main>
                <HomepageFeatures/>
            </main>
        </Layout>
    );
}
