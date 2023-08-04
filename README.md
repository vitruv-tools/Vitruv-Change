# Vitruv Change
[![GitHub Action CI](https://github.com/vitruv-tools/Vitruv-Change/actions/workflows/ci.yml/badge.svg)](https://github.com/vitruv-tools/Vitruv-Change/actions/workflows/ci.yml)
[![Latest Release](https://img.shields.io/github/release/vitruv-tools/Vitruv-Change.svg)](https://github.com/vitruv-tools/Vitruv-Change/releases/latest)
[![Issues](https://img.shields.io/github/issues/vitruv-tools/Vitruv-Change.svg)](https://github.com/vitruv-tools/Vitruv-Change/issues)
[![License](https://img.shields.io/github/license/vitruv-tools/Vitruv-Change.svg)](https://raw.githubusercontent.com/vitruv-tools/Vitruv-Change/main/LICENSE)

[Vitruvius](https://vitruv.tools) is a framework for view-based (software) development.
It assumes different models to be used for describing a system, which are automatically kept consistent by the framework executing (semi-)automated rules that preserve consistency.
These models are modified only via views, which are projections from the underlying models.
For general information on Vitruvius, see our [GitHub Organisation](https://github.com/vitruv-tools) and our [Wiki](https://github.com/vitruv-tools/.github/wiki).

This project contains the underlying definition of changes in Ecore-based models and interfaces for specifying the propagation of changes between models to preserve their consistency with the central interface `ChangePropagationSpecification`, as well as an interface and a default implementation for orchestrating the execution of such specifications.
In addition, interactions to involve the user into the change preservation process are provided.

## Installation

Vitruvius can be installed in Eclipse via the [nightly update site](https://vitruv.tools/updatesite/nightly). A wiki page provides [detailed instructions for using or extending Vitruvius or parts of it](https://github.com/vitruv-tools/.github/wiki/Getting-Started).
