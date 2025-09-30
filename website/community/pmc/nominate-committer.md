---
id: 'nominate-committer'
title: 'Nominate Committer'
---

This document mainly introduces how a PPMC member nominates a new committer.

## Template

Note that, there are three placeholder in template should be replaced before using

- CANDIDATE_NAME: The full name of the candidate.
- CANDIDATE_GITHUB_ID: The GitHub id of the candidate.
- NOMINATOR: The GitHub id of the candidate.

:::note

CANDIDATE_NAME This Must Be Public Name, Not Github Name Or Id.

:::

## Start a vote

Start a vote about the candidate via sending email to: <private@fesod.apache.org>:

Title:

```text
[VOTE] Add candidate ${CANDIDATE_NAME} as a new committer
```

Content:

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

Note that, Voting ends one week from today, i.e.:

- [midnight UTC on YYYY-MM-DD](https://www.timeanddate.com/counters/customcounter.html?year=YYYY&month=MM&day=DD)
- [Apache Voting Guidelines](https://community.apache.org/newcommitter.html)

## Close Vote

After **at least 3 `+1` binding vote** and **no veto**, claim the vote result:

Title:

```text
[RESULT][VOTE] Add candidate ${CANDIDATE_NAME} as a new committer
```

Content:

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

## Send invitation to the candidate

If the vote result is positive, send an invitation to the candidate and cc <private@fesod.apache.org>:

Title:

```text
Invitation to become Apache Fesod (Incubating) Committer: ${CANDIDATE_NAME}
```

Content:

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

## The candidate accepts the invitation

If the invitation is accepted, the candidate will receive an email from the ASF.

Title:

```text
Invitation to become Apache Fesod (Incubating) Committer: ${CANDIDATE_NAME}
```

Content:

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

## Add the candidate to the committer list

After the candidate accepts the invitation and the iCLA is recorded, add the candidate to the committer list by [whimsy roster tools](https://whimsy.apache.org/roster/committee/fesod)

![Roster](/img/community/roster-add-committer.png)

## Announcement

Once the nominee accepts the invitation and the commit bit is granted, it's encouraged to send an announcement email to <dev@fesod.apache.org> to welcome the new committers. Here is an email template:

```text
Hello, everyone

On behalf of the Apache Fesod (Incubating) PPMC, I'm happy to announce that
${CANDIDATE_NAME} has accepted the invitation to become a committer of
Apache Fesod (Incubating).

Welcome, and thank you for your contributions!

Best regards,
${NOMINATOR}
```
