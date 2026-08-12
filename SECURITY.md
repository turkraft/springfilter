# Security Policy

## Reporting a Vulnerability

If you discover a security vulnerability, please do **not** open a public issue.

Instead, email **torshid@gmail.com** with details. Include:

- A description of the vulnerability
- Steps to reproduce
- The affected module(s) and version(s)
- Any potential impact

You will receive a response within **24 hours**. We will work with you to understand the issue and coordinate a fix and release timeline.

## Supported Versions

| Version | Supported          |
|---------|--------------------|
| 4.x.x   | :white_check_mark: |
| 3.x.x   | :x:                |
| 2.x.x   | :x:                |

## Scope

Security issues in the following areas are in scope:

- Filter expression injection that bypasses access controls
- Field name manipulation that exposes restricted fields
- Parsing vulnerabilities (stack overflow, OOM)
- Type conversion exploits
- Any mechanism that allows unauthorized data access through filter expressions

## Disclosure Policy

We follow a coordinated disclosure process:

1. The reporter submits the vulnerability privately.
2. We acknowledge receipt within 24 hours.
3. We investigate and develop a fix.
4. A patch release is prepared.
5. The vulnerability is disclosed publicly after the fix is released.
6. Credit is given to the reporter in the release notes (unless anonymity is requested).
