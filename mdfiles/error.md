# EC2 GitHub Actions Deploy Errors

GitHub Actions에서 EC2 자동 배포 파이프라인을 구성하면서 발생한 에러와 해결 내용입니다.

## 1. `./gradlew: Permission denied`

### 에러

```text
Run ./gradlew test
/home/runner/work/_temp/...sh: line 1: ./gradlew: Permission denied
Error: Process completed with exit code 126.
```

### 원인

Linux GitHub Actions runner에서 `./gradlew`를 실행하려면 실행 권한이 필요합니다.

레포의 `gradlew` 파일 모드가 `100644`라서 실행할 수 없었습니다.

### 해결

Git 파일 모드를 실행 가능 상태로 변경했습니다.

```bash
git update-index --chmod=+x gradlew
git commit -m "Gradle 래퍼 실행 권한 추가"
git push origin main
```

결과적으로 `gradlew` 파일 모드가 `100755`가 되었습니다.

## 2. `ssh-keyscan` 실패

### 에러

```text
Run ssh-keyscan -H "***" >> ~/.ssh/known_hosts
Error: Process completed with exit code 1.
```

### 원인

GitHub Actions runner가 EC2의 SSH 포트에 접근하지 못했습니다.

초기에는 EC2 보안 그룹의 SSH 인바운드가 로컬 PC IP만 허용된 상태였고, GitHub Actions runner의 IP는 허용되지 않았습니다.

### 해결

EC2 보안 그룹 인바운드 규칙에서 SSH를 Anywhere IPv4로 열었습니다.

```text
Type: SSH
Protocol: TCP
Port: 22
Source: 0.0.0.0/0
```

참고로 `EC2_HOST` 값은 IP만 넣어야 합니다.

```text
EC2_HOST=52.79.231.192
```

잘못된 예:

```text
EC2_HOST=ec2-user@52.79.231.192
EC2_HOST=http://52.79.231.192
```

## 3. `Load key ... error in libcrypto`

### 에러

```text
Load key "/home/runner/.ssh/ec2_deploy_key": error in libcrypto
***@***: Permission denied (publickey,gssapi-keyex,gssapi-with-mic).
Error: Process completed with exit code 255.
```

### 원인

GitHub Secret `EC2_SSH_KEY`에 private key가 올바른 형식으로 들어가지 않았습니다.

SSH private key는 여러 줄 그대로 들어가야 하며, `BEGIN`과 `END` 줄도 포함되어야 합니다.

### 해결

`.pem` 파일 내용을 처음 줄부터 마지막 줄까지 그대로 `EC2_SSH_KEY`에 다시 등록했습니다.

올바른 예:

```text
-----BEGIN RSA PRIVATE KEY-----
...
-----END RSA PRIVATE KEY-----
```

또는:

```text
-----BEGIN OPENSSH PRIVATE KEY-----
...
-----END OPENSSH PRIVATE KEY-----
```

잘못된 예:

```text
C:\Users\leeka\Downloads\key.pem
-----BEGIN OPENSSH PRIVATE KEY-----\n...\n-----END OPENSSH PRIVATE KEY-----
```

## 4. `/home/ubuntu` 권한 오류

### 에러

```text
mkdir: cannot create directory '/home/ubuntu': Permission denied
Error: Process completed with exit code 1.
```

### 원인

Amazon Linux 2023의 기본 SSH 사용자는 보통 `ec2-user`입니다.

그런데 GitHub Secret `EC2_APP_DIR`이 Ubuntu 기준 경로로 설정되어 있었습니다.

```text
EC2_APP_DIR=/home/ubuntu/apps/dropdeal-be
```

`ec2-user`는 `/home/ubuntu` 아래에 디렉터리를 만들 권한이 없습니다.

### 해결

Amazon Linux 2023 기준으로 Secret 값을 수정했습니다.

```text
EC2_USER=ec2-user
EC2_APP_DIR=/home/ec2-user/apps/dropdeal-be
```

## 5. `docker: 'compose' is not a docker command`

### 에러

```text
docker: 'compose' is not a docker command.
See 'docker --help'
Error: Process completed with exit code 1.
```

### 원인

EC2에 Docker Engine은 설치되어 있었지만 Docker Compose CLI plugin이 설치되어 있지 않았습니다.

### 해결

GitHub Actions 배포 스크립트에 Docker Compose plugin 자동 설치 로직을 추가했습니다.

Amazon Linux 2023 기본 저장소에서 `docker-compose-plugin` 패키지를 찾지 못하는 경우가 있어, Docker Compose 공식 바이너리를 직접 설치하도록 처리했습니다.

