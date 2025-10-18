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

import React from 'react';
import BrowserOnly from '@docusaurus/BrowserOnly';
import TeamData from "./data/team.json";
import AvatarData from "./data/github-avatar.json";
import Layout from '@theme/Layout';
import './index.css';
import Github from "./github.svg"
import Translate from '@docusaurus/Translate'

/**
 * Derived from https://github.com/apache/streampark-website/tree/dev/src/pages/team
 */
export default function () {
    const teamData = TeamData;
    const avatarData = AvatarData;

    function getGitName(url) {
        return '@' + url.replace('https://github.com/', '');
    }

    function avatarUrl(id) {
        const avatarObj = avatarData.find((item) => item.id === id);
        if (avatarObj) {
            return "data:image/png;base64," + avatarObj.avatar_base64;
        }
        return "";
    }

    return (
        <BrowserOnly>
            {() => {
                return <Layout>
                    <div className="container team_page">
                        <h1><Translate>team.name</Translate></h1>
                        <p className="team_desc team_indent"><Translate>team.desc</Translate></p>

                        <h2 className="team_title">
                            PPMC
                            <small className="desc"><Translate>team.tip</Translate></small>
                        </h2>
                        <div className="team-row">
                            {
                                teamData.pmc.map((item, i) => (
                                    <div className='team-box' key={i} data-aos="fade-up" data-aos-delay={i * 100}>
                                        <div style={{textAlign: "center"}}>
                                            <div style={{overflow: "hidden", zIndex: 1}}>
                                                <img className="team-user-img" src={avatarUrl(item.githubId)} title=""
                                                     alt=""/>
                                            </div>
                                            <div className={item.isMentor ? 'team-mentor bg-team' : 'bg-team'}>
                                                <h5 className="team-name">{item.name}</h5>
                                                <small>{getGitName(item.gitUrl)}</small>
                                                <div>
                                                    <a className="team-link" href={item.gitUrl} target="_blank"
                                                       rel="noreferrer">
                                                        <Github className="github-icon"/>
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                ))
                            }
                        </div>
                        <h2 className="team_title">
                            Committer
                            <small className="desc"><Translate>team.tip</Translate></small>
                        </h2>
                        <div className="team-row">
                            {
                                teamData.committer.map((item, i) => (
                                    <div className='team-box' key={i} data-aos="fade-up" data-aos-delay={i * 100}>
                                        <div style={{textAlign: "center"}}>
                                            <div style={{overflow: "hidden", zIndex: 1}}>
                                                <img className="team-user-img" src={avatarUrl(item.githubId)}
                                                     title=""
                                                     alt=""/>
                                            </div>
                                            <div className="bg-team">
                                                <h5 className="team-name">{item.name}</h5>
                                                <small>{getGitName(item.gitUrl)}</small>
                                                <div>
                                                    <a className="team-link" href={item.gitUrl} target="_blank"
                                                       rel="noreferrer">
                                                        <Github className="github-icon"/>
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                ))
                            }
                        </div>
                        <h2 className="team_title">
                            Contributor
                        </h2>
                        <p><Translate>team.thanks</Translate></p>
                        <a _target="_blank" href="https://github.com/apache/fesod/graphs/contributors">GitHub
                            Contributors</a>
                    </div>
                </Layout>
            }}
        </BrowserOnly>
    )
}
