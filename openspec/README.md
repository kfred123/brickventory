# OpenSpec Configuration for Brickventory

This project uses OpenSpec with GitHub Copilot for spec-driven development.

## Reference Specifications

The project has two main specifications:

1. **Main Specification** (`spec.md`)
   - Architecture, data models, and API endpoints
   - Current reference state of the Brickventory application

2. **Monorepo Migration** (`001-monorepo.spec.md`)
   - Consolidation of brick-app and brick-server into a single monorepo
   - Historical specification for the migration

## Using OpenSpec

### Available Commands

Use these slash commands in GitHub Copilot to work with OpenSpec:

- `/opsx:propose "your change description"` - Propose a new change
- `/opsx:explore` - Explore existing changes  
- `/opsx:apply` - Apply and implement a change
- `/opsx:archive "change-name"` - Archive a completed change

### Workflow

1. **Propose**: Use `/opsx:propose` to describe a change. OpenSpec creates:
   - `proposal.md` (what & why)
   - `specs.md` (specification)
   - `design.md` (architecture)
   - `tasks.md` (implementation steps)

2. **Review & Refine**: Review the generated artifacts

3. **Implement**: Run `/opsx:apply` to start implementation

4. **Archive**: After completion, archive the change to update main specs

## Directory Structure

```
openspec/
├── specs/                    # Reference specifications
│   ├── 00-brickventory-main.md
│   └── 01-monorepo-migration.md
├── changes/                  # Change proposals and implementations
├── .openspec.yaml           # OpenSpec project configuration
└── .openspec/               # Internal OpenSpec data
```

## Integration with GitHub Copilot

The `.github/` directory contains Copilot-specific skills and prompts:

- Skills in `.github/skills/` enable OpenSpec commands
- Prompts in `.github/prompts/` guide the AI assistant
- This setup is tool-agnostic and can be adapted for other AI tools