설치 경로:

```text
/usr/local/lib/docker/cli-plugins/docker-compose
```

설치 후 확인:

```bash
docker compose version
```

## 6. `No match for argument: docker-compose-plugin`

### 에러

```text
No match for argument: docker-compose-plugin
Error: Unable to find a match: docker-compose-plugin
Error: Process completed with exit code 1.
```

### 원인

Amazon Linux 2023의 활성화된 기본 패키지 저장소에서 `docker-compose-plugin` 패키지를 찾을 수 없었습니다.

### 해결

패키지 설치 방식 대신 Docker Compose 공식 standalone CLI plugin 바이너리를 다운로드하도록 변경했습니다.

```bash
sudo mkdir -p /usr/local/lib/docker/cli-plugins
sudo curl -fsSL "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64" \
  -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose
```

## 7. `curl-minimal`과 `curl` 충돌

### 에러

```text
Problem: problem with installed package curl-minimal-...
package curl-minimal-... conflicts with curl provided by curl-...
Error: Process completed with exit code 1.
```

### 원인

Amazon Linux 2023에는 `curl-minimal`이 이미 설치되어 있었습니다.

워크플로우에서 추가로 `curl` 패키지를 설치하려고 하면서 패키지 충돌이 발생했습니다.

### 해결

`curl` 명령이 이미 존재하면 패키지 설치를 건너뛰도록 변경했습니다.

`curl`이 없는 경우에만 OS별로 설치합니다.

```bash
if ! command -v curl >/dev/null 2>&1; then
  if command -v dnf >/dev/null 2>&1; then
    sudo dnf install -y curl-minimal
  elif command -v apt-get >/dev/null 2>&1; then
    sudo apt-get update
    sudo apt-get install -y curl
  fi
fi
```

## 8. `compose build requires buildx 0.17.0 or later`

### 에러

```text
Image dropdeal-be-backend Building
compose build requires buildx 0.17.0 or later
Error: Process completed with exit code 1.
```

### 원인

Docker Compose는 설치되었지만, 백엔드 이미지를 빌드할 때 필요한 Docker Buildx가 없거나 버전이 낮았습니다.

`redis`, `postgres` 이미지는 pull에 성공했고, `backend` 이미지를 빌드하는 단계에서 실패했습니다.

### 해결

GitHub Actions 배포 스크립트에서 최신 Docker Buildx plugin을 설치하도록 추가했습니다.

설치 경로:

```text
/usr/local/lib/docker/cli-plugins/docker-buildx
```

설치 후 확인:

```bash
docker buildx version
```

이후 `docker compose up -d --build`가 정상 실행되었습니다.

## 최종 상태

최종적으로 GitHub Actions 배포가 성공했습니다.

배포 흐름:

```text
push to main
-> GitHub Actions test
-> EC2 SSH 접속
-> git fetch / checkout / pull
-> Docker Compose plugin 확인 및 설치
-> Docker Buildx plugin 확인 및 설치
-> docker compose up -d --build
-> deploy success
```

최종 확인 명령:

```bash
cd /home/ec2-user/apps/dropdeal-be
docker compose ps
docker compose logs -f backend
curl http://localhost:8080/actuator/health
```

## 9. `api.dropdealkr.com` DNS 및 HTTPS 연결 오류

### 증상

- `api.dropdealkr.com` 접속 시 EC2가 아니라 Vercel의 `DEPLOYMENT_NOT_FOUND`가 표시됨
- DNS 수정 후 HTTP는 동작했지만 HTTPS는 `ERR_CONNECTION_REFUSED` 발생

### 원인

1. 도메인의 실제 네임서버가 Cloudflare가 아닌 Vercel DNS라서 Cloudflare의 A 레코드가 적용되지 않았습니다.
2. EC2의 Docker Nginx가 80 포트만 사용하고 있었고, 443 포트와 SSL 인증서 설정이 없었습니다.

네임서버 확인:

```bash
nslookup -type=ns dropdealkr.com
```

### 해결

1. Vercel DNS에 `api` A 레코드를 추가해 EC2의 Elastic IP로 연결했습니다.
2. Docker Nginx에 443 포트와 Certbot 인증서 볼륨을 추가했습니다.
3. Certbot의 webroot 방식으로 `api.dropdealkr.com` 인증서를 발급했습니다.
4. HTTP 요청은 HTTPS로 리다이렉트하고, HTTPS 요청은 `backend:8080`으로 프록시하도록 Nginx를 설정했습니다.

