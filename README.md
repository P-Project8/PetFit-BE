# PetFit Backend

반려동물 의류 쇼핑몰 백엔드 서비스입니다.

## 프로젝트 개요

PetFit은 반려동물 의류를 검색, 구매하고 AI 가상 피팅을 체험할 수 있는 서비스입니다.

## 기술 스택

- **Framework**: Spring Boot 3.x, Java 17
- **Database**: PostgreSQL
- **Authentication**: JWT (Access + Refresh Token)
- **AI**: Google Gemini API
- **API Docs**: Swagger (SpringDoc OpenAPI)
- **Build**: Gradle

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
| **AI 스타일링** | 반려동물 사진 + 의류 AI 합성 (Gemini) |
| **마이페이지** | 프로필, 주문 내역, 리뷰/찜 수 통합 조회 |

## 로컬 환경 설정

### 사전 요구사항

- Java 17 이상
- PostgreSQL
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

GEMINI_API_KEY: {Google Gemini API 키}
```

**Gmail 앱 비밀번호 발급:**
1. Google 계정 > 2단계 인증 활성화
2. [앱 비밀번호 생성](https://myaccount.google.com/apppasswords)
3. 생성된 16자리 비밀번호를 `password`에 입력

**Gemini API 키 발급:**
1. [Google AI Studio](https://aistudio.google.com/apikey)에서 API 키 생성
2. Google Cloud Console에서 Generative Language API 활성화

### 4. 실행

```bash
./gradlew bootRun
```

첫 실행 시 JPA가 자동으로 테이블을 생성하고, `data-seed.sql`로 초기 데이터가 삽입됩니다.
- 카테고리 7개, 상품 180개, 상품 옵션 1,740개, 리뷰 84개

### 5. 확인

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **프론트엔드 연동**: http://localhost:5173 (프론트 dev 서버)

## API 엔드포인트

자세한 API 명세는 [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md)를 참고하세요.

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
| AI | `/api/ai` | 스타일링 |
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
├── ai/             # AI 스타일링 (Gemini)
├── mypage/         # 마이페이지
└── global/         # 공통 (보안, 예외, 설정, Swagger)
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

## 보안

### 인증 (JWT)

- **Access Token** (1시간): API 요청 인증에 사용. 짧은 만료시간으로 탈취 시 피해 최소화
- **Refresh Token** (7일): Access Token 재발급용. DB에 저장하여 서버 측 폐기 가능
- **Token Blacklist**: 로그아웃 시 Access Token을 블랙리스트에 등록하여 즉시 무효화
- **Token Whitelist**: 인메모리 캐시로 자주 사용되는 토큰의 검증 성능 최적화

### 비밀번호

- BCrypt 해싱 알고리즘 적용 (평문 저장하지 않음)
- 비밀번호 규칙 검증 (최소 길이, 영문/숫자/특수문자 조합)

### 이메일 인증

- 6자리 랜덤 인증코드 발송 (Google SMTP)
- 인증코드 5분 후 자동 만료
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
