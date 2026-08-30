## Why

Brickventory backend currently requires manual deployment steps, making it difficult to integrate new features and fixes quickly. Automating deployments on every main branch push enables continuous delivery, faster feedback loops, and reduces human error. Using Render provides a free, persistent hosting solution suitable for development and testing with acceptable cold starts.

## What Changes

- Add GitHub Actions workflow that triggers on main branch pushes
- Build and test Spring Boot backend automatically
- Deploy to Render platform (free tier with PostgreSQL included)
- Provide a stable production URL for the frontend app to connect to
- Accept ~5 second cold starts after 15 minutes of inactivity (free tier limitation)
- Add deployment status notifications/logging

## Capabilities

### New Capabilities
- `render-cd-pipeline`: Automated CI/CD pipeline using GitHub Actions that builds, tests, and deploys the Kotlin/Spring Boot backend to Render on every main branch commit

### Modified Capabilities
- `brickventory-main`: The backend now has automated deployment; the API specification remains unchanged but deployment mechanism is now automated rather than manual

## Impact

- **Code**: New `.github/workflows/deploy-to-render.yml` GitHub Actions workflow
- **Infrastructure**: Integration with Render platform (requires Render project and API key)
- **APIs**: No API changes; existing backend endpoints remain the same
- **Dependencies**: No new backend dependencies; GitHub Actions uses standard deployment tooling
- **Systems**: Deployment system moves from manual to fully automated
- **Frontend**: App configuration will reference the stable Render.com URL
- **Performance**: Accept 5-10 second cold starts on first request after 15 minutes idle
