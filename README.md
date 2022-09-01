# Vitruv Change
[![GitHub Action CI](https://github.com/vitruv-tools/Vitruv-Change/workflows/CI/badge.svg)](https://github.com/vitruv-tools/Vitruv-Change/actions?query=workflow%3ACI)
[![Latest Release](https://img.shields.io/github/release/vitruv-tools/Vitruv-Change.svg)](https://github.com/vitruv-tools/Vitruv-Change/releases/latest)
[![Issues](https://img.shields.io/github/issues/vitruv-tools/Vitruv-Change.svg)](https://github.com/vitruv-tools/Vitruv-Change/issues)
[![License](https://img.shields.io/github/license/vitruv-tools/Vitruv-Change.svg)](https://raw.githubusercontent.com/vitruv-tools/Vitruv-Change/main/LICENSE)

[Vitruv](https://vitruv.tools) is a framework for view-based software development. It assumes different models to be used for describing a software system,
which are automatically kept consistent by the framework and its applications. For general information on Vitruv, see our [GitHub Organisation](https://github.com/vitruv-tools) and our [Wiki](https://github.com/vitruv-tools/.github/wiki).

This project contains the underlying definition of changes in Ecore-based models and interfaces for specifying the propagation of changes between models to preserve their consistency, as well as an interface and a default implementation for orchestrating the execution of such specifications.

## Installation

The Vitruv change specification and propagation projects can be installed in Eclipse via the [nightly update site](https://vitruv.tools/updatesite/nightly). A wiki page provides [detailed instructions for using or extending Vitruv or parts of it](https://github.com/vitruv-tools/.github/wiki/Getting-Started).

## Project Development

Vitruv is realized as Eclipse Plug-ins and depends on the following Eclipse tools:
- Eclipse Modeling Framework (EMF) _as the modelling environment_
- Xtend _for code_
