---
id: 'committer-onboarding'
title: 'Committer 须知'
---

收到 Apache Fesod (Incubating) PPMC 的邀请邮件后，新的 Committer 应考虑是否接受邀请。

若决定接受，请选择 **回复所有** 并表明最终决定。回复邮件请包含以下参考内容：

```text
Hi, i accept. Thanks for invitaion.
```

## 提交 CLA

要求所有贡献者签署一份贡献者许可协议（CLA）。处理步骤参考 [签署 ICLA 指南](./icla.md)

## 设置 ASF 账号

进入 [Apache Account Utility](https://id.apache.org/reset/enter) 并输入您的Apache ID。

![Reset Password](/img/community/reset-passwd.png)

点击“Send Email”后，将出现提示信息，表明邮件已成功发送。随后请查收您的邮箱，并点击邮件中提供的链接重置密码。

### 关联 GitHub 账号

![Link GitHub](/img/community/link-github.png)

1. 访问 [ASF Boxer](https://gitbox.apache.org/boxer/)，输入您的 Apache ID 和密码。
2. 点击“Link GitHub username to ASF id”，并按照提示将您的 ASF 账户与 GitHub 绑定。
3. 查收标题为 “[GitHub] @asfgit has invited you to join the @apache organization” 的邮件并接受邀请。
4. 请稍候片刻，网站将自动刷新。
5. （若您尚未在GitHub启用双重验证）请遵循[操作指南](https://docs.github.com/en/authentication/securing-your-account-with-two-factor-authentication-2fa/configuring-two-factor-authentication)。

您的 Apache ID 和 GitHub ID 现已同时显示在 [ASF Boxer](https://gitbox.apache.org/boxer/) 上。恭喜您成功将 ASF 账户与 GitHub 关联！

## 邮箱设置

您无法直接使用您的 Apache 电子邮件地址进行工作。必须为此地址设置转发功能。

### 收取邮件

您可以在[Apache账户管理平台](https://id.apache.org/)更改您的邮件转发地址。

### 发送邮件

若要使用您的 `@apache.org` 邮箱地址发送邮件，请配置您的邮件客户端以利用 `mail-relay` 服务。具体操作请参阅[此指南](https://infra.apache.org/committer-email.html)。

### Gmail

1. 打开 Gmail 设置选择 "See all settings".
2. 进入“账户和导入”选项卡，找到“用这个地址发送邮件”。
3. 点击“添加其他电子邮件地址”，输入您的姓名和apache.org邮箱地址。
4. 输入 SMTP 服务器信息：
    - SMTP 服务器：mail-relay.apache.org
    - 端口：587
    - 用户名：您的 Apache ID
    - 密码：您的 Apache 密码
    - 通过 TLS 建立安全连接
5. 点击“添加账户”后，您将收到 Gmail 发送的确认邮件。

![Gmail Setting](/img/community/gmail-setting.png)

### 订阅邮件列表

请参考 [订阅邮件列表](../index.md#如何订阅邮件列表)

## 在 GitHub 上启用双因素认证

双因素认证（2FA）是指结合凭证（如护照）与实体设备（信用卡、短信手机、令牌或指纹等生物特征）共同验证用户身份的认证方式。
为保障提交者账户安全，我们要求您在GitHub登录及提交代码时启用双因素认证。

更多详情请参阅[双因素认证指南](https://help.github.com/articles/requiring-two-factor-authentication-in-your-organization/)。

具体操作请参阅[通过TOTP启用双因素认证](https://help.github.com/articles/configuring-two-factor-authentication-via-a-totp-mobile-app/)。

启用双因素认证后，您需通过用户名/密码+手机验证码的方式登录GitHub。
