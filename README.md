# LendAHand (Astera) — Community Donation Platform

> A native Android donation platform connecting donors with verified recipients, backed by a PHP REST API and a normalised MySQL database.

**Team:** Group 2713 · University of the Witwatersrand
**Platform:** Android (Java) · PHP 8 · PostgreSQL

---

## Overview

LendAHand bridges the gap between donors and verified community members in need. The platform operates across three user roles:

- **Donors** — browse open requests and donate against specific needs
- **Recipients** — post resource requests specifying item type and quantity
- **Staff** — moderate incoming donations and allocate them to matched requests

All data flows through a RESTful API using authenticated HTTP POST requests returning JSON. The Android client manages sessions via tokens stored locally.

## Core Features

- 🔐 Role-based authentication with session token management
- 📋 Community request board — recipients post resource requests
- 🎁 Donation flow — donors browse requests and donate against specific needs
- ✅ Staff pending-donations dashboard — accept or decline incoming donations
- 🔄 Allocation engine — staff allocate accepted donations to matched requests
- 🏆 Donor leaderboard ("The Guardians Circle") — real-time ranking by contribution
- 👤 Profile management — account details and donation history

## Tech Stack

| Layer | Technology | Role |
|---|---|---|
| Front-end | Android (Java) | Native mobile UI — Activities, Volley networking |
| API | PHP 8 | RESTful endpoints — JSON request/response |
| Database | PostgreSQL | Relational persistence with triggers and views |
| Networking | Volley (Android) | Async HTTP POST with queued requests |
| Auth | Session tokens | Token-based API authentication per user |

## Database Design

The schema was built through an iterative design process, resolving a many-to-many relationship between `REQUEST` and `DONATION` by introducing an `ALLOCATIONS` bridge entity, and normalising composite/multivalued attributes out of the initial design.

**Final entities (6 tables, validated to 3NF):**

| Entity | Role |
|---|---|
| `USERS` | Core user entity with role differentiation (user / admin / staff) |
| `RESOURCES` | Normalised item catalogue |
| `REQUESTS` | Recipient requests — FK to `USERS` and `RESOURCES` |
| `DONATIONS` | One donation per request, resolving the original M:M relationship |
| `ALLOCATIONS` | Bridge entity — links `DONATIONS` to `REQUESTS` via staff action |
| `LEADERBOARD` | Pre-aggregated donor scores (denormalised for read performance) |

Key implementation details:
- Database triggers update the leaderboard on donation acceptance and mark requests as fulfilled on allocation
- A view summarises open requests with live donation counts
- User registration checks for duplicate username/email at the database layer

> Note: an earlier draft of the design documentation referenced MySQL; the implementation runs on PostgreSQL. This README reflects the corrected stack.

## Screens

| Sign In | Donation Item Selection | Leaderboard |
|---|---|---|
| Role-based authentication | Category-filtered item selection for donors and recipients | Real-time donor ranking with tiered recognition |

## Setup

> _Add local setup instructions here — database import, PHP server config, and Android Studio build steps._

```bash
# Example placeholder — update with actual steps
1. Import schema.sql into MySQL
2. Configure backend/config.php with your database credentials
3. Deploy the PHP API to your server
4. Open the Android project in Android Studio and update the base API URL
5. Build and run
```

## Contributors

Group 2713 — University of the Witwatersrand

| Contributor | Focus |
|---|---|
| Thando Shabangu ([@Alch3mist-42](https://github.com/Alch3mist-42)) | UI/XML — app layouts, custom drawables, navigation, demo video. Also contributed substantially to the Java logic layer (Volley API calls, registration logic, RecyclerView adapters, input validation). |
| Sibongakonke Ntsele ([@Sbongakonk3](https://github.com/Sbongakonk3/) | Database/PHP — PostgreSQL schema, ERD, auth scripts, allocation logic, leaderboard API |
| Lufuno Pearl Moyo ([@Lufuno-pearl](https://github.com/Lufuno-pearl/) | Java logic layer (with significant support from Thando) |

---

<sub>Built as a coursework project for a Wits Computer Science module. Shared with permission.</sub>
