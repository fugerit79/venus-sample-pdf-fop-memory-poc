# venus-sample-pdf-fop-memory-poc

> [![GitHub stars](https://img.shields.io/github/stars/fugerit79/venus-sample-pdf-fop-memory-poc?style=social)](https://github.com/fugerit79/venus-sample-pdf-fop-memory-poc/stargazers)  
> ❤️ If this project helps you, please consider dropping a star — it really motivates continued development!


This is a sample project configured using [fj-doc-maven-plugin init plugin](https://venusdocs.fugerit.org/guide/#maven-plugin-goal-init).

[![Keep a Changelog v1.1.0 badge](https://img.shields.io/badge/changelog-Keep%20a%20Changelog%20v1.1.0-%23E05735)](CHANGELOG.md)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=fugerit79_venus-sample-pdf-fop-memory-poc&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=fugerit79_venus-sample-pdf-fop-memory-poc)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=fugerit79_venus-sample-pdf-fop-memory-poc&metric=coverage)](https://sonarcloud.io/summary/new_code?id=fugerit79_venus-sample-pdf-fop-memory-poc)
[![License: MIT](https://img.shields.io/badge/License-MIT-teal.svg)](https://opensource.org/licenses/MIT)
[![code of conduct](https://img.shields.io/badge/conduct-Contributor%20Covenant-purple.svg)](https://github.com/fugerit-org/fj-universe/blob/main/CODE_OF_CONDUCT.md)

This project is part of a series of mini tutorial on [Venus Fugerit Doc](https://github.com/fugerit-org/fj-doc),
here you can find the [other tutorials](https://github.com/fugerit79/venus-sample-index).

## Requirement

* JDK 8+ (*)
* Maven 3.8+

## Project initialization

This project was created with [Venus Maven plugin](https://venusdocs.fugerit.org/guide/#maven-plugin-goal-init)

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
