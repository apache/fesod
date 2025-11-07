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

## 讨论

如果任何 PPMC 成员认为某人符合我们的要求并且可以成为潜在的提交者，
请通过发送电子邮件的方式发起对该候选人的讨论，邮箱地址为： <private@fesod.apache.org>:

主题：

```text
[DISCUSS] New committer: [CANDIDATE_NAME]
```

内容：

```text
Hi all,

I propose to nominate ${CANDIDATE_NAME} (GitHub id: ${CANDIDATE_GITHUB_ID}) as a new committer.

[State reasons that you believe they are a good candidate.]

${CANDIDATE_NAME}'s great contributions can be found:

- GitHub Account: https://github.com/${CANDIDATE_GITHUB_ID}
- [XX PRs]: https://github.com/apache/fesod/pulls?q=is%3Apr+author%3A${CANDIDATE_GITHUB_ID}
- [XX Issues]: https://github.com/apache/fesod/issues?q=is%3Aissue+involves%3A${CANDIDATE_GITHUB_ID}

This is still in the discussion phase. If everything goes smoothly, we will proceed with the official vote 
in a separate email.

Looking forward to your thoughts and feedback.
 
Best regards,
${NOMINATOR}
```

讨论将至少持续**一周**的时间。

## 发起投票

如果所推荐的候选人得到了大多数回复者的积极评价，请通过发送电子邮件的方式启动对该候选人的投票，邮箱地址为： <private@fesod.apache.org>:

主题：

```text
[VOTE] Add candidate ${CANDIDATE_NAME} as a new committer
```

内容：

```text
Hi all,
  
This is a VOTE to add candidate ${CANDIDATE_NAME} (GitHub id: ${CANDIDATE_GITHUB_ID}) as a new committer.

This has been discussed here: [Link to DISCUSS thread on lists.apache.org] If you have more to 
add to the discussion, please do so there, rather than in this VOTE thread.

Please vote accordingly:

[ +1 ] Yes, add this committer
[  0 ] Abstain
[ -1 ] No, do not add this committer

Voting ends one week from today, i.e. midnight UTC on YYYY-MM-DD
https://www.timeanddate.com/counters/customcounter.html?year=YYYY&month=MM&day=DD

See voting guidelines at
https://community.apache.org/pmc/adding-committers.html
```

投票将在今天开始的**一周后**结束, 即：

- [midnight UTC on YYYY-MM-DD](https://www.timeanddate.com/counters/customcounter.html?year=YYYY&month=MM&day=DD)
- [Apache Voting Guidelines](https://community.apache.org/newcommitter.html)

## 关闭投票

在至少获得 3 次 +1 赞成票且无人投反对票的情况下，宣布投票结果：

主题：

```text
[RESULT] [VOTE] Add candidate ${CANDIDATE_NAME} as a new committer
```

内容：

```text
Hi all,

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

The Apache Fesod (Incubating) (PPMC) hereby offers you 
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

${NOMINATOR}
On behalf of Apache Fesod (Incubating) PPMC
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

${NOMINATOR}
On behalf of Apache Fesod (Incubating) PPMC
```

## 将候选人添加至提交者名单

候选人接受邀请并完成iCLA记录后，请通过[whimsy roster tools](https://whimsy.apache.org/roster/committee/fesod)将其添加至提交者名单。

![Roster](/img/community/roster-add-committer.png)

## 欢迎新的 Committer

向新提交者发送欢迎电子邮件。

主题:

```text
Welcome, ${CANDIDATE_NAME}, New Committer!
```

内容:

```text
Hello, ${CANDIDATE_NAME},

As you know, the ASF Infrastructure has set up your committer account
with the username `[apacheID]`

You will now be able to merge approved PRs on GitHub for this project.
(You'll need to associate your GitHub account with your Apache email
address.)

You can manage your account settings at https://id.apache.org/

The developer section of the website describes roles within the ASF and 
provides other resources:
  https://www.apache.org/foundation/how-it-works.html
  https://www.apache.org/dev/

The incubator also has some useful information for new committers
in incubating projects:
  https://incubator.apache.org/guides/committer.html
  https://incubator.apache.org/guides/ppmc.html

You now have expanded access to portions of the Whimsy toolset
specific to committers: https://whimsy.apache.org/

As an ASF committer, you now also have commit access to specific
sections of the ASF Foundation repository, as follows:

The general "committers" at:
https://svn.apache.org/repos/private/committers

Just as before you became a committer, participation in any ASF community
requires adherence to the ASF Code of Conduct:
  https://www.apache.org/foundation/policies/conduct.html

If you have any questions during this phase, then please
see the following resources:

Apache developer's pages: https://www.apache.org/dev/
Incubator committer guide: https://incubator.apache.org/guides/committer.html

Naturally, if you don't understand anything be sure to ask us on the 
Fesod dev mailing list. Documentation is maintained by volunteers 
and hence can be out-of-date and incomplete - of course you can now
help fix that.

A PPMC member will announce your election to the dev list, and we
encourage you to introduce yourself there.

${NOMINATOR}
On behalf of Apache Fesod (Incubating) PPMC
```

## 公告

在 `dev@fesod.apache.org` 邮件列表中宣布新的提交者。

主题:

```text
[ANNOUNCEMENT] New committer: ${CANDIDATE_NAME}
```

内容:

```text
The Podling Project Management Committee (PPMC) for Apache Fesod (Incubating)
has invited ${CANDIDATE_NAME} to become a committer and we are pleased
to announce that they have accepted.

[State New Committers's contribution, if available and relevant.]

Please join us in welcoming ${CANDIDATE_NAME} to their new role and
responsibility in our project community.

${NOMINATOR}
On behalf of Apache Fesod (Incubating) PPMC
```