확인:

```bash
nslookup api.dropdealkr.com 8.8.8.8
curl -I https://api.dropdealkr.com/swagger-ui/index.html
sudo docker exec dropdeal-nginx nginx -t
```

운영 보안 그룹은 22, 80, 443 포트만 필요한 범위에서 공개하고, 8080, 5432, 6379 포트는 외부 공개를 피합니다. Let's Encrypt 인증서는 만료 전에 `certbot renew` 실행과 Nginx 재시작이 필요합니다.

## 10. 배포 중 SSH `Broken pipe` 및 exit code 255

### 에러

```text
client_loop: send disconnect: Broken pipe
Error: Process completed with exit code 255.
```

### 원인

기존 배포는 GitHub Actions가 EC2에 SSH로 접속한 뒤 `docker compose up -d --build`를 실행했습니다. 이 과정에서 t3.micro EC2가 운영 컨테이너와 Gradle/Docker 빌드를 함께 처리했고, Gradle 빌드 중 SSH 연결이 장시간 응답 없이 끊어졌습니다.

### 해결

- GitHub Actions runner에서 백엔드 Docker 이미지를 빌드합니다.
- 완성된 이미지를 GitHub Container Registry(GHCR)에 커밋 SHA 태그로 저장합니다.
- EC2에서는 GHCR 이미지를 내려받아 `docker compose up -d --no-build`로 실행만 합니다.
- SSH에 `ServerAliveInterval=30`, `ServerAliveCountMax=20`을 적용합니다.
- `ssh-keyscan`에 30초 타임아웃과 최대 5회 재시도를 적용합니다.
- GitHub Actions의 Docker 레이어 캐시를 사용해 재빌드 시간을 줄입니다.

현재 배포 흐름:

```text
push to main
-> GitHub Actions test
-> GitHub Actions에서 Docker 이미지 빌드
-> GHCR에 이미지 push
-> EC2 SSH 접속
-> GHCR에서 이미지 pull
-> docker compose up -d --no-build
-> deploy success
```

EC2에서 Gradle 빌드를 하지 않으므로 t3.micro의 메모리 사용량과 배포 중 SSH 단절 가능성이 줄어듭니다.

## 11. SSH 배너 타임아웃과 `MaxStartups` throttling

### 증상

GitHub Actions의 `ssh-keyscan`이 exit code 1로 실패했고, 로컬 SSH와 EC2 Instance Connect도 연결 단계에서 장시간 멈췄습니다.

```text
Connection established.
Connection timed out during banner exchange
```

TCP 22번 포트 연결 자체는 성공했지만 EC2의 SSH 서버가 호스트 키와 SSH 배너를 제때 반환하지 못했습니다. 따라서 이 장애는 `.pem` 개인키 인증 이전에 발생했으며, 키 파일 유무가 직접적인 원인은 아니었습니다.

### 점검 결과

- EC2 시스템, 인스턴스, EBS 상태 검사는 모두 통과했습니다.
- 최근 CPU 사용률은 최대 약 14%로 CPU 고갈 상태가 아니었습니다.
- 루트 볼륨은 8 GiB 중 5.6 GiB를 사용해 사용률이 71%였으며, 디스크가 가득 찬 상태는 아니었습니다.
- 메모리는 총 916 MiB, 사용 가능 약 280 MiB였고 swap은 없었지만 이전 부팅의 OOM 기록은 없었습니다.
- 일반 재부팅 후에도 증상이 유지됐고, 인스턴스를 중지한 뒤 다시 시작하자 SSH 연결이 복구됐습니다.

복구 후 이전 부팅의 `sshd` 로그에서 다음 기록을 확인했습니다.

```text
Invalid user ubuntu from 71.206.190.209
Invalid user pi from 71.206.190.209
Invalid user baikal from 71.206.190.209
error: beginning MaxStartups throttling
```

### 원인

보안 그룹의 SSH TCP 22번 포트가 `0.0.0.0/0`으로 공개되어 있었습니다. 인터넷의 자동화된 SSH 스캐너가 존재하지 않는 여러 사용자 이름으로 인증을 반복하면서 미인증 동시 연결 수가 증가했고, `sshd`의 `MaxStartups` 제한이 작동했습니다.

이 제한은 공격 연결뿐 아니라 정상적인 `ssh`, `ssh-keyscan`, EC2 Instance Connect 연결도 지연하거나 거절할 수 있습니다. 이번 장애의 직접 원인은 디스크, CPU 또는 `.pem` 키가 아니라 공개된 SSH 포트에 대한 반복 접속과 그로 인한 `MaxStartups` throttling이었습니다.

