# PetFit Backend - 프로젝트 구조

## 기술 스택
- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Database**: PostgreSQL
- **Cache**: Redis
- **Security**: Spring Security, JWT (JJWT)
- **Email**: Spring Mail (Google SMTP)
- **AI**: Google Gemini API
- **API Docs**: Swagger (SpringDoc OpenAPI)
- **Build**: Gradle
- **Container**: Docker

---

## 아키텍처 규칙

```
의존성 방향: presentation → application → domain (단방향)
트랜잭션 경계: UseCase 단위
```

| 레이어 | 역할 | 네이밍 |
|--------|------|--------|
| **presentation** | HTTP 입출력 (파싱, 검증, 상태코드) | XXXController, XXXRequest, XXXResponse |
| **application** | 시나리오 오케스트레이션, 트랜잭션 | XXXUseCase |
| **domain** | 비즈니스 규칙, 엔티티, 리포지토리 | XXXEntity, XXXRepository, XXXService |

---

## 디렉토리 구조

```
com.PetFit.backend
│
├── AuthApplication.java                    # Spring Boot 메인 클래스
│
├── auth/                                   # 인증/회원 도메인
│   └── domain/
│       ├── auth/                           # 인증 (로그인/회원가입/토큰)
│       │   ├── application/usecase/
│       │   │   ├── UserAuthUseCase.java        # 로그인, 회원가입 처리
│       │   │   ├── TokenReissueUseCase.java    # Access Token 재발급
│       │   │   ├── UserProfileUseCase.java     # 프로필 조회
│       │   │   └── UpdateProfileUseCase.java   # 프로필 수정
│       │   ├── domain/
│       │   │   ├── entity/
│       │   │   │   └── User.java               # 사용자 엔티티
│       │   │   ├── repository/
│       │   │   │   └── UserRepository.java
│       │   │   └── service/
│       │   │       ├── UserService.java             # 사용자 CRUD
│       │   │       ├── RefreshTokenService.java     # Refresh Token 관리 (Redis)
│       │   │       ├── TokenBlacklistService.java   # 토큰 블랙리스트 (Redis)
│       │   │       ├── TokenWhitelistService.java   # 토큰 화이트리스트 (Redis)
│       │   │       └── TokenReissueService.java     # 토큰 재발급 로직
│       │   └── presentation/
│       │       ├── AuthController.java          # 로그인/회원가입/로그아웃/토큰 API
│       │       ├── UserController.java          # 프로필 조회/수정 API
│       │       └── dto/
│       │           ├── request/
│       │           │   ├── LoginRequest.java
│       │           │   ├── SignUpRequest.java
│       │           │   ├── TokenReissueRequest.java
│       │           │   └── UpdateProfileRequest.java
│       │           └── response/
│       │               ├── LoginResponse.java
│       │               ├── ProfileResponse.java
│       │               └── TokenReissueResponse.java
│       │
│       └── email/                          # 이메일 인증
│           ├── application/usecase/
│           │   ├── SendEmailVerificationUseCase.java  # 인증 코드 발송
│           │   └── VerifyEmailUseCase.java            # 인증 코드 검증
│           ├── domain/service/
│           │   ├── EmailService.java                  # 이메일 발송 (SMTP)
│           │   └── EmailVerificationService.java      # 인증 코드 저장/검증 (Redis)
│           └── presentation/
│               ├── EmailController.java
│               └── dto/
│                   ├── request/
│                   │   ├── SendVerificationRequest.java
│                   │   └── VerifyEmailRequest.java
│                   └── response/
│                       └── EmailVerificationResponse.java
│
├── product/                                # 상품 도메인
│   ├── application/usecase/
│   │   └── ProductUseCase.java             # 상품 조회/검색/필터/인기순
│   ├── domain/
│   │   ├── entity/
│   │   │   ├── Product.java                # 상품 (이름, 가격, 재고, isNew/isHot/isSale, discountRate)
│   │   │   ├── ProductOption.java          # 상품 옵션 (사이즈, 색상, 추가금액, 재고)
│   │   │   └── ProductImage.java           # 상품 이미지 (URL, 순서, isMain)
│   │   ├── repository/
│   │   │   ├── ProductRepository.java      # 상품 검색/필터/인기순 쿼리 포함
│   │   │   ├── ProductOptionRepository.java
│   │   │   └── ProductImageRepository.java
│   │   └── service/
│   │       └── ProductService.java         # 상품 조회 도메인 로직
│   └── presentation/
│       ├── ProductController.java          # GET /api/products, /search, /filter, /popular, /curated
│       └── dto/response/
│           ├── ProductListResponse.java    # 목록용 (id, name, price, thumbnailUrl, 배지)
│           ├── ProductDetailResponse.java  # 상세용 (옵션, 이미지, avgRating, reviewCount)
│           ├── ProductOptionResponse.java
│           └── ProductImageResponse.java
│
├── category/                               # 카테고리 도메인
│   ├── application/usecase/
│   │   └── CategoryUseCase.java            # 카테고리 목록 조회
│   ├── domain/
│   │   ├── entity/
│   │   │   └── Category.java               # 카테고리 (이름, displayOrder, 부모-자식 계층)
│   │   ├── repository/
│   │   │   └── CategoryRepository.java
│   │   └── service/
│   │       └── CategoryService.java
│   └── presentation/
│       ├── CategoryController.java         # GET /api/categories
│       └── dto/response/
│           └── CategoryResponse.java       # 계층 구조 응답 (children 포함)
│
├── cart/                                   # 장바구니 도메인
│   ├── application/usecase/
│   │   └── CartUseCase.java                # 장바구니 CRUD + 주문 시 활용
│   ├── domain/
│   │   ├── entity/
│   │   │   └── Cart.java                   # 장바구니 (userId, product, option, quantity)
│   │   ├── repository/
│   │   │   └── CartRepository.java
│   │   └── service/
│   │       └── CartService.java
│   └── presentation/
│       ├── CartController.java             # /api/cart (GET, POST, PATCH, DELETE)
│       └── dto/
│           ├── request/
│           │   ├── AddCartRequest.java
│           │   └── UpdateCartQuantityRequest.java
│           └── response/
│               └── CartResponse.java
│
├── wishlist/                               # 찜 목록 도메인
│   ├── application/usecase/
│   │   └── WishlistUseCase.java            # 찜 추가/삭제/토글/찜수 카운트
│   ├── domain/
│   │   ├── entity/
│   │   │   └── Wishlist.java               # 찜 (userId, productId, soft delete)
│   │   ├── repository/
│   │   │   └── WishlistRepository.java
│   │   └── service/
│   │       └── WishlistService.java
│   └── presentation/
│       ├── WishlistController.java         # /api/wishlist (GET, POST, DELETE, /counts)
│       └── dto/
│           ├── request/
│           │   └── AddWishlistRequest.java
│           └── response/
│               └── WishlistResponse.java
│
├── order/                                  # 주문 도메인
│   ├── application/usecase/
│   │   └── OrderUseCase.java               # 주문 생성 (장바구니→주문), 조회, 취소
│   ├── domain/
│   │   ├── entity/
│   │   │   ├── Order.java                  # 주문 (userId, totalPrice, status, 배송정보)
│   │   │   ├── OrderItem.java              # 주문 상세 항목 (product, option, quantity, price)
│   │   │   └── OrderStatus.java            # PENDING, PAID, SHIPPING, DELIVERED, CANCELLED
│   │   ├── repository/
│   │   │   ├── OrderRepository.java
│   │   │   └── OrderItemRepository.java
│   │   └── service/
│   │       └── OrderService.java
│   └── presentation/
│       ├── OrderController.java            # /api/orders (POST, GET, PATCH /{id}/cancel)
│       └── dto/
│           ├── request/
│           │   └── CreateOrderRequest.java
│           └── response/
│               ├── OrderResponse.java
│               └── OrderItemResponse.java
│
├── review/                                 # 리뷰 도메인
│   ├── application/usecase/
│   │   └── ReviewUseCase.java              # 리뷰 작성/수정/삭제/조회
│   ├── domain/
│   │   ├── entity/
│   │   │   └── Review.java                 # 리뷰 (userId, product, order, rating, content)
│   │   ├── repository/
│   │   │   └── ReviewRepository.java       # 평균 평점, 상품별 리뷰 수 쿼리 포함
│   │   └── service/
│   │       └── ReviewService.java
│   └── presentation/
│       ├── ReviewController.java           # /api/reviews (POST, PATCH, DELETE, GET)
│       └── dto/
│           ├── request/
│           │   ├── CreateReviewRequest.java
│           │   └── UpdateReviewRequest.java
│           └── response/
│               └── ReviewResponse.java
│
├── ai/                                     # AI 스타일링 도메인
│   ├── application/usecase/
│   │   ├── StyleImageUseCase.java          # AI 가상 피팅 실행
│   │   └── GetStyleHistoryUseCase.java     # 스타일링 이력 조회
│   ├── domain/
│   │   ├── entity/
│   │   │   └── AiStyling.java              # AI 스타일링 (petImageUrl, clothImageUrl, resultImageUrl, status)
│   │   ├── repository/
│   │   │   └── AiStylingRepository.java
│   │   └── service/
│   │       ├── GeminiAIService.java        # Google Gemini API 호출
│   │       ├── AiStylingService.java       # 스타일링 CRUD
│   │       └── S3Service.java              # (미사용, 향후 이미지 스토리지)
│   └── presentation/
│       ├── AiController.java               # POST /api/ai/styling
│       └── dto/
│           ├── request/
│           │   └── StyleRequest.java       # petImageBase64, productId
│           └── response/
│               ├── StyleResponse.java      # resultImageBase64
│               └── StyleHistoryResponse.java
│
├── mypage/                                 # 마이페이지 도메인
│   ├── application/usecase/
│   │   └── MyPageUseCase.java              # 마이페이지 통합 조회 (주문, 리뷰, 찜 수)
│   └── presentation/
│       ├── MyPageController.java           # GET /api/mypage
│       └── dto/response/
│           └── MyPageResponse.java
│
└── global/                                 # 전역 공통 기능
    ├── annotation/
    │   ├── CurrentUser.java                # @CurrentUser - JWT에서 userId 추출
    │   └── RefreshToken.java               # @RefreshToken - Refresh Token 추출
    │
    ├── common/
    │   ├── BaseEntity.java                 # createdAt, updatedAt, deletedAt (soft delete)
    │   └── BaseResponse.java               # { isSuccess, code, message, result } 공통 응답
    │
    ├── config/
    │   ├── properties/
    │   │   └── CorsProperties.java         # CORS 설정 (origins, methods, headers)
    │   ├── AwsConfig.java                  # AWS 설정 (향후 S3용)
    │   ├── EmailConfig.java                # SMTP 이메일 설정
    │   ├── RestTemplateConfig.java         # HTTP 클라이언트 빈
    │   ├── SecurityConfig.java             # Spring Security + JWT 필터 체인
    │   ├── SwaggerConfig.java              # Swagger/OpenAPI 설정
    │   └── WebMvcConfig.java               # Argument Resolver, Interceptor 등록
    │
    ├── exception/
    │   ├── code/
    │   │   ├── BaseCode.java               # 에러 코드 빌더
    │   │   ├── BaseCodeInterface.java      # 에러 코드 인터페이스
    │   │   └── status/                     # 도메인별 에러 코드 enum
    │   │       ├── AuthErrorStatus.java
    │   │       ├── EmailErrorStatus.java
    │   │       ├── GlobalErrorStatus.java
    │   │       ├── SuccessStatus.java
    │   │       ├── AiErrorStatus.java
    │   │       ├── CartErrorStatus.java
    │   │       ├── CategoryErrorStatus.java
    │   │       ├── OrderErrorStatus.java
    │   │       ├── ProductErrorStatus.java
    │   │       ├── ReviewErrorStatus.java
    │   │       └── WishlistErrorStatus.java
    │   ├── ExceptionAdvice.java            # @ControllerAdvice 전역 예외 핸들러
    │   └── RestApiException.java           # 커스텀 런타임 예외
    │
    ├── interceptor/
    │   └── JwtBlacklistInterceptor.java    # 블랙리스트 토큰 차단
    │
    ├── resolver/
    │   ├── CurrentUserArgumentResolver.java    # @CurrentUser 파라미터 리졸버
    │   └── RefreshTokenArgumentResolver.java   # @RefreshToken 파라미터 리졸버
    │
    ├── security/
    │   ├── ExcludeAuthPathProperties.java      # 인증 제외 경로 설정
    │   ├── ExcludeBlacklistPathProperties.java # 블랙리스트 제외 경로 설정
    │   ├── JwtAuthenticationFilter.java        # JWT 토큰 검증 필터
    │   ├── JwtProperties.java                  # JWT 키, 만료시간 설정
    │   └── TokenProvider.java                  # JWT 생성/검증
    │
    ├── swagger/                            # Swagger API 인터페이스 (컨트롤러 문서화)
    │   ├── BaseApi.java
    │   ├── AuthApi.java
    │   ├── EmailApi.java
    │   ├── UserProfileApi.java
    │   ├── ProductApi.java
    │   ├── CategoryApi.java
    │   ├── CartApi.java
    │   ├── WishlistApi.java
    │   ├── OrderApi.java
    │   ├── ReviewApi.java
    │   ├── MyPageApi.java
    │   └── AiApi.java
    │
    └── util/
        └── SecureRandomGenerator.java      # 보안 랜덤 문자열 생성

---

## 리소스 파일

```
src/main/resources/
├── application.yml              # 메인 설정 (JPA, CORS, Swagger, 인증 경로, AI 설정)
├── application-secret.yml       # 비밀 설정 (DB 접속, JWT 키, SMTP, Gemini API 키)
└── data-seed.sql                # 초기 데이터 (카테고리 7개, 상품 180개, 옵션 1740개, 리뷰 84개)
```

---

## API 엔드포인트 요약

### 인증 (Auth)
| Method | URI | 설명 | 인증 |
|--------|-----|------|------|
| POST | /api/auth/signup | 회원가입 | X |
| POST | /api/auth/login | 로그인 | X |
| POST | /api/auth/reissue | 토큰 재발급 | X |
| POST | /api/auth/verify | 토큰 유효성 검증 | X |
| DELETE | /api/auth/logout | 로그아웃 | O |
| GET | /api/auth/profile | 프로필 조회 | O |
| PATCH | /api/auth/profile | 프로필 수정 | O |

### 이메일 인증
| Method | URI | 설명 | 인증 |
|--------|-----|------|------|
| POST | /api/email/verification/send | 인증 코드 발송 | X |
| POST | /api/email/verification/verify | 인증 코드 검증 | X |

### 상품 (Product)
| Method | URI | 설명 | 인증 |
|--------|-----|------|------|
| GET | /api/products | 상품 목록 (페이징) | X |
| GET | /api/products/{id} | 상품 상세 (평점, 리뷰수 포함) | X |
| GET | /api/products/search?keyword= | 상품 검색 | X |
| GET | /api/products/filter | 카테고리/가격 필터 | X |
| GET | /api/products/curated | 큐레이션 상품 | X |
| GET | /api/products/popular | 인기 상품 (리뷰수 기준) | X |

### 카테고리 (Category)
| Method | URI | 설명 | 인증 |
|--------|-----|------|------|
| GET | /api/categories | 카테고리 목록 (계층 구조) | X |

### 장바구니 (Cart)
| Method | URI | 설명 | 인증 |
|--------|-----|------|------|
| GET | /api/cart | 장바구니 조회 | O |
| POST | /api/cart | 장바구니 추가 | O |
| PATCH | /api/cart/{id} | 수량 변경 | O |
| DELETE | /api/cart/{id} | 장바구니 삭제 | O |

### 찜 (Wishlist)
| Method | URI | 설명 | 인증 |
|--------|-----|------|------|
| GET | /api/wishlist | 찜 목록 조회 | O |
| POST | /api/wishlist | 찜 추가/토글 | O |
| DELETE | /api/wishlist/{productId} | 찜 삭제 | O |
| GET | /api/wishlist/counts | 상품별 찜 수 | X |
| GET | /api/wishlist/my | 내 찜 상품 ID 목록 | O |

### 주문 (Order)
| Method | URI | 설명 | 인증 |
|--------|-----|------|------|
| POST | /api/orders | 주문 생성 (장바구니 → 주문) | O |
| GET | /api/orders | 주문 목록 (페이징) | O |
| GET | /api/orders/{id} | 주문 상세 | O |
| PATCH | /api/orders/{id}/cancel | 주문 취소 (PENDING만) | O |

### 리뷰 (Review)
| Method | URI | 설명 | 인증 |
|--------|-----|------|------|
| GET | /api/reviews?productId= | 상품별 리뷰 조회 | X |
| POST | /api/reviews | 리뷰 작성 | O |
| PATCH | /api/reviews/{id} | 리뷰 수정 | O |
| DELETE | /api/reviews/{id} | 리뷰 삭제 | O |

### AI 스타일링
| Method | URI | 설명 | 인증 |
|--------|-----|------|------|
| POST | /api/ai/styling | AI 가상 피팅 | X |

### 마이페이지
| Method | URI | 설명 | 인증 |
|--------|-----|------|------|
| GET | /api/mypage | 마이페이지 통합 정보 | O |
