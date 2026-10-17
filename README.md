# venus-sample-pdf-fop-memory-poc

> [![GitHub stars](https://img.shields.io/github/stars/fugerit79/venus-sample-pdf-fop-memory-poc?style=social)](https://github.com/fugerit79/venus-sample-pdf-fop-memory-poc/stargazers)  
> ❤️ If this project helps you, please consider dropping a star — it really motivates continued development!


This is a sample project configured using [fj-doc-maven-plugin init plugin](https://venusdocs.fugerit.org/guide/#maven-plugin-goal-init).

[![Keep a Changelog v1.1.0 badge](https://img.shields.io/badge/changelog-Keep%20a%20Changelog%20v1.1.0-%23E05735)](CHANGELOG.md)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=fugerit79_venus-sample-pdf-fop-memory-poc&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=fugerit79_venus-sample-pdf-fop-memory-poc)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=fugerit79_venus-sample-pdf-fop-memory-poc&metric=coverage)](https://sonarcloud.io/summary/new_code?id=fugerit79_venus-sample-pdf-fop-memory-poc)
[![License: MIT](https://img.shields.io/badge/License-MIT-teal.svg)](https://opensource.org/licenses/MIT)
[![code of conduct](https://img.shields.io/badge/conduct-Contributor%20Covenant-purple.svg)](https://github.com/fugerit-org/fj-universe/blob/main/CODE_OF_CONDUCT.md)

This project is part of a series of mini tutorials on [Venus Fugerit Doc](https://github.com/fugerit-org/fj-doc),
here you can find the [other tutorials](https://github.com/fugerit79/venus-sample-index).

---

## Overview: Apache FOP High Memory Consumption POC

This Proof of Concept (POC) reproduces and explains a real-world troubleshooting scenario: **abnormal heap memory consumption and potential `OutOfMemoryError` during Apache FOP PDF generation**.

### The Problem

When generating a PDF from an XSL-FO document that contains very large blocks of text (such as ~30,000 characters or more), Apache FOP memory usage can suddenly explode, consuming hundreds of megabytes or even gigabytes of heap memory for a single document.

#### Specific Trigger Combinations

It is important to note that this critical memory spike typically occurs under a specific combination of factors:

1. **Inside Table Cells (`<cell>` / `<fo:table-cell>`)**:
   - In Apache FOP, table formatting requires negotiating cell widths, row heights, and page breaks across cells.
   - Unlike standard root-level flow paragraphs that FOP can paginate and flush sequentially, blocks inside table cells must be completely evaluated and held in memory during the table layout pass.
2. **A Very Large Unsegmented Block**:
   - A single `<para>` containing a massive amount of text (~30,000 characters or more).
3. **Default Linefeed Handling**:
   - By default in XSL-FO, `<fo:block>` uses `linefeed-treatment="treat-as-space"`. FOP treats newlines as ordinary spaces, causing the entire block to be handled as **one continuous, gigantic paragraph**.

#### The Culprit: Knuth-Plass Line-Breaking Algorithm

> *"The culprit: Apache FOP's Knuth-Plass line-breaking algorithm operating on a single `<fo:block>` of 30,000 characters without `linefeed-treatment="preserve"`"*

Under the hood:
1. **Knuth-Plass Algorithm**: Apache FOP uses the Knuth-Plass line-breaking algorithm (originally designed for TeX) to find the aesthetically optimal line breaks for an entire paragraph by evaluating candidate break points using boxes, glue, and penalties.
2. **Combinatorial Explosion**: Because newlines are treated as spaces, the algorithm attempts to calculate optimal line breaks globally across the entire 30,000+ character block. The active node graph and candidate break states grow exponentially, resulting in severe memory allocation spikes and high CPU time.

---

## The Solution in Fugerit Venus Doc

In [Venus Fugerit Doc](https://github.com/fugerit-org/fj-doc), paragraphs are defined using the `<para>` element.

### 1. Using `format="preserve-line"`

To prevent Apache FOP from running the unconstrained Knuth-Plass optimization over the entire block, set the attribute `format="preserve-line"` on the `<para>` tag:

```xml
<para format="preserve-line" align="left" style="normal" size="12">
    Your very large text content with line breaks...
</para>
```

#### How it works:
- The `fj-doc-mod-fop` handler maps `format="preserve-line"` directly to the XSL-FO attribute `linefeed-treatment="preserve"` on the generated `<fo:block>`:
  ```xml
  <fo:block linefeed-treatment="preserve" ...>
  ```
- With `linefeed-treatment="preserve"`, Apache FOP respects line breaks (`\n`) as hard boundaries. It resets and processes line breaks per line rather than attempting global optimization across all 30,000 characters, completely eliminating the combinatorial explosion.

### 2. Best Practice: Break Large Blocks into Multiple `<para>` Elements

Whenever possible, avoid putting massive text blocks into a single `<para>` tag—especially inside table cells. Splitting text into separate paragraphs or smaller logical blocks ensures optimal layout performance and avoids memory bottlenecks.

---

## Benchmark & Results

The test in this POC compares generating the exact same document containing a ~30,000 character block with and without the fix:

| Metric | Fixed (`format="preserve-line"`) | Original (Default Knuth-Plass) | Difference |
| :--- | :--- | :--- | :--- |
| **Generation Time** | **~1.3 s** | ~2.8 s | **~2x faster** |
| **Used Heap Delta** | **+14.15 MB** | +523.17 MB | **>35x less heap allocated** |
| **Peak Heap Usage** | **181.02 MB** | 951.25 MB | **>5x peak memory reduction** |

*Measurements taken using Java MXBeans during JUnit test execution.*

---

## POC Structure

* **Template**: [`src/main/resources/venus-sample-pdf-fop-memory-poc/template/document.ftl`](src/main/resources/venus-sample-pdf-fop-memory-poc/template/document.ftl)
  - Contains a sample text of ~30,000 characters inside a table cell `<cell><para>...</para></cell>`.
  - Conditioned with `<#if fixLineHandling = '1'> format="preserve-line" </#if>` to toggle the fix.
* **JUnit Test**: [`src/test/java/test/org/fugerit/java/demo/venussamplepdffopmemorypoc/DocHelperTest.java`](src/test/java/test/org/fugerit/java/demo/venussamplepdffopmemorypoc/DocHelperTest.java)
  - Executes PDF generation for both configurations (`fixLineHandling = "1"` and `fixLineHandling = "0"`).
  - Measures elapsed time, heap delta, and peak heap usage using `MemoryPoolMXBean`.

---

## How to Run

### Requirement

* JDK 8+
* Maven 3.8+

### Running the Test

Run the test suite with Maven:

```shell
mvn clean test
```

Inspect the test logs to see the memory benchmark comparison:

```text
[after pdf generation (fixed)]    time: 1344 ms | usedHeap: 20.22 MB (delta: +14.15 MB)  | peakHeap: 181.02 MB
[after pdf generation (original)] time: 2787 ms | usedHeap: 535.25 MB (delta: +523.17 MB) | peakHeap: 951.25 MB
peakMemoryFixed : 181.02 MB vs peakMemoryOriginal : 951.25 MB
```

---

## Project Initialization

This project was created with [Venus Maven plugin](https://venusdocs.fugerit.org/guide/#maven-plugin-goal-init):

```shell
mvn org.fugerit.java:fj-doc-maven-plugin:8.18.4:init \
-DgroupId=org.fugerit.java.demo \
-DartifactId=venus-sample-pdf-fop-memory-poc \
-Dextensions=base,freemarker,mod-fop \
-DaddJacoco=true \
-DaddJacoco=addFormatting=true \
-DwithCI=github \
-Dflavour=vanilla
```
