# Delta Spec: Monorepo Migration

## Goal
Combine the separate `brick-app` and `brick-server` repositories into a single Git monorepo at the project root to simplify development and continuous integration.

## Architecture

### MODIFIED
- **Repository Structure**: The project now utilizes a monorepo approach. Both backend (`brick-server`) and frontend (`brick-app`) share a single Git history.
- **Git Tracking**: The `.git` directories inside sub-projects are removed, and a new root `.git` directory is established.

### ADDED
- Root `.gitignore` file.

### REMOVED
- `brick-app/.git`
- `brick-server/.git`
