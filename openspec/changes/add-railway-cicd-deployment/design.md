## Context

Brickventory is a monorepo with a Kotlin/Spring Boot backend (brick-server) and React frontend (brick-app). Currently, deployments are manual. The goal is to automate deployment to Render on every main branch push, ensuring a test/staging environment is always up-to-date with the latest code.

Render was chosen for:
- **Free tier**: Truly free, unlimited usage (no $5/month requirement)
- **Persistent URLs**: Stable `.onrender.com` domain for frontend to connect to
- **GitHub integration**: Native webhook support for automated deploys
- **PostgreSQL included**: Free PostgreSQL database for development/testing
- **Cold starts acceptable**: 5-10 second wake-up time after 15 min idle is acceptable for non-production
- **Simplicity**: Easier setup than Fly.io, better free tier than Railway

## Goals / Non-Goals

**Goals:**
1. Automate backend deployment on every main branch push via GitHub Actions
2. Deploy to Render with a stable, persistent URL
3. Run automated tests before deployment to catch regressions
4. Enable quick feedback loops for feature development
5. Provide deployment logs and status visibility
6. Minimize operational cost (100% free tier)

**Non-Goals:**
- Frontend CI/CD (out of scope; focus on backend)
- Database migrations (assumes Render postgres is pre-configured)
- Blue-green deployments (Render handles rolling restarts)
- Multi-region deployments (single Render instance)
- Production SLA (this is a dev/test environment with acceptable cold starts)

## Decisions

### 1. GitHub Actions for CI/CD
**Decision:** Use GitHub Actions instead of external CI/CD service  
**Rationale:** Native GitHub integration, no additional vendor lock-in, free for public repos  
**Alternatives considered:**
- CircleCI (more features, but overkill for this project)
- Jenkins (self-hosted, too much operational overhead)
- GitLab CI (would require moving repo, unnecessary migration)

### 2. Render as deployment target
**Decision:** Deploy to Render platform (free tier)  
**Rationale:** Truly free tier, stable URL, PostgreSQL included, cold starts acceptable for testing  
**Alternatives considered:**
- Railway ($5/month, not truly free)
- Fly.io (better but more complex setup)
- Self-hosted VPS (requires DevOps expertise, higher maintenance)

### 3. Deployment triggers
**Decision:** Deploy only on main branch pushes (not on pull requests)  
**Rationale:** Main branch represents production-ready code; avoids deploying every PR draft  
**Alternatives considered:**
- Deploy on every branch (too noisy, wastes resources)
- Manual trigger only (defeats automation purpose)

### 4. Accept cold starts
**Decision:** Accept 5-10 second cold start after 15 minutes of inactivity  
**Rationale:** Free tier limitation; acceptable for development/testing environment  
**Alternatives considered:**
- Paid tier (defeats "free" goal)
- Pin deployment to prevent sleeping (wastes free credits, use Fly.io instead)

### 5. Health checks before marking deployment complete
**Decision:** Include health check after deployment (curl /api/health or similar)  
**Rationale:** Ensures API is responsive before considering deploy successful  
**Alternatives considered:**
- Skip health checks (risk of silent failures)
- Use Render's built-in health checks (supplementary, but our own ensures business logic works)

## Risks / Trade-offs

| Risk | Mitigation |
|------|-----------|
| **Cold starts break frontend UX** | Document in frontend error handling; show loading state on first API call |
| **Render free tier gets deprecated** | Monitor Render status; have Fly.io as backup plan |
| **Secrets exposure** (Render API key in GitHub Actions) | Use GitHub Secrets to store RENDER_API_KEY safely; never commit to repo |
| **Failed deployments break test environment** | Include automated tests in workflow; manual fix via git revert if needed |
| **No automatic rollback** | Manual rollback: revert commit, push to main, or use Render dashboard |
| **Render API rate limits** | Monitor deployment frequency; not an issue for typical development flow |

## Migration Plan

1. **Setup Phase** (manual, one-time):
   - Create Render account at render.com
   - Create Web Service and PostgreSQL database in Render
   - Generate Render API key (stored in GitHub Secrets)
   - Note the permanent Render URL

2. **Implementation Phase**:
   - Create `.github/workflows/deploy-to-render.yml`
   - Configure workflow to trigger on main branch pushes
   - Test workflow with a test commit

3. **Rollout Phase**:
   - Merge workflow to main
   - First automated deployment happens automatically
   - Monitor deployment logs and API health
   - Frontend environment config uses Render URL

4. **Rollback Strategy**:
   - If deployment fails: GitHub Actions shows logs; fix and push new commit
   - If manual rollback needed: Revert commit and push, or manually restart previous deployment via Render dashboard
   - Render keeps deployment history for easy rollback

## Open Questions

1. Should we add manual approval gates for deployments? (Decision: No for now; main branch == test-ready)
2. Should frontend auto-detect and use Render URL? (Decision: Use .env file for now)
3. Should we set up Render alerts for downtime? (Decision: Out of scope for MVP)
