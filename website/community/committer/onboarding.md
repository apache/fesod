---
id: 'committer-onboarding'
title: 'Onboarding'
---

Upon receiving an invitation email from Apache Fesod (Incubating) PPMC, a new committer should consider whether accepting.

If they decide in favor, they should select "Reply All" and express their decision. Please provide the reference content of this reply email:

```text
Hi, i accept. Thanks for invitation.
```

## Submit CLA

We require all contributors to sign a Contributor License Agreement (CLA). This step refers to [Sign ICLA guide](./icla.md)

## Setup ASF Account

Go to [Apache Account Utility](https://id.apache.org/reset/enter) and enter your Apache ID.

![Reset Password](/img/community/reset-passwd.png)

After clicking "Send Email", a prompt will appear indicating that the email has been sent successfully. Then check your email and click the provided link to reset your password.

### Link ASF Account to GitHub

![Link GitHub](/img/community/link-github.png)

1. Navigate to [ASF Boxer](https://gitbox.apache.org/boxer/) and enter your Apache ID and password.
2. Click "Link GitHub username to ASF id" and follow the given instructions to link your ASF account to GitHub.
3. Check your email titled "[GitHub] @asfgit has invited you to join the @apache organization" and accept the invitation.
4. Wait momentarily, and the website will refresh on its own.
5. (If you do not enable 2FA on GitHub) Please follow the [instruction](https://docs.github.com/en/authentication/securing-your-account-with-two-factor-authentication-2fa/configuring-two-factor-authentication).

Your Apache ID and GitHub ID will now both appear on [ASF Boxer](https://gitbox.apache.org/boxer/). Congrats on successfully linking your ASF account to GitHub!

## Email Settings

You cannot work directly with your Apache email address. You must set up forwarding for this address.

### Receive Email

You can change your forwarding email address at [Apache Account Utility Platform](https://id.apache.org/)

### Send Email

To send emails using your apache.org address, configure your email client to leverage the `mail-relay` service. For specifics, refer to [this guide](https://infra.apache.org/committer-email.html).

### Gmail

1. Open Gmail settings and select "See all settings".
2. Navigate to "Accounts and Import", then locate "Send mail as".
3. Click "Add another email address" and enter your name and apache.org email address.
4. Input the SMTP server information:
    - SMTP Server: mail-relay.apache.org
    - Port: 587
    - Username: your apache ID
    - Password: your apache password
    - Secured connection using TLS
5. Click "Add account" and you will receive an email from Gmail that need to confirm.

![Gmail Setting](/img/community/gmail-setting.png)

### Subscribe to Mailing List

Please refer to [Subscribe to Mailing List](../index.md#how-to-subscribe-to-a-mailing-list)

## Enable 2FA on GitHub

Two-factor authentication (2FA) refers to the authentication method that combines both passport, and an object (credit card, SMS phone, token or biomarkers as fingerprint) to identify a user.
To ensure the security of the committer’s account, we need you to enable 2FA to sign in and contribute codes on GitHub.

More details, please refer to [2FA](https://help.github.com/articles/requiring-two-factor-authentication-in-your-organization/).

For detailed operations, please refer to [Enable Two Factor Authentication with TOTP](https://help.github.com/articles/configuring-two-factor-authentication-via-a-totp-mobile-app/).

After enabling 2FA, you need to sign in GitHub with the way of username/password + mobile phone authentication code.
