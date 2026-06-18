# EC2 Auto Deploy

This project deploys to EC2 through GitHub Actions.

When code is pushed to `main`, GitHub Actions:

1. Runs the Gradle test suite.
2. Connects to EC2 over SSH.
3. Clones the repository on the first deploy, or pulls the latest code on later deploys.
4. Runs `docker compose up -d --build`.

## EC2 one-time setup

Install Docker, Docker Compose, and Git:

```bash
sudo apt update
sudo apt install -y docker.io docker-compose-plugin git
sudo usermod -aG docker ubuntu
exit
```

Reconnect to EC2 after running `usermod`.

Create the app directory and `.env` file:

```bash
mkdir -p ~/apps/dropdeal-be
cd ~/apps/dropdeal-be
nano .env
```

Use this as the base `.env`:

```env
APP_PORT=8080

POSTGRES_DB=dropdeal
POSTGRES_USER=dropdeal
POSTGRES_PASSWORD=change-this-password
POSTGRES_PORT=5432

REDIS_PORT=6379

CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
SWAGGER_ENABLED=false
```

The deploy workflow keeps this `.env` file on EC2. Do not commit it.

## GitHub Secrets

Add these repository secrets in GitHub:

`Settings` -> `Secrets and variables` -> `Actions` -> `New repository secret`

```text
EC2_HOST=your-ec2-public-ip-or-domain
EC2_USER=ubuntu
EC2_APP_DIR=/home/ubuntu/apps/dropdeal-be
EC2_SSH_KEY=contents of your .pem private key
```

`EC2_SSH_KEY` must include the full private key text, including:

```text
-----BEGIN ... PRIVATE KEY-----
...
-----END ... PRIVATE KEY-----
```

## Deploy

Push to `main`:

```bash
git push origin main
```

Then check the workflow in GitHub Actions.

On EC2, logs can be checked with:

```bash
cd ~/apps/dropdeal-be
docker compose ps
docker compose logs -f backend
```

Health check:

```bash
curl http://localhost:8080/actuator/health
```
