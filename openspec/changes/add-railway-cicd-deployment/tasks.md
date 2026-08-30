## 1. Render Setup (Manual, One-Time)

- [ ] 1.1 Create Render account at render.com (free signup)
- [ ] 1.2 Create new PostgreSQL database in Render (free tier)
- [ ] 1.3 Create new Web Service in Render for Spring Boot backend
- [ ] 1.4 Configure Render environment variables (DB connection, app settings)
- [ ] 1.5 Generate Render API key for CI/CD access
- [ ] 1.6 Note the permanent Render URL (e.g., `https://brickventory-api.onrender.com`)
- [ ] 1.7 Configure Render service to auto-deploy on Git push (enable/disable as needed)

## 2. GitHub Secrets Configuration

- [ ] 2.1 Open GitHub repository settings → Secrets and variables
- [ ] 2.2 Add secret `RENDER_API_KEY` with the API key from step 1.5
- [ ] 2.3 Add secret `RENDER_SERVICE_ID` with Render Web Service ID
- [ ] 2.4 Add secret `RENDER_DEPLOY_HOOK` (optional, if using deploy hooks)
- [ ] 2.5 Verify secrets are masked (not visible in logs)

## 3. Create GitHub Actions Workflow

- [ ] 3.1 Create `.github/workflows/deploy-to-render.yml` file
- [ ] 3.2 Configure workflow trigger: `on: [push]` with `branches: [main]`
- [ ] 3.3 Add checkout step to clone repository
- [ ] 3.4 Add Java/Kotlin setup step (set JDK version to match project)
- [ ] 3.5 Add Maven/Gradle build step with `mvn clean package` or equivalent
- [ ] 3.6 Add test execution step to run unit/integration tests
- [ ] 3.7 Add Render API authentication using stored API key
- [ ] 3.8 Add deployment step using Render API (curl or REST call to trigger deploy)
- [ ] 3.9 Add wait step (30 seconds) for Render to stabilize deployment
- [ ] 3.10 Add health check step (curl to Render URL's `/api/health` endpoint)
- [ ] 3.11 Add error notification (optional: Slack, email, or GitHub status)

## 4. Test Workflow

- [ ] 4.1 Commit workflow file to main branch
- [ ] 4.2 Push to GitHub and monitor Actions tab for execution
- [ ] 4.3 Verify build step completes successfully
- [ ] 4.4 Verify tests pass (or fail with clear error messages)
- [ ] 4.5 Verify Render API call succeeds (deployment triggered)
- [ ] 4.6 Wait 30+ seconds and verify health check passes
- [ ] 4.7 Access Render Web Service URL and verify API responds
- [ ] 4.8 Make a sample API call (e.g., GET /api/bricks) and verify response
- [ ] 4.9 Test cold start: wait 15 minutes idle, then make request and note startup time

## 5. Render Configuration & Optimization

- [ ] 5.1 Configure Render to auto-scale (if needed)
- [ ] 5.2 Set up PostgreSQL connection pooling in Spring Boot for cold starts
- [ ] 5.3 Review Render billing dashboard (confirm free tier usage)
- [ ] 5.4 Configure Render to keep web service on free plan (no paid upgrades)
- [ ] 5.5 Test Render deploy history and manual rollback from Render dashboard

## 6. Frontend Integration

- [ ] 6.1 Document the Render production URL in README
- [ ] 6.2 Update frontend `.env` or config to point to Render URL
- [ ] 6.3 Test frontend can connect to deployed backend API
- [ ] 6.4 Document cold start behavior for frontend team (expected 5-10s on first request after idle)
- [ ] 6.5 Add frontend error handling for cold start delays

## 7. Documentation & Handoff

- [ ] 7.1 Document the production Render URL in README or project docs
- [ ] 7.2 Document how to manually rollback (via Render dashboard or git revert)
- [ ] 7.3 Document how to monitor deployments (GitHub Actions Deployments tab)
- [ ] 7.4 Add troubleshooting guide for common issues (cold starts, database connection)
- [ ] 7.5 Document Render free tier limits and what to do if exceeded
- [ ] 7.6 Update team documentation with CI/CD workflow process

## 8. Verification & Launch

- [ ] 8.1 Make a test commit to main with a minor code change
- [ ] 8.2 Verify workflow triggers automatically
- [ ] 8.3 Monitor Render dashboard during deployment
- [ ] 8.4 Verify new code is deployed successfully
- [ ] 8.5 Test API functionality against Render
- [ ] 8.6 Confirm frontend can connect to deployed backend
- [ ] 8.7 Test a complete user flow (auth, read/write data)
- [ ] 8.8 Document any issues or improvements needed
- [ ] 8.9 Mark deployment pipeline as ready for team use
