---
id: 'security'
title: 'Security'
---

<!--
- Licensed to the Apache Software Foundation (ASF) under one or more
- contributor license agreements.  See the NOTICE file distributed with
- this work for additional information regarding copyright ownership.
- The ASF licenses this file to You under the Apache License, Version 2.0
- (the "License"); you may not use this file except in compliance with
- the License.  You may obtain a copy of the License at
-
-   http://www.apache.org/licenses/LICENSE-2.0
-
- Unless required by applicable law or agreed to in writing, software
- distributed under the License is distributed on an "AS IS" BASIS,
- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
- See the License for the specific language governing permissions and
- limitations under the License.
-->

The Apache Software Foundation takes a rigorous stance on eliminating security issues in its software projects. Likewise, Apache Fesod (Incubating) is also vigilant and takes security issues related to its features and functionality into the highest consideration.

If you have any concerns regarding Apache Fesod (Incubating)’s security, or you discover a vulnerability or potential threat, please don’t hesitate to get in touch with the [Apache Security Team](http://www.apache.org/security/) by dropping an email at [security@apache.org](mailto:security@apache.org).

Please specify the project name as "Apache Fesod (Incubating)" in the email, and provide a description of the relevant problem or potential threat. You are also urged to recommend how to reproduce and replicate the issue.

The Apache Security Team and the Apache Fesod (Incubating) community will get back to you after assessing and analyzing the findings.

**Please note** that the security issue should be reported on the security email first, before disclosing it on any public domain.

## Advisories for Dependencies

Many organizations employ security scanning tools to identify components with known security advisories. Although we strongly recommend these tools as they can alert users to potential risks, they often generate false positives. This occurs because a vulnerable dependency may not necessarily impact Apache Fesod (Incubating) if used in a non-exploitable manner.

Therefore, advisories regarding Apache Fesod (Incubating)'s dependencies are not automatically considered critical. However, if additional analysis indicates that Apache Fesod (Incubating) might be affected by a dependency's vulnerability, please report your findings privately to [security@apache.org](mailto:security@apache.org).

If a dependency advisory is identified, please:

1. Verify if our DependencyCheck suppressions contain relevant details.
2. Check our issue tracker for discussions regarding this advisory.
3. Conduct your own analysis to determine whether Apache Fesod (Incubating) is affected.
    - If affected, report your findings privately through [security@apache.org](mailto:security@apache.org).
    - If not affected, please contribute by updating the DependencyCheck suppression list, clearly documenting why Apache Fesod (Incubating) is not impacted.
