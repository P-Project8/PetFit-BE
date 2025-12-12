# PetFit Service

PetFit 백엔드 서비스입니다. 

## 📋 프로젝트 개요

PetFit은 반려동물 의류 쇼핑몰 서비스로, 사용자가 반려동물에게 맞는 의류를 선택하고 AI 스타일링 기능을 통해 실제 입혀본 느낌을 경험할 수 있는 서비스입니다.

이 인증 서비스는 PetFit의 핵심 기능인 회원가입, 로그인, 이메일 인증, JWT 토큰 관리 등을 담당합니다.

## 🚀 주요 기능

### 회원가입 (2단계 절차)

**1단계: 이메일 인증**
- 사용자가 이메일을 입력하면 Google SMTP를 통해 6자리 인증코드 전송
- 인증코드는 Redis에 TTL을 두고 저장하여 5분 후 자동 삭제
- Rate Limiting 적용 (일일 최대 5회, 쿨다운 60초)

**2단계: 계정 생성**
- 이메일 인증 여부 확인
- 비밀번호 규칙 검증
- 닉네임 중복 체크
- 가입 완료 후 자동 로그인 및 JWT 발급

### 로그인

- 이메일 + 비밀번호 기반 인증
- Access Token (15분)과 Refresh Token (14일) 발급
- Redis 기반 토큰 블랙리스트 시스템
- 로그아웃 및 비밀번호 변경 시 토큰 즉시 무효화

### 프로필 관리

- 사용자 프로필 조회
- 프로필 정보 수정 (이름, 생년월일, 비밀번호)

### 토큰 관리

- Access Token 재발급 (Refresh Token 사용)
- 토큰 블랙리스트/화이트리스트 관리
- JWT 기반 인증 필터

### 메인 화면

- 최신 문구와 배너로 사용자 유입 유도
- 큐레이션 섹션 제공
  - **New**: 신상품 섹션
  - **Hot**: 인기 상품 섹션
  - **Sale**: 할인 상품 섹션
- AI 스타일링 기능으로 자연스러운 유입 동선 설계
- 모바일 환경 고려 (스와이프 제스처, 애니메이션)

### 카테고리/쇼핑 화면

- 상단 탭으로 카테고리 빠른 전환 (전체, 아우터, 상의 등)
- 정렬 옵션 제공
  - 최신순
  - 인기순
  - 낮은 가격순
  - 높은 가격순
- 하단 페이지네이션으로 효율적인 상품 탐색
- 최소 클릭으로 원하는 상품군 탐색 가능

### 상품 상세

- 상품 이미지, 가격, 설명, 옵션 정보 제공
- 평균 평점과 리뷰 개수 동적 표시
- AI 스타일링 버튼 제공 (반려동물에게 입혀보기)
- Zustand를 활용한 전역 상태 관리
  - 찜하기
  - 장바구니 추가
  - 옵션 선택

### 찜 목록 / 장바구니

- **찜 목록**: 사용자의 관심 상품을 모아보는 개인화 공간
- **장바구니**: 구매 직전 단계에서 상품 비교 및 수량 조정
- 상태 관리를 통한 부드러운 인터랙션
  - 수량 변경
  - 삭제 (Confirm 모달로 실수 방지)

### AI 스타일링 화면

- 반려동물 사진 업로드
- 함께 스타일링할 의류 상품 선택
- Gemini API를 통한 이미지 합성
- 결과 확인 및 부가 기능
  - 다시하기
  - 저장
  - 공유
  - 상품 상세 이동
  - 장바구니 이동

### 마이페이지

- 사용자 정보 관리
- 주문 내역 조회
- 리뷰 관리
- 로그아웃 및 계정 관리 기능
- 탭 구조로 개인화된 정보 빠른 탐색

## 🛠 기술 스택

- **Backend**: Spring Boot 3.5.5, Java 17
- **Database**: PostgreSQL
- **Cache**: Redis
- **Security**: Spring Security, JWT (JJWT)
- **Email**: Spring Mail (Google SMTP)
- **API Documentation**: Swagger (SpringDoc OpenAPI)
- **Build Tool**: Gradle
- **Container**: Docker