### 22번 포트를 내 IP로 제한한 이유

보안 그룹의 22번 규칙을 `0.0.0.0/0`에서 현재 관리자의 공인 IP `/32`로 변경했습니다. `/32`는 해당 IP 한 개만 허용하므로 불특정 인터넷 사용자가 SSH 포트에 접근할 수 없습니다.

```text
SSH / TCP / 22 / 관리자 공인 IP/32
```

80과 443은 웹 서비스 제공을 위해 공개 상태를 유지할 수 있지만, SSH는 관리와 배포 주체에만 허용해야 합니다. 8080도 Nginx를 통해서만 서비스한다면 외부 공개를 제거하는 것이 좋습니다.

### GitHub Actions에 IAM 정책이 필요한 이유

GitHub-hosted runner인 `ubuntu-latest`의 공인 IP는 실행할 때마다 달라질 수 있습니다. 따라서 보안 그룹을 관리자 IP로만 제한하면 수동 SSH는 안전해지지만 GitHub Actions의 직접 SSH 배포는 차단됩니다.

이를 해결하기 위해 GitHub Actions가 실행되는 짧은 시간 동안에만 현재 runner IP를 `/32` 규칙으로 추가하고, 배포가 끝나면 제거하도록 구성했습니다. 이 작업에는 EC2 보안 그룹 규칙을 추가하고 제거할 AWS 권한이 필요합니다.

장기 AWS Access Key를 GitHub Secret에 저장하지 않고 OIDC 임시 자격 증명을 사용하도록 다음 리소스를 구성했습니다.

- OIDC 공급자: `token.actions.githubusercontent.com`
- Audience: `sts.amazonaws.com`
- IAM 역할: `GitHubActionsDropDealDeploy`
- 허용 저장소와 브랜치: `Leeka99/Drop-Deal-BE`의 `main`
- 권한 정책: `GitHubActionsTemporarySshIngress`
- 대상 보안 그룹: `sg-0f70c83f2bfb428d5`
- 허용 작업: `ec2:AuthorizeSecurityGroupIngress`, `ec2:RevokeSecurityGroupIngress`

전용 정책은 특정 보안 그룹의 규칙 추가와 제거만 허용합니다. `AdministratorAccess`나 `AmazonEC2FullAccess`를 사용하지 않아 GitHub Actions 자격 증명이 탈취되더라도 다른 AWS 리소스에 미치는 영향을 줄였습니다.

### GitHub Actions 수정 내용

`deploy` job에만 다음 OIDC 권한을 추가했습니다.

```yaml
permissions:
  contents: read
  packages: write
  id-token: write
```

배포 과정은 다음과 같이 변경됐습니다.

```text
GitHub OIDC 토큰 발급
-> GitHubActionsDropDealDeploy 역할의 임시 AWS 자격 증명 획득
-> GitHub runner의 현재 공인 IPv4 확인
-> 해당 IP/32를 보안 그룹 22번 포트에 임시 추가
-> ssh-keyscan 및 EC2 SSH 배포 실행
-> 성공 또는 실패와 관계없이 임시 22번 규칙 제거
```

구체적으로 `aws-actions/configure-aws-credentials@v6`으로 IAM 역할을 맡고, `https://checkip.amazonaws.com`에서 runner IP를 구한 뒤 IPv4 형식을 검증합니다. 이후 AWS CLI로 SSH 인바운드 규칙을 추가합니다.

정리 단계에는 `always()` 조건을 적용해 SSH 배포나 중간 단계가 실패해도, 규칙 추가에 성공한 경우 임시 runner 규칙을 제거하도록 했습니다. 관리자의 `/32` 규칙은 별도 규칙이므로 제거되지 않습니다.

### 최종 보안 및 배포 구조

```text
관리자 수동 SSH: 관리자 공인 IP/32에서 항상 허용
GitHub Actions SSH: 배포 중 runner 공인 IP/32만 임시 허용
인터넷 SSH 스캐너: 보안 그룹에서 차단
AWS 인증: 저장된 Access Key 대신 GitHub OIDC 임시 자격 증명 사용
IAM 권한: 특정 보안 그룹의 규칙 추가와 제거로 제한
```

이 구성으로 SSH 포트를 인터넷 전체에 상시 공개하지 않으면서 GitHub-hosted runner의 유동 IP에서도 자동 배포할 수 있습니다.
