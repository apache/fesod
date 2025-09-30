import React from 'react';
import BrowserOnly from '@docusaurus/BrowserOnly';
import config from "./team.json";
import Layout from '@theme/Layout';
import './index.css';
import Github from "./github.svg"
import Translate from '@docusaurus/Translate'
import useBaseUrl from '@docusaurus/useBaseUrl';

/**
 * Derived from https://github.com/apache/streampark-website/tree/dev/src/pages/team
 */
export default function () {
    const dataSource = config;

    function getGitName(url) {
        return '@' + url.replace('https://github.com/', '');
    }

    function avatarUrl(id) {
        return useBaseUrl('/img/team/' + id + '.png');
    }

    return (
        <BrowserOnly>
            {() => {
                return <Layout>
                    <div className="block team_page" style={{padding: "10px 0 30px"}}>
                        <h1><Translate>team.name</Translate></h1>
                        <p className="team_desc team_indent"><Translate>team.desc</Translate></p>

                        <h2 className="team_title">
                            PPMC
                            <small className="desc"><Translate>team.tip</Translate></small>
                        </h2>
                        <div className="team-row">
                            {
                                dataSource.pmc.map((item, i) => (
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
                                                    <a className="team-link" href={item.gitUrl}>
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
                                dataSource.committer.map((item, i) => (
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
                                                    <a className="team-link" href={item.gitUrl}>
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
                    ;
            }}

        </BrowserOnly>

    )
        ;
}