## 📁 프로젝트 구조

```
com.PetFit.backend
├── auth/
│   └── domain/
│       ├── auth/                              # 인증 도메인
│       │   ├── application/
│       │   │   ├── dto/
│       │   │   │   ├── request/              # 요청 DTO
│       │   │   │   │   ├── LoginRequest.java
│       │   │   │   │   ├── SignUpRequest.java
│       │   │   │   │   ├── TokenReissueRequest.java
│       │   │   │   │   └── UpdateProfileRequest.java
│       │   │   │   └── response/            # 응답 DTO
│       │   │   │       ├── LoginResponse.java
│       │   │   │       ├── ProfileResponse.java
│       │   │   │       └── TokenReissueResponse.java
│       │   │   └── usecase/                 # 사용자 유스케이스
│       │   │       ├── UserAuthUseCase.java
│       │   │       ├── UserProfileUseCase.java
│       │   │       ├── TokenReissueUseCase.java
│       │   │       └── UpdateProfileUseCase.java
│       │   ├── domain/
│       │   │   ├── entity/                  # 엔티티
│       │   │   │   └── User.java
│       │   │   ├── repository/              # 리포지토리
│       │   │   │   └── UserRepository.java
│       │   │   └── service/                 # 도메인 서비스
│       │   │       ├── UserService.java
│       │   │       ├── RefreshTokenService.java
│       │   │       ├── TokenBlacklistService.java
│       │   │       ├── TokenWhitelistService.java
│       │   │       └── TokenReissueService.java
│       │   └── ui/                          # 컨트롤러
│       │       ├── AuthController.java
│       │       └── UserController.java
│       └── email/                           # 이메일 도메인
│           ├── application/
│           │   ├── dto/
│           │   │   ├── request/
│           │   │   │   ├── SendVerificationRequest.java
│           │   │   │   └── VerifyEmailRequest.java
│           │   │   └── response/
│           │   │       └── EmailVerificationResponse.java
│           │   └── usecase/
│           │       ├── SendEmailVerificationUseCase.java
│           │       └── VerifyEmailUseCase.java
│           ├── domain/
│           │   └── service/
│           │       ├── EmailService.java
│           │       └── EmailVerificationService.java
│           └── ui/
│               └── EmailController.java
│
├── product/                                 # 상품 도메인
│   ├── application/
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   │   ├── ProductSearchRequest.java
│   │   │   │   └── ProductFilterRequest.java
│   │   │   └── response/
│   │   │       ├── ProductResponse.java
│   │   │       ├── ProductListResponse.java
│   │   │       └── ProductDetailResponse.java
│   │   └── usecase/
│   │       ├── GetProductUseCase.java
│   │       ├── GetProductListUseCase.java
│   │       ├── GetProductDetailUseCase.java
│   │       └── GetCuratedProductsUseCase.java
│   ├── domain/
│   │   ├── entity/
│   │   │   ├── Product.java
│   │   │   ├── ProductOption.java
│   │   │   └── ProductImage.java
│   │   ├── repository/
│   │   │   ├── ProductRepository.java
│   │   │   └── ProductOptionRepository.java
│   │   └── service/
│   │       └── ProductService.java
│   └── ui/
│       └── ProductController.java
│
├── category/                                # 카테고리 도메인
│   ├── application/
│   │   ├── dto/
│   │   │   └── response/
│   │   │       └── CategoryResponse.java
│   │   └── usecase/
│   │       └── GetCategoryListUseCase.java
│   ├── domain/
│   │   ├── entity/
│   │   │   └── Category.java
│   │   ├── repository/
│   │   │   └── CategoryRepository.java
│   │   └── service/
│   │       └── CategoryService.java
│   └── ui/
│       └── CategoryController.java
│
├── cart/                                    # 장바구니 도메인
│   ├── application/
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   │   ├── AddCartRequest.java
│   │   │   │   └── UpdateCartRequest.java
│   │   │   └── response/
│   │   │       └── CartResponse.java
│   │   └── usecase/
│   │       ├── AddCartUseCase.java
│   │       ├── GetCartUseCase.java
│   │       ├── UpdateCartUseCase.java
│   │       └── DeleteCartUseCase.java
│   ├── domain/
│   │   ├── entity/
│   │   │   └── Cart.java
│   │   ├── repository/
│   │   │   └── CartRepository.java
│   │   └── service/
│   │       └── CartService.java
│   └── ui/
│       └── CartController.java
│
├── wishlist/                                # 찜 목록 도메인
│   ├── application/
│   │   ├── dto/
│   │   │   └── response/
│   │   │       └── WishlistResponse.java
│   │   └── usecase/
│   │       ├── AddWishlistUseCase.java
│   │       ├── GetWishlistUseCase.java
│   │       └── DeleteWishlistUseCase.java
│   ├── domain/
│   │   ├── entity/
│   │   │   └── Wishlist.java
│   │   ├── repository/
│   │   │   └── WishlistRepository.java
│   │   └── service/
│   │       └── WishlistService.java
│   └── ui/
│       └── WishlistController.java
│
├── order/                                   # 주문 도메인
│   ├── application/
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   │   └── CreateOrderRequest.java
│   │   │   └── response/
│   │   │       ├── OrderResponse.java
│   │   │       └── OrderListResponse.java
│   │   └── usecase/
│   │       ├── CreateOrderUseCase.java
│   │       └── GetOrderListUseCase.java
│   ├── domain/
│   │   ├── entity/
│   │   │   ├── Order.java
│   │   │   └── OrderItem.java
│   │   ├── repository/
│   │   │   └── OrderRepository.java
│   │   └── service/
│   │       └── OrderService.java
│   └── ui/
│       └── OrderController.java
│
├── review/                                  # 리뷰 도메인
│   ├── application/
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   │   ├── CreateReviewRequest.java
│   │   │   │   └── UpdateReviewRequest.java
│   │   │   └── response/
│   │   │       ├── ReviewResponse.java
│   │   │       └── ReviewListResponse.java
│   │   └── usecase/
│   │       ├── CreateReviewUseCase.java
│   │       ├── GetReviewListUseCase.java
│   │       └── UpdateReviewUseCase.java
│   ├── domain/
│   │   ├── entity/
│   │   │   └── Review.java
│   │   ├── repository/
│   │   │   └── ReviewRepository.java
│   │   └── service/
│   │       └── ReviewService.java
│   └── ui/
│       └── ReviewController.java
│
├── ai/                                      # AI 스타일링 도메인
│   ├── application/
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   │   └── StyleRequest.java
│   │   │   └── response/
│   │   │       └── StyleResponse.java
│   │   └── usecase/
│   │       └── StyleImageUseCase.java
│   ├── domain/
│   │   ├── entity/
│   │   │   └── StyledImage.java
│   │   ├── repository/
│   │   │   └── StyledImageRepository.java
│   │   └── service/
│   │       └── GeminiAIService.java
│   └── ui/
│       └── AIController.java
│
└── mypage/                                  # 마이페이지 도메인
    ├── application/
    │   ├── dto/
    │   │   └── response/
    │   │       └── MyPageResponse.java
    │   └── usecase/
    │       └── GetMyPageUseCase.java
    ├── domain/
    │   └── service/
    │       └── MyPageService.java
    └── ui/
        └── MyPageController.java
│
└── global/                                 # 전역 공통 기능
    ├── annotation/                         # 커스텀 어노테이션
    │   ├── CurrentUser.java                # 현재 사용자 주입
    │   └── RefreshToken.java               # 리프레시 토큰 주입
    │
    ├── common/                             # 공통 클래스
    │   ├── BaseEntity.java                 # 기본 엔티티 (생성/수정일시)
    │   └── BaseResponse.java               # 기본 응답 형식
    │
    ├── config/                             # 설정 클래스
    │   ├── properties/
    │   │   └── CorsProperties.java         # CORS 설정
    │   ├── EmailConfig.java                # 이메일 설정
    │   ├── RedisConfig.java                # Redis 설정
    │   ├── RestTemplateConfig.java         # HTTP 클라이언트 설정
    │   ├── SecurityConfig.java             # 보안 설정
    │   ├── SwaggerConfig.java              # API 문서 설정
    │   └── WebMvcConfig.java               # Web MVC 설정
    │
    ├── exception/                          # 예외 처리
    │   ├── code/                           # 에러 코드 정의
    │   │   ├── BaseCode.java               # 기본 에러 코드
    │   │   ├── BaseCodeInterface.java      # 에러 코드 인터페이스
    │   │   └── status/                     # HTTP 상태별 에러 코드
    │   │       ├── AuthErrorStatus.java
    │   │       ├── EmailErrorStatus.java
    │   │       ├── GlobalErrorStatus.java
    │   │       └── SuccessStatus.java
    │   ├── ExceptionAdvice.java            # 전역 예외 처리
    │   └── RestApiException.java           # REST API 예외
    │
    ├── interceptor/                        # 인터셉터
    │   └── JwtBlacklistInterceptor.java    # JWT 블랙리스트 인터셉터
    │
    ├── resolver/                           # Argument 리졸버
    │   ├── CurrentUserArgumentResolver.java    # 현재 사용자 리졸버
    │   └── RefreshTokenArgumentResolver.java   # 리프레시 토큰 리졸버
    │
    ├── security/                           # 보안 관련
    │   ├── ExcludeAuthPathProperties.java      # 인증 제외 경로
    │   ├── ExcludeBlacklistPathProperties.java # 블랙리스트 제외 경로
    │   ├── JwtAuthenticationFilter.java        # JWT 인증 필터
    │   ├── JwtProperties.java                  # JWT 설정
    │   └── TokenProvider.java                  # 토큰 제공자
    │
    ├── swagger/                            # API 문서 어노테이션
    │   ├── AuthApi.java                    # 인증 API 문서
    │   ├── BaseApi.java                    # 기본 API 문서
    │   ├── EmailApi.java                   # 이메일 API 문서
    │   └── UserProfileApi.java             # 사용자 프로필 API 문서
    │
    └── util/                               # 유틸리티
        └── SecureRandomGenerator.java      # 보안 랜덤 생성기
```

