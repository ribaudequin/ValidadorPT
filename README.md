<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/ribaudequin/ribaudequin/main/assets/bandua-light.svg">
    <img src="https://raw.githubusercontent.com/ribaudequin/ribaudequin/main/assets/bandua-dark.svg" alt="Bandua Studio" width="64">
  </picture>
</p>

<h1 align="center">ValidadorPT</h1>

<p align="center">Offline validator for Portuguese NIF, IBAN and NIB.</p>

<p align="center"><sub>A <b>Bandua Studio</b> project · by Marcelo Salvador</sub></p>

---

An Android app that validates the structure of Portuguese identifiers — **NIF**, **IBAN** and **NIB** —
entirely on the device. No network, no accounts, no tracking.

## What it does

Validates the check digits and structure of Portuguese identifiers, and tells you what it found:

- **NIF** — 9 digits, check digit validated, entity type identified
- **IBAN** — 25 characters for Portugal, check digits validated, bank identified
- **NIB** — 21 digits, the legacy format, bank identified

Bank identification uses a local table of 32 Portuguese bank codes. Everything runs offline.

## Features

- 100% offline — no internet permission is requested
- No accounts, no analytics, no tracking
- NIF, IBAN and NIB validation with check digits
- Entity type (NIF) and bank (IBAN/NIB) identification
- Portuguese (PT-PT) and English
- Permanent legal disclaimer
- ~6.2 MB APK

## Installation

Download the latest APK from [Releases](https://github.com/ribaudequin/ValidadorPT/releases) and install
it on your device. Android will ask you to allow installation from an unknown source, since the app is
not distributed through the Play Store.

## Building

```bash
./gradlew assembleRelease
```

Requires JDK 17 and the Android SDK. Kotlin 2.1.10, Jetpack Compose, MVVM, Gradle KTS.

## Tests

```bash
./gradlew test
```

34 unit tests cover NIF, IBAN and NIB validation — valid cases, invalid cases and edge cases.

## Status

**v0.1.0 — MVP Beta.** Functional and tested, with known limitations:

- Signed with a debug keystore, not suitable for real distribution
- No dark theme
- NIB is a legacy format, officially replaced by IBAN in 2016

See `AUDIT_REPORT.md` for the full review.

## Disclaimer

This app performs **structural validation only**. It confirms that an identifier is well-formed and
that its check digits are correct. It does not confirm that an identifier exists, is active, or belongs
to anyone in particular.

## License

MIT
