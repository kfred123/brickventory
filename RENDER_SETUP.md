# Render CI/CD Deployment Setup Guide

This guide walks through setting up automated deployments to Render via GitHub Actions.

## Overview

The CI/CD pipeline:
1. **Triggers** on every push to the `main` branch
2. **Builds** the Spring Boot backend with Maven
3. **Tests** to ensure code quality
4. **Deploys** to Render Web Service
5. **Verifies** health with API health check

## Prerequisites

- GitHub account with repository access
- Render account (https://render.com) - free tier
- Repository with `.github/workflows/deploy-to-render.yml` file

## Step 1: Create Render Project (Manual Setup)

### 1.1 Create Render Account
- Go to https://render.com and sign up (free)
- Complete email verification

### 1.2 Create PostgreSQL Database
- In Render Dashboard: Click "New" → "PostgreSQL"
- Configuration:
  - **Name**: `brickventory-postgres`
  - **Database**: `brickventory`
  - **User**: Keep auto-generated
  - **Region**: Choose closest to you
  - **Pricing Plan**: Free
- Click "Create Database"
- **Note the connection string** (you'll need this for Web Service)

### 1.3 Create Web Service
- In Render Dashboard: Click "New" → "Web Service"
- **Connect Repository**:
  - Click "Connect your GitHub account" if not already connected
  - Select `kfred123/brickventory` repository
  - Branch: `main`
  - Auto-deploy: **ON**
- **Configuration**:
  - **Name**: `brickventory-api`
  - **Runtime**: `Docker`
  - **Region**: Same as database
  - **Pricing Plan**: Free
  - **Build Command**: `cd brick-server && mvn clean package -DskipTests`
  - **Start Command**: `java -jar target/brick-server-0.0.1-SNAPSHOT.jar`
- **Environment Variables**:
  - Add the PostgreSQL connection variables from step 1.2
  - Example:
    ```
    SPRING_DATASOURCE_URL=postgresql://user:password@host:5432/brickventory
    SPRING_DATASOURCE_USERNAME=user
    SPRING_DATASOURCE_PASSWORD=password
    SPRING_JPA_HIBERNATE_DDL_AUTO=validate
    ```
- Click "Create Web Service"

### 1.4 Get Render Credentials
After Web Service is created:
1. **Render API Key**: 
   - Account Settings → API Keys
   - Create new API Key
   - Copy the full key

2. **Service ID**:
   - Open Web Service settings
   - Copy the **Service ID** from URL or settings

3. **Deploy Hook** (Optional, but recommended):
   - Web Service Settings → Deploy Hook
   - Copy the Deploy Hook URL

4. **Service URL**:
   - Will be something like: `https://brickventory-api.onrender.com`
   - Note this URL

## Step 2: Configure GitHub Secrets

1. Open GitHub Repository → Settings → Secrets and variables → Actions
2. Add the following secrets:

| Secret Name | Value | Source |
|-------------|-------|--------|
| `RENDER_API_KEY` | Your Render API Key | Render Account Settings |
| `RENDER_SERVICE_ID` | Your Service ID | Render Web Service Settings |
| `RENDER_DEPLOY_HOOK` | Deploy Hook URL | Render Web Service Deploy Hook |
| `RENDER_URL` | `https://brickventory-api.onrender.com` | Render Web Service URL |

**Important**: Never commit these secrets to git. GitHub will mask them in logs.

## Step 3: Test the Workflow

### 3.1 Trigger Workflow
1. Make a small change to `brick-server/**` files
2. Commit to `main` branch
3. Push to GitHub

### 3.2 Monitor Deployment
1. Go to GitHub Repository → Actions tab
2. Click the latest workflow run "Deploy Backend to Render"
3. Watch the job progress:
   - ✅ Checkout code
   - ✅ Setup JDK 21
   - ✅ Build with Maven
   - ✅ Run tests
   - ✅ Trigger Render deployment
   - ⏳ Wait 30 seconds
   - ✅ Health check

### 3.3 Verify Deployment
Once workflow completes successfully:
```bash
# Test API endpoint
curl https://brickventory-api.onrender.com/api/health

# Expected response (200 OK):
{"status":"UP","timestamp":1693472000000,"version":"1.0.0"}

# Test another endpoint
curl https://brickventory-api.onrender.com/api/bricks
```

## Troubleshooting

### ❌ Health check fails
- **Cause**: Render service not ready or endpoint doesn't exist
- **Fix**: 
  - Wait 30+ seconds after deployment
  - Verify `/api/health` endpoint exists
  - Check `HealthController.kt` is in `brick-server`

### ❌ Deployment skipped (secrets not configured)
- **Cause**: GitHub Secrets not set
- **Fix**: 
  - Add all secrets to GitHub (Step 2)
  - Re-run workflow

### ❌ Build fails
- **Cause**: Compilation or test errors
- **Fix**:
  - Check GitHub Actions logs for error details
  - Fix code locally
  - Push fix to main

### ❌ Cold start delays (first request after idle)
- **Expected**: Render free tier has 15-min auto-sleep
- **Mitigation**:
  - First request takes 5-10 seconds
  - Render wakes service automatically
  - Frontend should show loading indicator

### ❌ Can't connect to database
- **Cause**: PostgreSQL connection string incorrect
- **Fix**:
  - Verify `SPRING_DATASOURCE_URL` in Render Web Service env vars
  - Format: `postgresql://user:password@host:5432/dbname`
  - Check credentials from Render PostgreSQL dashboard

## Manual Deployment (if automated fails)

If GitHub Actions fails repeatedly:

1. **Via Render Dashboard**:
   - Open Web Service
   - Click "Manual Deploy"
   - Select branch: `main`
   - Click "Deploy"

2. **Via Render CLI**:
   ```bash
   npm install -g render-cli
   render login
   render deploy --service-id YOUR_SERVICE_ID
   ```

3. **Via Git Push** (if Render auto-deploy enabled):
   - Push to main branch
   - Render automatically deploys

## Monitoring & Maintenance

### Monitor Deployments
- GitHub: Actions tab → Deployments
- Render: Web Service → Logs

### Monitor Costs
- Render Free Tier: Included with free plan
- Set up billing alerts in Render Account Settings

### Update Deployment Config
- Edit `.github/workflows/deploy-to-render.yml`
- Change build/start commands in Render Web Service settings
- Update environment variables as needed

## Frontend Configuration

Update your frontend to point to Render API:

```env
# .env or similar
REACT_APP_API_URL=https://brickventory-api.onrender.com/api
```

Restart frontend dev server or rebuild.

## Rollback Deployment

### Quick Rollback
```bash
# Revert last commit
git revert HEAD
git push main
```

### Via Render Dashboard
1. Open Web Service
2. Click "Deployments"
3. Find previous successful deployment
4. Click "Redeploy"

## Performance Tips

### Reduce Cold Starts
- Keep database queries efficient
- Use connection pooling (already configured in Spring Boot)
- Minimize startup time

### Optimize Build Times
- Cache Maven dependencies in GitHub Actions (already configured)
- Exclude tests from build if needed

### Database Performance
- Monitor query logs in Render PostgreSQL
- Add indexes for frequently queried columns
- Archive old data if needed

## Next Steps

1. ✅ Complete setup (Steps 1-2)
2. ✅ Test workflow (Step 3)
3. Deploy frontend to connect to Render API
4. Set up monitoring/alerts (optional)
5. Document runbooks for your team

## References

- [Render Documentation](https://render.com/docs)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Spring Boot Deployment](https://spring.io/guides/gs/spring-boot)
- [Kotlin + Spring Boot](https://spring.io/guides/tutorials/spring-boot-kotlin)
