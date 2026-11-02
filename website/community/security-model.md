---
id: 'security-model'
title: 'Security Model'
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

Apache Fesod (Incubating) runs as a library inside your Java application and builds on Apache POI for Excel processing. This security model describes the assumptions and responsibilities for reading and writing Excel and CSV files, including related conversion features.

**Password protection of a completed XLSX file does not encrypt all data used while processing it.** Plaintext may exist in temporary files, caches, output locations, and JVM memory. Applications and operators must protect each of these locations.

## Scope and Trust Boundaries

Apache Fesod (Incubating) performs file access, memory allocation, and enabled network operations with the privileges of its JVM process. It does not provide application authentication, business authorization, or tenant isolation. Use maintained versions of Apache Fesod (Incubating), the JDK, and dependencies, and check the capabilities and configuration of the version you deploy.

| Resource or operation | Security expectation | Application and operator responsibility |
| --- | --- | --- |
| Application code, converters, listeners, and handlers | Extensions execute as trusted code in the same JVM | Do not execute extensions supplied by untrusted users; extensions can access resources available to the process |
| Documents, templates, and cell data | Input may contain malicious or unexpected content; parsing and streaming APIs are not a sandbox | Validate business input, limit resource consumption, and isolate processing according to risk |
| Local paths and streams | Apache Fesod (Incubating) reads and writes the paths or streams supplied by the caller | Authorize access, protect input, output, and template directories, and isolate tenants |
| Temporary files and read caches | Intermediate data may contain unencrypted workbook or cell content | Prevent untrusted parties from reading, modifying, or replacing temporary storage and its contents |
| JVM memory, dumps, and logs | Sensitive content or passwords may be present | Control debugging, dumps, logs, backups, and access to the service identity separately |
| Network features such as URL images | Remote fetching is disabled by default and requires an explicit exact-host allowlist; requests originate from the JVM process | Keep the default deny policy or restrict destinations, apply network egress controls, and do not directly trust user-supplied URLs |
| Password-protected XLSX output | Successfully completed output uses Apache POI workbook encryption | Protect passwords, intermediate plaintext, and failed output; publish only after successful completion |

Apache Fesod (Incubating) does not promise to hide data from parties that already have access to the corresponding application files, temporary storage, or process memory. These are separate permissions: the ability to read a temporary file does not imply the ability to inspect JVM heap memory, or vice versa.

## Relationship with Apache POI {#apache-poi}

Apache POI's security guidance, supported behavior, and implementation limits must be considered when using Apache Fesod (Incubating)'s POI-based features. A higher-level Apache Fesod (Incubating) API does not remove POI's requirements for protected temporary storage and process memory, resource isolation, or the limitations of its document formats and encryption support. Review the [Apache POI security guidance](https://poi.apache.org/security.html) alongside this model for the POI version used by your application.

Apache Fesod (Incubating) aims to respect POI's security model, API contracts, and protective checks. It does not aim to bypass POI's safeguards or promise stronger security properties than the underlying implementation provides. Applications should not disable or weaken POI's protections merely to accept a document that is rejected by those checks. Any necessary configuration change must follow POI's documented behavior and account for its security impact, including effects on other code sharing the same JVM.

This dependency does not remove Apache Fesod (Incubating)'s responsibility for its own code and integration. Unexpected behavior introduced by Apache Fesod (Incubating), or a bypass of security controls through its APIs, should still be reported privately for assessment. Support for a capability in POI does not by itself mean that every Apache Fesod (Incubating) API exposes or enables that capability.

## Temporary Files and Caches {#temporary-files-and-caches}

Apache Fesod (Incubating) and its dependencies may use temporary files or disk caches during reading, streaming writes, template processing, and encryption or decryption. Intermediate data may be unencrypted even when the final workbook is password-protected.

Operators must provide a protected temporary directory for the JVM and protect any separately configured cache, staging, or output locations. Untrusted parties must not be able to read, modify, or replace these directories or their contents. Random names and the sticky bit on a shared directory do not replace access controls.

