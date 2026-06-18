# DropDeal Backend

DropDeal 서비스의 Spring Boot 백엔드 프로젝트입니다.

프론트엔드 저장소 `Leeka99/Drop-Deal-FE`의 `api` 모드에서 호출하는 `/api/v1` 계약에 맞춰 상품, 주문, 프로필, 판매자 정산 API를 제공합니다. 현재 단계는 프론트 연동과 API 구조 검증을 위한 보일러플레이트이며, 데이터는 인메모리 샘플 데이터로 동작합니다.

## 기술 스택

- Java 21
- Spring Boot 3.5
- Gradle Wrapper
- Spring Web
- Spring Validation
- Spring Data JPA
- H2 Database
- Spring Boot Actuator
- Springdoc OpenAPI Swagger UI
- Spring Boot DevTools

## 프로젝트 구조

```text
src/main/java/com/dropdeal/api
├─ common      # 공통 응답, 에러 응답, 전역 예외 처리
├─ config      # CORS, OpenAPI 설정
├─ product     # 상품 조회 API
├─ order       # 회원/비회원 주문 API
├─ profile     # 기본 배송지 API
├─ settlement  # 판매자 정산 API
└─ DropdealBeApplication.java
```

## 실행 방법

Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

macOS/Linux:

```bash
./gradlew bootRun
```

기본 서버 주소:

```text
http://localhost:8080
```

## 운영 환경 변수

`prod` 프로필은 PostgreSQL RDS에 연결합니다. 민감 정보는 파일에 저장하지 않고 환경변수로 주입합니다.

```env
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://dropdeal-db-instance-1.c1qesycswhwe.ap-northeast-2.rds.amazonaws.com:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=your-password
CORS_ALLOWED_ORIGINS=https://dropdealkr.com
SWAGGER_ENABLED=false
```

운영 실행 예시:

```powershell
$env:SPRING_PROFILES_ACTIVE="prod"
$env:DB_URL="jdbc:postgresql://dropdeal-db-instance-1.c1qesycswhwe.ap-northeast-2.rds.amazonaws.com:5432/postgres"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your-password"
$env:CORS_ALLOWED_ORIGINS="https://dropdealkr.com"
.\gradlew.bat bootRun
```

## Swagger

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/api-docs
```

## 프론트엔드 연동

프론트엔드 `.env.local` 예시:

```env
NEXT_PUBLIC_API_MODE=api
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

백엔드는 기본적으로 다음 프론트 개발 서버 주소의 CORS 요청을 허용합니다.

```text
http://localhost:3000
http://127.0.0.1:3000
```

## 응답 규칙

모든 성공 응답은 프론트 서비스 코드에 맞춰 `data` 필드로 감쌉니다.

```json
{
  "data": {}
}
```

에러 응답은 `message`, `status`, `timestamp`를 반환합니다.

```json
{
  "message": "Product not found: 999",
  "status": 404,
  "timestamp": "2026-06-17T03:00:00Z"
}
```

## 구현된 API

### 상품

- `GET /api/v1/products`
- `GET /api/v1/products?type=CLEARANCE`
- `GET /api/v1/products?type=FREE_GIVEAWAY`
- `GET /api/v1/products/{id}`

### 주문

- `GET /api/v1/me/orders`
- `POST /api/v1/orders`
- `POST /api/v1/orders/{orderId}/cancel`
- `POST /api/v1/guest-orders/verify`
- `GET /api/v1/guest-orders`
- `POST /api/v1/guest-orders/{orderId}/cancel`

### 프로필

- `GET /api/v1/me/profile`
- `PUT /api/v1/me/profile`

### 판매자 정산

- `GET /api/v1/seller/settlements`
- `GET /api/v1/seller/settlements/{id}`

## 개발 편의 기능

DevTools가 포함되어 있어 코드 변경 후 컴파일이 발생하면 애플리케이션이 자동 재시작됩니다.

IntelliJ에서 자동 재시작을 사용하려면 다음 설정을 켭니다.

- `Settings > Build, Execution, Deployment > Compiler > Build project automatically`
- `Settings > Advanced Settings > Compiler > Allow auto-make to start even if developed application is currently running`

## 빌드와 테스트

```powershell
.\gradlew.bat test
.\gradlew.bat build
```

## Docker

Docker Compose 실행:

```powershell
docker compose up --build
```

백그라운드 실행:

```powershell
docker compose up -d --build
```

종료:

```powershell
docker compose down
```

데이터 볼륨까지 삭제:

```powershell
docker compose down -v
```

이미지 빌드:

```powershell
docker build -t dropdeal-be:local .
```

로컬 프로필 실행:

```powershell
docker run --rm -p 8080:8080 dropdeal-be:local
```

운영 프로필 실행:

```powershell
docker run --rm -p 8080:8080 `
  -e SPRING_PROFILES_ACTIVE=prod `
  -e DB_URL="jdbc:postgresql://dropdeal-db-instance-1.c1qesycswhwe.ap-northeast-2.rds.amazonaws.com:5432/postgres" `
  -e DB_USERNAME="postgres" `
  -e DB_PASSWORD="your-password" `
  -e CORS_ALLOWED_ORIGINS="https://dropdealkr.com" `
  dropdeal-be:local
```

## Git 제외 대상

다음 파일은 커밋하지 않습니다.

- Gradle 캐시와 빌드 결과: `.gradle/`, `build/`
- IDE 개인 설정: `.idea/`, `*.iml`
- 실행 로그: `*.log`, `logs/`
- 로컬 환경변수: `.env`, `.env.*`
- 로컬 DB/파일 저장소: `*.db`, `*.mv.db`, `uploads/`, `storage/`

## 향후 구현 예정

- 회원 인증과 권한 관리
- 상품/주문/정산 JPA 엔티티와 영속화
- 결제 승인, 취소, 부분 환불
- 판매자 정산 원장과 수수료 정책
- 운영 DB 프로필 분리
- API 테스트와 통합 테스트 확대
