## ADDED Requirements

### Requirement: GitHub Actions workflow triggers on main branch push
The system SHALL execute a deployment workflow automatically whenever code is pushed to the main branch.

#### Scenario: Push to main triggers deployment
- **WHEN** a commit is pushed to the main branch
- **THEN** GitHub Actions workflow is triggered automatically

#### Scenario: Push to other branches does not trigger deployment
- **WHEN** a commit is pushed to a non-main branch (e.g., feature branch)
- **THEN** GitHub Actions workflow is not triggered

### Requirement: Backend code is compiled and tested
The system SHALL build the Spring Boot application and execute all unit/integration tests before deployment.

#### Scenario: Build succeeds with passing tests
- **WHEN** the workflow runs
- **THEN** Maven/Gradle compiles the Kotlin backend successfully AND all tests pass

#### Scenario: Build fails with test failures
- **WHEN** tests fail during the build
- **THEN** workflow stops and does not deploy; failure is reported to GitHub

#### Scenario: Build fails with compilation errors
- **WHEN** there are compilation errors in the code
- **THEN** workflow stops and does not deploy; error logs are available in GitHub Actions

### Requirement: Compiled application is deployed to Render
The system SHALL package and deploy the compiled Spring Boot application to a Render Web Service.

#### Scenario: Successful deployment
- **WHEN** build and tests pass
- **THEN** application is packaged (JAR/Docker) and deployed to Render Web Service

#### Scenario: Cold start after idle period
- **WHEN** the Render service has been idle for 15+ minutes
- **THEN** first request takes 5-10 seconds to complete as service wakes up

#### Scenario: Deployment failure keeps previous version running
- **WHEN** deployment fails (e.g., Render API timeout, build error in deployment)
- **THEN** workflow logs the error and previous version continues running

### Requirement: Application health is verified after deployment
The system SHALL confirm that the deployed application is responding to requests.

#### Scenario: Health check passes
- **WHEN** deployment completes
- **THEN** workflow waits 30 seconds for Render to stabilize, then makes a request to `/api/health` (or similar) endpoint and receives 200 OK response

#### Scenario: Health check fails
- **WHEN** health check does not receive a successful response within timeout
- **THEN** workflow marks deployment as failed and alerts via GitHub

### Requirement: Deployment is secured with credentials
The system SHALL protect Render API credentials and never expose them in logs or source code.

#### Scenario: API key is stored securely
- **WHEN** workflow runs
- **THEN** Render API key is retrieved from GitHub Secrets (encrypted) and injected at runtime

#### Scenario: Credentials do not appear in logs
- **WHEN** workflow runs
- **THEN** GitHub Actions automatically masks sensitive values; API key does not appear in output logs

### Requirement: Stable persistent URL is available
The system SHALL provide a consistent, persistent `.onrender.com` URL for the deployed API that the frontend can connect to.

#### Scenario: Frontend accesses deployed API
- **WHEN** frontend makes a request to the Render `.onrender.com` URL
- **THEN** request is routed to the latest deployed backend version

#### Scenario: URL remains the same across deployments
- **WHEN** a new version is deployed
- **THEN** the Render `.onrender.com` URL does not change; previous deployments' URL maps to new version

### Requirement: Service wakes reliably from idle state
The system SHALL handle the cold start scenario gracefully without losing data or state.

#### Scenario: Service wakes and serves request
- **WHEN** a request arrives after 15+ minutes of inactivity
- **THEN** Render service wakes, application starts, and request is processed (within 5-10 seconds)

#### Scenario: Database connection re-established after wake
- **WHEN** service wakes from idle
- **THEN** PostgreSQL connection pool re-establishes and queries succeed

## MODIFIED Requirements

### Requirement: Backend API specification
The Brickventory backend API endpoints remain unchanged in terms of interface. The only modification is that deployment is now automated rather than manual.

All existing requirements from `openspec/specs/main/spec.md` regarding API endpoints (/api/auth, /api/bricks, /api/sets, etc.) continue to apply unchanged.

#### Scenario: Deployed API maintains compatibility
- **WHEN** a new version is deployed to Railway
- **THEN** all existing API endpoints continue to work with the same request/response format as before
