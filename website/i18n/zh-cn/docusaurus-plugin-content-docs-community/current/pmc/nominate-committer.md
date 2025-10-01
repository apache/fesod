---
id: 'nominate-committer'
title: '提名新 Committer'
---

本文件主要介绍PPMC成员如何提名新提交者。

## 模板

请注意，模板中有三个占位符在使用之前应该替换：

- CANDIDATE_NAME: 候选人英文真实姓名，而非 Github 名称或 Id
- CANDIDATE_GITHUB_ID: 候选人 Github ID
- NOMINATOR: 提名者

## 发起投票

请通过发送电子邮件的方式发起对该候选人的投票，邮箱地址为： <private@fesod.apache.org>:

主题：

```text
[VOTE] Add candidate ${CANDIDATE_NAME} as a new committer
```

内容：

```text
Hi, All Apache Fesod (Incubating) PPMC members.
  
I'd like to take this chance to call the vote for inviting committed
contributor ${CANDIDATE_NAME} (GitHub id: ${CANDIDATE_GITHUB_ID}) as a new committer of Apache 
Fesod.

${CANDIDATE_NAME}'s great contributions can be found:

- GitHub Account: https://github.com/${CANDIDATE_GITHUB_ID}
- [27 PRs]: https://github.com/apache/fesod/pulls?q=is%3Apr+author%3A${CANDIDATE_GITHUB_ID}
- [15 Issues]: https://github.com/apache/fesod/issues?q=is%3Aissue+involves%3A${CANDIDATE_GITHUB_ID}

Please make your valuable evaluation on whether we could invite ${CANDIDATE_NAME} as a
committer:

[ +1 ] Agree to add ${CANDIDATE_NAME} as a committer of Fesod.
[  0 ] Have no sense.
[ -1 ] Disagree to add ${CANDIDATE_NAME} as a committer of Fesod, because .....

This vote starts from the moment of sending and will be open for 3 days.
 
Best regards,
${NOMINATOR}
```

投票将在今天开始的**一周后**结束, 即：

