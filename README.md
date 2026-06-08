# PetFit Backend

반려동물 의류 쇼핑몰 백엔드 서비스입니다.

## 프로젝트 개요

PetFit은 반려동물 의류를 검색, 구매하고 AI 가상 피팅을 체험할 수 있는 서비스입니다.

## 기술 스택

- **Framework**: Spring Boot 3.5.5, Java 17
- **Database**: PostgreSQL 15
- **Cache**: Redis 7
- **Storage**: AWS S3 (ap-northeast-2)
- **Security**: Spring Security, JWT (JJWT)
- **Email**: Spring Mail (Google SMTP)
- **AI**: Google Gemini 2.5 Flash Image API
- **Scheduler**: Spring `@Scheduled` (구독 만료 일배치)
- **API Docs**: Swagger (SpringDoc OpenAPI)
- **Build**: Gradle
- **Container**: Docker Compose
- **Deploy**: AWS EC2 + Nginx Reverse Proxy

## 주요 기능

| 도메인 | 기능 |
|--------|------|
| **인증** | 회원가입, 로그인, 로그아웃, 토큰 재발급, 이메일 인증 |
| **상품** | 목록/상세 조회, 검색, 카테고리 필터, 가격 필터, 인기순 정렬 |
| **카테고리** | 계층 구조 카테고리 목록 조회 |
| **장바구니** | 추가, 조회, 수량 변경, 삭제 |
| **찜** | 찜 추가/토글, 찜 목록 조회, 상품별 찜 수 |
| **주문** | 장바구니 기반 주문 생성, 주문 내역 조회, 주문 취소 |
| **리뷰** | 리뷰 작성/수정/삭제, 상품별 리뷰 조회, 평균 평점 |
| **반려견 프로필** | 견종/체형(목·가슴·등) 등록, 사이즈 추천(XXS~5XL), 유사 체형 큐레이션 |
| **AI 스타일링** | 반려동물 사진 + 의류 AI 합성 (Gemini), 체형 데이터 기반 프롬프트 강화 |
| **Pet-Gallery 커뮤니티** | 스타일링 공유 피드, 좋아요/댓글, 인기 게시물 알고리즘, 상품 태그 |
| **구독 & 크레딧** | FREE(월 3회)/PREMIUM(무제한) 플랜, 업그레이드/취소, 만료 자동 처리 |
| **파일 (S3)** | base64/멀티파트 업로드, 폴더별 관리, 결과 영구 저장 |
| **마이페이지** | 프로필, 주문 내역, 리뷰/찜 수 통합 조회 |

## 로컬 환경 설정

### 사전 요구사항

- Java 17 이상
- PostgreSQL
- Docker Desktop (Redis 실행용)
- Git

### 1. 클론

```bash
git clone https://github.com/P-Project8/PetFit-BE.git
cd PetFit-BE
git checkout develop
```

### 2. 데이터베이스 생성

```sql
CREATE DATABASE petfit;
```

### 3. application-secret.yml 생성

`src/main/resources/application-secret.yml` 파일을 생성합니다.

이 파일은 `.gitignore`에 포함되어 있어 GitHub에 올라가지 않습니다.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/petfit
    username: postgres
    password: {본인 DB 비밀번호}
    driver-class-name: org.postgresql.Driver
  data:
    redis:
      host: localhost
      port: 6379
  mail:
    host: smtp.gmail.com
    port: 587
    username: {Gmail 주소}
    password: {Gmail 앱 비밀번호}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

jwt:
  key: petfitprojectsecretkey1234567890abcdefghijklmnopqrstuvwxyz
  access:
    expiration: 3600000      # 1시간
  refresh:
    expiration: 604800000    # 7일

email:
  from: {Gmail 주소}

# Google Gemini AI
GEMINI_API_KEY: {Google Gemini API 키}

# AWS S3 (스타일링 결과 / 펫 이미지 저장)
cloud:
  aws:
    credentials:
      access-key: {IAM Access Key}
      secret-key: {IAM Secret Key}
    s3:
      bucket: {버킷 이름}
    region:
      static: ap-northeast-2