On POSIX systems, create a private directory under the service's operating system identity before starting the application:

```sh
umask 077
FESOD_TMPDIR="$(mktemp -d "${TMPDIR:-/tmp}/fesod.XXXXXXXX")" || exit 1
chmod 700 "$FESOD_TMPDIR" || exit 1
java -Djava.io.tmpdir="$FESOD_TMPDIR" -jar app.jar
```

The parent directory must prevent untrusted parties from replacing the private directory. On Windows, use equivalent ACLs. This example protects the default JVM temporary location; separately configured locations need their own protection. Directory permissions do not isolate untrusted tenants that share the same operating system identity.

Set `java.io.tmpdir` when starting the JVM. Changing it after library initialization does not ensure that every component switches directories. In containers, use protected temporary volumes dedicated to the workload, and avoid sharing accessible staging storage with untrusted workloads.

Close readers and writers and complete their resource-release lifecycle. During normal resource release, the library and its dependencies attempt to remove temporary files and caches managed by the current operation, including operation-specific temporary subdirectories where applicable. Cleanup is not guaranteed to succeed.

The library does not perform a general cleanup of shared temporary directories or residual files from earlier runs or other processes. Exceptions, forced termination, host failures, or deletion failures may leave intermediate files behind. Operators must protect temporary storage and arrange controlled cleanup of residual files that are no longer in use by active tasks. Ordinary deletion is not secure erasure and does not remove storage snapshots or backups.

## Password Protection {#password-protection}

When writing XLSX with a non-empty password, successfully completed output uses Apache POI's workbook encryption. This protects the completed file's content; it does not provide encryption throughout input, processing, staging, and delivery, nor does it provide authentication, authorization, or a guarantee of password strength.

The encryption mode is selected by the library, not by the caller. Current releases use Apache POI's standard encryption mode, which is AES-128 with the SHA-1 based key derivation defined by the Office Open XML standard, not the more recent agile mode that allows AES-256. The mode determines key length and key derivation, and therefore how well a completed file resists offline password guessing. Verify the mode used by the version you deploy rather than assuming a stronger one, and reassess if your threat model depends on it.

Password protection is an access-control measure for data at rest and in transit. It is not a substitute for controlling who can obtain or retain the file, and it is not the right mechanism for long-term storage of high-value secrets.

Encryption may first produce a plaintext workbook before producing the encrypted result. When writing directly to a file path, that location may also contain plaintext during processing. Protect output locations and prevent downloads or publication until the entire write operation has completed successfully.

When writing in batches with `ExcelWriter`, a successful `write()` call does not mean that output or encryption is complete. Publish only after all writes and the final `finish()` or `close()` call succeed. With try-with-resources, publish after the entire statement completes normally, not inside its block.

Treat output from a failed write or encryption operation as incomplete and sensitive. Do not assume that a failure leaves an encrypted file, an empty file, or no file. Where publication must be controlled, generate the result in protected staging storage and publish it only after success.

An in-memory option is not a promise that every Apache Fesod (Incubating) or dependency execution path avoids disk storage. CSV has no workbook password encryption, and legacy XLS protection must not be assumed to provide the same confidentiality as XLSX encryption.

Obtain passwords from controlled configuration or a secrets manager. Keep them out of source code, logs, and exception output. Password protection does not replace filesystem permissions, protected transport, or sensitive-data retention policies.

## Untrusted Documents and Resource Isolation

Compressed data, shared strings, images, styles, and complex document structures may consume substantial CPU, memory, or disk space. Input size, filename extension, and use of streaming APIs do not guarantee bounded processing costs. Existing checks do not provide complete protection against every malicious document.

OOXML archives are read through Apache POI, which applies its own expansion-ratio limits to compressed entries. Those limits reject some highly compressed input, but they are a heuristic bound on expansion, not a reliable isolation mechanism. Do not treat them as a substitute for process-level limits.