## 🔧 환경 설정

### 필수 요구사항

- Java 17+
- Gradle 8+
- PostgreSQL
- Redis
- Docker (선택사항)

### 설정 파일

#### `application.yml`

기본 애플리케이션 설정 파일입니다.

#### `application-secret.yml`

민감한 정보를 포함하는 설정 파일입니다. Git에 커밋되지 않도록 `.gitignore`에 추가되어 있습니다.

**필수 설정 항목:**

```yaml
# PostgreSQL 데이터베이스 설정
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/PetFit_db
    username: postgres
    password: your_password
    driver-class-name: org.postgresql.Driver
  data:
    redis:
      host: localhost
      port: 6379

# JWT 설정
jwt:
  key: your-secret-key-here
  access:
    expiration: 900000       # 15분 (밀리초)
  refresh:
    expiration: 1209600000  # 14일 (밀리초)
  verification-expiration-ms: 900000 # 15분 (밀리초)

# 이메일 설정
email:
  from: your-email@gmail.com
  host: smtp.gmail.com
  port: 587
  username: your-email@gmail.com
  password: your-app-password-here
```

**Gmail 앱 비밀번호 설정:**
1. Google 계정에서 2단계 인증 활성화
2. [앱 비밀번호 생성](https://myaccount.google.com/apppasswords)
3. 생성된 앱 비밀번호를 `email.password`에 입력

## 🚀 실행 방법

### 로컬 개발 환경

```bash
# 의존성 설치
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

### Docker를 사용한 실행

```bash
# Docker 이미지 빌드
docker build -t petfit-service .

# Docker 컨테이너 실행
docker run -p 8080:8080 petfit-service
```

## 🏗️ 인프라 아키텍처

PetFit 서비스는 AWS 클라우드 환경에 배포되어 있으며, 다음과 같은 구조로 구성되어 있습니다.

### 인프라 구성 요소
<img width="977" height="699" alt="스크린샷 2025-12-03 오후 4 34 04" src="https://github.com/user-attachments/assets/7ff29c48-b673-4fc9-ba78-a6ba5f0271cb" />

#### 프론트엔드
- **플랫폼**: Vercel
- **프레임워크**: React
- **배포**: 자동 배포 (GitHub 연동)

#### 백엔드 인프라
- **컴퓨팅**: Amazon EC2 (Private Subnet)
- **컨테이너**: Docker
- **데이터베이스**: Amazon RDS PostgreSQL (Private Subnet)
- **캐시**: Redis (EC2 인스턴스 내부 또는 ElastiCache)
- **스토리지**: Amazon S3 (파일 저장)

#### 네트워크 구성
- **VPC**: Virtual Private Cloud
- **Public Subnet**: 
  - NAT Gateway: Private Subnet의 인스턴스가 외부 인터넷 접근
  - Internet Gateway: 외부에서 VPC 접근
- **Private Subnet**: 
  - EC2 인스턴스: 백엔드 서비스 실행
  - RDS 인스턴스: 데이터베이스 서버

### 보안

- **Private Subnet**: 백엔드 서버와 데이터베이스를 프라이빗 네트워크에 배치하여 외부 직접 접근 차단
- **Security Groups**: EC2와 RDS에 대한 네트워크 접근 제어
- **SSH 접근**: EC2 인스턴스는 SSH 포트(22)를 통해서만 접근 가능
- **데이터베이스 보안**: RDS는 Private Subnet 내부에서만 접근 가능

## 📚 API 문서

애플리케이션 실행 후 Swagger UI에서 API 문서를 확인할 수 있습니다.

- **로컬**: http://localhost:8080/swagger-ui/index.html
- **원격**: http://43.200.89.199:8080/swagger-ui/index.html

## 🔐 보안 기능

### JWT 토큰 관리

- **Access Token**: 짧은 만료시간 (15분)으로 보안 강화
- **Refresh Token**: 긴 만료시간 (14일)으로 사용자 편의성 제공
- **블랙리스트 시스템**: 로그아웃 및 비밀번호 변경 시 토큰 즉시 무효화
- **화이트리스트 시스템**: 자주 사용하는 토큰 캐싱으로 성능 최적화

### 이메일 인증

- 6자리 랜덤 인증코드 생성
- Redis TTL을 통한 자동 만료 (5분)
- Rate Limiting으로 무차별 대입 공격 방지
- 일일 최대 발송 횟수 제한 (5회)

### 비밀번호 보안

- BCrypt 해싱 알고리즘 사용
- 비밀번호 규칙 검증

## 📊 데이터베이스 스키마
<img width="1180" height="597" alt="스크린샷 2025-12-09 오후 6 31 47" src="https://github.com/user-attachments/assets/1928e2f6-735d-4f9d-a96a-80062156976b" />



## 🧪 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 실행
./gradlew test --tests "com.PetFit.backend.auth.*"
```

## 📝 개발 가이드

### 코드 컨벤션

- 패키지명: `com.PetFit.backend`
- 클래스명: PascalCase
- 메서드명: camelCase
- 상수명: UPPER_SNAKE_CASE

### 커밋 메시지
<img width="370" height="321" alt="스크린샷 2025-12-12 오전 10 16 27" src="https://github.com/user-attachments/assets/03cf1739-464f-4ecb-8aec-ae3129a4571a" />


## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다.

## 👥 팀

PetFit Development Team

---

**PetFit** - 반려동물을 위한 스타일링 쇼핑몰