```

**Gmail 앱 비밀번호 발급:**
1. Google 계정 > 2단계 인증 활성화
2. [앱 비밀번호 생성](https://myaccount.google.com/apppasswords)
3. 생성된 16자리 비밀번호를 `password`에 입력

**Gemini API 키 발급:**
1. [Google AI Studio](https://aistudio.google.com/apikey)에서 API 키 생성
2. Google Cloud Console에서 Generative Language API 활성화

### 4. Redis 실행

```bash
docker compose up -d redis
```

### 5. 실행

```bash
./gradlew bootRun
```

첫 실행 시 JPA가 자동으로 테이블을 생성하고, `data-seed.sql`로 초기 데이터가 삽입됩니다.
- 카테고리 7개, 상품 180개, 상품 옵션 1,740개, 리뷰 84개

### 6. 확인

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **프론트엔드 연동**: http://localhost:5173 (프론트 dev 서버)

## API 엔드포인트

자세한 API 명세는 [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md)를 참고하세요.

총 **56개 API** 제공.

| 도메인 | Base URL | 주요 엔드포인트 |
|--------|----------|----------------|
| 인증 | `/api/auth` | signup, login, logout, reissue, profile |
| 이메일 | `/api/email` | verification/send, verification/verify |
| 상품 | `/api/products` | 목록, 상세, 검색, 필터, 인기순, 큐레이션 |
| 카테고리 | `/api/categories` | 목록 (계층 구조) |
| 장바구니 | `/api/cart` | CRUD |
| 찜 | `/api/wishlist` | 추가/삭제, 목록, 찜수 카운트 |
| 주문 | `/api/orders` | 생성, 목록, 상세, 취소 |
| 리뷰 | `/api/reviews` | 작성, 수정, 삭제, 상품별 조회 |
| 반려견 | `/api/pets` | CRUD, 사이즈 추천, 유사 체형 큐레이션 |
| AI | `/api/ai` | 스타일링, 히스토리 |
| 갤러리 | `/api/gallery` | 피드, 인기, 좋아요 토글, 댓글 CRUD |
| 구독 | `/api/subscription` | 조회/업그레이드/취소, 크레딧 현황 |
| 파일 | `/api/files` | S3 업로드 |
| 마이페이지 | `/api/mypage` | 통합 정보 조회 |

## 프로젝트 구조

```
com.PetFit.backend
├── auth/           # 인증/회원 (로그인, 회원가입, JWT, 이메일 인증)
├── product/        # 상품 (조회, 검색, 필터)
├── category/       # 카테고리 (계층 구조)
├── cart/           # 장바구니
├── wishlist/       # 찜 목록
├── order/          # 주문
├── review/         # 리뷰
├── pet/            # 반려견 프로필 (체형, 사이즈 추천, 유사 체형 큐레이션)
├── ai/             # AI 스타일링 (Gemini)
├── gallery/        # Pet-Gallery 커뮤니티 (피드, 좋아요, 댓글)
├── subscription/   # 구독 & 크레딧 (FREE/PREMIUM, 만료 스케줄러)
├── file/           # S3 업로드 공통 모듈
├── mypage/         # 마이페이지
└── global/         # 공통 (보안, 예외, 설정, Swagger, Annotation)
```

각 도메인은 아래 레이어 구조를 따릅니다:
```
{도메인}/
├── application/usecase/      # 비즈니스 시나리오
├── domain/entity/            # 엔티티
├── domain/repository/        # 리포지토리 인터페이스
├── domain/service/           # 도메인 서비스
└── presentation/             # 컨트롤러 + DTO
```

자세한 내용은 [CONTRIBUTING.md](CONTRIBUTING.md)를 참고하세요.

## 설계 결정 (Design Decisions)

기술 선택의 근거와 trade-off는 별도 문서에서 정리합니다:
- **[docs/DESIGN_DECISIONS.md](docs/DESIGN_DECISIONS.md)** — JWT 인증 구조, 인프라 (EC2+S3 단순 아키텍처) 선택 근거, 부하 테스트 결과
- **[load-tests/README.md](load-tests/README.md)** — k6 기반 시나리오별 부하 테스트

## 서비스 고도화 정책

### 1. 사이즈 추천 (`pet` 도메인)
가슴둘레(chestSize) 기준 XXS~5XL 10단계 자동 매핑.

| 사이즈 | 가슴 둘레 (cm) |
|--------|---------------|
| XXS | 20 ~ 26 |
| XS | 26 ~ 32 |
| S | 32 ~ 38 |
| M | 38 ~ 45 |
| L | 45 ~ 53 |
| XL | 53 ~ 60 |
| XXL | 60 ~ 68 |
| XXXL | 68 ~ 78 |
| 4XL | 78 ~ 88 |
| 5XL | 88 ~ 100 |

`GET /api/pets/{petId}/size-recommendation` — 펫 체형 + 상품 옵션 매칭.

### 2. 유사 체형 큐레이션 (`pet` 도메인)
- 가슴둘레 ±20% 범위의 사용자들 식별
- 해당 사용자들의 주문 내역 집계 → 인기 상품 Top 10
- 각 상품별 구매 인원 / 인기도 % 계산
- `GET /api/pets/{petId}/similar-products`

### 3. 인기 게시물 알고리즘 (`gallery` 도메인)
```
인기 점수 = likeCount × 2 + commentCount + (최근 7일 이내 작성 시 +5)
```
`GET /api/gallery/popular` — 점수 내림차순 정렬.

### 4. AI 크레딧 시스템 (`subscription` 도메인)
- **FREE 플랜**: 월 3회 (`AiStyling` 카운트 기반, 실패는 미차감)
- **PREMIUM 플랜**: 무제한, 30일 유효
- AI 호출 직전 `CreditService.assertCanConsume()` 가드
- 한도 초과 시 `402 PAYMENT_REQUIRED` + 에러 코드 `SUB004`
- 만료 스케줄러: 매일 KST 01:00 PREMIUM → EXPIRED + FREE 자동 재발급

## 보안

### 인증 (JWT)

- **Access Token** (1시간): API 요청 인증에 사용. 짧은 만료시간으로 탈취 시 피해 최소화
- **Refresh Token** (7일): Access Token 재발급용. DB에 저장하여 서버 측 폐기 가능
- **Token Blacklist**: 로그아웃 시 Access Token을 블랙리스트에 등록하여 즉시 무효화
- **Token Whitelist**: Redis 캐시로 자주 사용되는 토큰의 검증 성능 최적화

### 비밀번호

- BCrypt 해싱 알고리즘 적용 (평문 저장하지 않음)
- 비밀번호 규칙 검증 (최소 길이, 영문/숫자/특수문자 조합)

### 이메일 인증

- 6자리 랜덤 인증코드 발송 (Google SMTP)
- Redis TTL을 통한 인증코드 5분 후 자동 만료
- 최대 시도 횟수 제한 (5회)으로 무차별 대입 방지

### API 보안

- Spring Security 필터 체인으로 모든 요청 검증
- 인증이 필요 없는 공개 API 경로만 화이트리스트로 관리
- CORS 설정으로 허용된 Origin만 접근 가능
- Soft Delete 적용으로 데이터 영구 삭제 방지

## 기여

코드 컨벤션과 개발 가이드는 [CONTRIBUTING.md](CONTRIBUTING.md)를 참고하세요.

## 팀

PetFit Development Team (P-Project8)