- [midnight UTC on YYYY-MM-DD](https://www.timeanddate.com/counters/customcounter.html?year=YYYY&month=MM&day=DD)
- [Apache Voting Guidelines](https://community.apache.org/newcommitter.html)

## 关闭投票

在至少获得 3 次“+1”赞成票且无人投反对票的情况下，宣布投票结果：

主题：

```text
[RESULT][VOTE] Add candidate ${CANDIDATE_NAME} as a new committer
```

内容：

```text
Hi, all:

The vote for "Add candidate ${CANDIDATE_NAME} as a new committer" has PASSED and closed now.

The result is as follows:

4 binding +1 Votes:
- voter names

Vote thread: https://lists.apache.org/thread/j16lvkyrmvg8wyf3z4gqpjky5m594jhy

The vote is successful, and then I'm going to invite ${CANDIDATE_NAME} to join us.

Thanks for everyone's support!

Best regards,
${NOMINATOR}
```

## 向候选人发送邀请函

如果投票结果是积极的，就向候选人发送邀请函，并抄送给 <private@fesod.apache.org>:

主题：

```text
Invitation to become Apache Fesod (Incubating) Committer: ${CANDIDATE_NAME}
```

内容：

```text
Hello ${CANDIDATE_NAME},

The Apache Fesod (Incubating) (PMC)hereby offers you 
committer privileges to the project.

These privileges are offered on the understanding that
you'll use them reasonably and with common sense.
We like to work on trust rather than unnecessary constraints.

Being a committer enables you to more easily make 
changes without needing to go through the patch 
submission process.

Being a committer does not require you to 
participate any more than you already do. It does 
tend to make one even more committed.  You will 
probably find that you spend more time here.

Of course, you can decline and instead remain as a 
contributor, participating as you do now.

This personal invitation is a chance for you to accept or decline in private.
Please let us know in reply to this message whether you accept or decline.

If you accept, you will need an Apache account (id) with privileges.
Please follow these instructions.

A. If you already have an ICLA on file:

    1. If you already have an Apache account, let us know your id and we
will grant you privileges on the project repositories.

    2. If you have previously sent an ICLA, let us know the email address
and public name used on the ICLA and your preferred Apache id, and
we will request your account.

    3. If the email address on the previously submitted ICLA is no longer
valid, let us know the email address and public name used on the new ICLA,
and your preferred Apache id. Continue to step B below and file your new ICLA.

Look to see if your preferred ID is already taken at
https://people.apache.org/committer-index.html

B. If there is not already an ICLA on file, you need to submit an ICLA:

    1. Details of the ICLA and the forms are found
    through this link: https://www.apache.org/licenses/#clas

    2. Instructions for its completion and return to
    the Secretary of the ASF are found at
    https://www.apache.org/licenses/contributor-agreements.html#submitting

    Do not copy the project or any other individual on your message
    to Secretary, as the form contains Personally Identifiable Information
    that should be kept private.

    3. When you complete the ICLA form, be sure to include in the form
    the Apache [Project] project and choose a
    unique Apache ID. Look to see if your preferred
    ID is already taken at
    https://people.apache.org/committer-index.html
    This will allow the Secretary to notify the PMC
    when your ICLA has been recorded.

When recording of your ICLA is noted, you will
receive a follow-up message with the next steps for
establishing you as a committer.

${NOMINATOR} (as represents of The Apache Fesod (Incubating) PPMC)
```

## 候选人接受邀请

如果接受邀请，该候选人将会收到来自 ASF 的一封电子邮件。

主题：

```text
Invitation to become Apache Fesod (Incubating) Committer: ${CANDIDATE_NAME}
```

内容：

```text
Welcome. Here are the next steps in becoming a project committer. After that
we will make an announcement to the dev@fesod.apache.org list.

You need to send a Contributor License Agreement to the ASF.
Normally you would send an Individual CLA. If you also make
contributions done in work time or using work resources,
see the Corporate CLA. Ask us if you have any issues.
https://www.apache.org/licenses/#clas.

You need to choose a preferred ASF user name and alternatives.
In order to ensure it is available you can view a list of taken IDs at
https://people.apache.org/committer-index.html

Please notify us when you have submitted the CLA and by what means 
you did so. This will enable us to monitor its progress.

We will arrange for your Apache user account when the CLA has 
been recorded.

After that is done, please make followup replies to the dev@fesod.apache.org list.
We generally discuss everything there and keep the
private@fesod.apache.org list for occasional matters which must be private.

The developer section of the website describes roles within the ASF and provides other
resources:
  https://www.apache.org/foundation/how-it-works.html
  https://www.apache.org/dev/

The incubator also has some useful information for new committers
in incubating projects:
  https://incubator.apache.org/guides/committer.html
  https://incubator.apache.org/guides/ppmc.html

Just as before you became a committer, participation in any ASF community
requires adherence to the ASF Code of Conduct:
  https://www.apache.org/foundation/policies/conduct.html

Yours,
The Apache Fesod (Incubating) PPMC
```

## 将候选人添加至提交者名单

候选人接受邀请并完成iCLA记录后，请通过[whimsy roster tools](https://whimsy.apache.org/roster/committee/fesod)将其添加至提交者名单。

![Roster](/img/community/roster-add-committer.png)

## 公告

一旦候选人接受邀请并获得提交权限，建议向 `dev@fesod.apache.org` 发送公告邮件以欢迎新提交者。以下是邮件模板：

```text
Hello, everyone

On behalf of the Apache Fesod (Incubating) PPMC, I'm happy to announce that
${CANDIDATE_NAME} has accepted the invitation to become a committer of
Apache Fesod (Incubating).

Welcome, and thank you for your contributions!

Best regards,
${NOMINATOR}
```
