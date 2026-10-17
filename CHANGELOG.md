# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

- Enhanced `README.md` with a Mermaid pipeline diagram, a side-by-side comparison table (Original vs. Fixed), best-practice guidance on `format="preserve-line"` vs. splitting `<para>` tags, and updated requirement/init instructions.
- Improved `DocHelperTest.java` by resetting `ByteArrayOutputStream` between runs, asserting `peakMemoryOriginal > peakMemoryFixed`, and updating class documentation.
- Added explanatory comments in `document.ftl` highlighting the FOP memory trigger and fix condition.

### Removed

- Removed unused template scaffolding class `People.java`.

## [1.0.0] - 2026-10-17

### Added

- Proof of Concept (POC) demonstrating and analyzing Apache FOP excessive heap memory consumption caused by the Knuth-Plass line-breaking algorithm on large text blocks (~30,000 characters) inside table cells.
- Implementation of the solution using `format="preserve-line"` on `<para>` elements (translating to `linefeed-treatment="preserve"` on `<fo:block>`).
- FreeMarker template (`document.ftl`) generating a Fugerit Venus Doc XML source document and conditionally toggling between original and fixed layout.
- JUnit test (`DocHelperTest.java`) measuring generation duration, heap memory delta, and peak heap usage.
- Comprehensive `README.md` documentation detailing root cause analysis, specific trigger conditions, and benchmark results.
- Project initialization with GitHub Actions CI workflow and SonarCloud analysis configuration.
