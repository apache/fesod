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
import Heading from '@theme/Heading';
import styles from './styles.module.css';
import {translate} from '@docusaurus/Translate';

const FeatureList = [
    {
        title: translate(
            {
                id: 'homepage.feature.quick.title',
            },
        ),
        Svg: require('@site/static/img/index/undraw_spreadsheet.svg').default,
        description: translate(
            {
                id: 'homepage.feature.quick.description',
            },
        ),
    },
    {
        title: translate(
            {
                id: 'homepage.feature.simple.title',
            },
        ),
        Svg: require('@site/static/img/index/undraw_programming.svg').default,
        description: translate(
            {
                id: 'homepage.feature.simple.description',
            },
        ),
    },
    {
        title: translate(
            {
                id: 'homepage.feature.bigdata',
            },
        ),
        Svg: require('@site/static/img/index/undraw_files.svg').default,
        description: translate(
            {
                id: 'homepage.feature.bigdata.description',
            },
        ),
    },
];

function Feature({Svg, title, description}) {
    return (
        <div className={clsx('col col--4')}>
            <div className="text--center">
                <Svg className={styles.featureSvg} role="img"/>
            </div>
            <div className="text--center padding-horiz--md">
                <Heading as="h3">{title}</Heading>
                <p className="text--left">{description}</p>
            </div>
        </div>
    );
}

export default function HomepageFeatures() {
    return (
        <section className={styles.features}>
            <div className="container">
                <div className="row">
                    {FeatureList.map((props, idx) => (
                        <Feature key={idx} {...props} />
                    ))}
                </div>
            </div>
        </section>
    );
}
