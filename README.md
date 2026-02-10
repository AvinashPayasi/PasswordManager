# Secure Password Manager (CLI)

A local, offline-first password manager built using Java and PostgreSQL.
This project focuses on backend engineering concepts such as cryptography,
secure data handling, and database-backed application design.

---

## Features

- Master password–based authentication
- Password hashing using Argon2id
- Credential encryption using AES-GCM
- Encrypted per-user data key (key-wrapping model)
- Brute-force protection with progressive account locking
- Secure password input via terminal (no echo)
- CLI-based interaction

---

## Tech Stack

- Java 21
- PostgreSQL
- Maven
- Bouncy Castle (cryptography)

---

## Security Overview

- The master password is never stored
- Argon2id is used for key derivation
- Actual credentials are encrypted using a randomly generated data key
- The data key itself is encrypted using a key derived from the master password
- AES-GCM provides confidentiality and integrity

---

## Configuration

Database credentials are **not committed** to the repository.

To run the project, create a local configuration file at:
"src/main/resources/application.properties"

with the following keys:

db.url

db.username

db.password