Replacing the default XML parser factory changes the parser hardening applied while reading. The library configures DOCTYPE and external-entity restrictions on the parser it creates, but a replacement factory that does not support those settings will not enforce them. If you supply your own factory, verify that equivalent protections are active.

For externally supplied files, limit input and output size, concurrency, temporary storage, and application-level processing volume. Services requiring reliable isolation from timeouts or memory exhaustion should process documents in a separate process or container, with an external supervisor enforcing time, resource, and network limits. Do not rely on thread interruption to stop every parsing operation.

Do not treat partially imported data or incomplete exports as successful business operations. Applications are responsible for transaction boundaries, data validation, and retry behavior.

## Paths, Networks, and Spreadsheet Clients

Do not pass user input directly as an unrestricted local path. Restrict allowed roots, authorize the actual target, and account for symbolic links and concurrent replacement. Apache Fesod (Incubating) does not know which user or tenant a caller-supplied path belongs to.

For URL images and other network features, remote fetching is disabled by default and must be enabled with an explicit exact-host allowlist. Requests originate from the server. Allowing a domain does not authorize every location to which it might resolve or redirect. Network operations in custom converters require the same care.

The URL fetch policy is process-wide, not per workbook or per reader. Any code in the JVM can change it, and a change affects every component using Apache Fesod (Incubating) in that process, including components that believed fetching was disabled. Set it once during application startup, do not let independent modules override each other at runtime, and confirm the effective policy where several libraries share a JVM.

Apache Fesod (Incubating) does not provide a general sanitization policy for formulas, hyperlinks, external references, or CSV values. Spreadsheet applications may interpret exported content when a user opens the result. Distinguish text from intentionally supplied formulas and choose an output policy appropriate to the data source and target client. CSV quoting alone does not prevent formula injection.

## Reporting Vulnerabilities and Hardening Suggestions

If behavior may cross these boundaries or cause unexpected data disclosure, file access, network requests, or code execution, report it privately to [security@apache.org](mailto:security@apache.org). Identify Apache Fesod (Incubating) and include the version, configuration, attacker prerequisites, impact, and a minimal reproduction. Do not first disclose potential vulnerability details in public issues, pull requests, or mailing lists.

Use the private reporting channel when you are unsure whether an issue is a vulnerability. The project assesses actual behavior, affected versions, and applicable security expectations. Dependency advisories require analysis of the relevant usage path. Reports of bypassed security controls are not excluded solely because operators also have security responsibilities.

The following are generally not vulnerabilities in Apache Fesod (Incubating) itself, because the boundary is on the application side. They are still worth reporting if the library could reasonably prevent the outcome:

- Passing an untrusted string to the library as a local path, which makes the application the origin of a path traversal
- Supplying a replacement XML parser factory, or widening the URL fetch policy, and then relying on the library to restore protections the caller disabled
- Leaving plaintext in an unprotected temporary or output directory that the application configured
- A completed password-protected file being guessable because the application chose a weak password

Risks from operating outside a protected environment generally require deployment changes. The project also welcomes hardening suggestions, such as stricter temporary-file permissions and more reliable cleanup. Vulnerability classification, CVE allocation, and adoption of hardening improvements are separate decisions.

## Supported Versions and Disclosure

Security fixes are provided on the most recent release line. Older release lines do not receive backported fixes; upgrade to a maintained version before reporting an issue that may already be addressed. The project follows the [ASF vulnerability handling process](https://www.apache.org/security/committers.html): reports are discussed privately, a fix is prepared, and disclosure is coordinated with the reporter. A CVE is requested when the project and the ASF Security Team determine that the issue warrants one.

## Further Reading

- [Apache Fesod (Incubating) security reporting and dependency advisories](./security.md)
- [Apache POI security guidance](https://poi.apache.org/security.html)
- [ASF Security Team](https://www.apache.org/security/)
