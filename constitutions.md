# Repository Constitutions

This repository follows the **OpenSpec** framework for AI-assisted software development to ensure maintainability, predictability, and structure.

## Core Rules

1. **English Language Requirement**
   All documentation, specifications (including this file), commit messages, and code (comments, variable names, etc.) MUST be written in English. Interactions with the user in the prompt may occur in other languages (e.g., German), but the resulting artifacts within the project must strictly be in English.

2. **Structure Before Code**
   Do not write or modify code before the specification is defined and approved by the human developer. The specification serves as the source of truth.

## OpenSpec Workflow

1. **Explore**: Discuss and explore ideas without generating formal files.
2. **Propose**: Create a "Delta Spec" (e.g., `feature-name.spec.md`) detailing the proposed changes. Use explicit sections for `ADDED`, `MODIFIED`, and `REMOVED` elements.
3. **Apply**: Write code strictly following the approved Delta Spec. Do not deviate from the agreed-upon specification.
4. **Archive**: Once the code is merged, integrate the changes from the Delta Spec into the main `spec.md` file to keep the living documentation up to date, and then archive or delete the Delta Spec.

## Single Source of Truth

- The `spec.md` file in the root directory is the living documentation and the single source of truth for the project's current state and architecture.
