# PetFit API 명세서

> Base URL: `http://localhost:8080`
> 인증: JWT Bearer Token (`Authorization: Bearer {accessToken}`)
> 공통 응답 형식: `BaseResponse<T>`

```json
{
  "timestamp": "2026-03-19T12:00:00",
  "code": "COMMON200",
  "message": "성공입니다.",
  "result": { ... }
}
```

---

## 1. 인증 (Auth)

### 1.1 회원가입
```
POST /api/auth/signup
```
| 인증 | 필요 없음 |
|------|-----------|

**Request Body**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| email | String | O | 이메일 (인증 완료된) |
| userId | String | O | 아이디 |
| password | String | O | 비밀번호 |
| name | String | O | 이름 |
| birth | String | O | 생년월일 (YYYY-MM-DD) |

**Response**: `LoginResponse`
| 필드 | 타입 | 설명 |
|------|------|------|
| accessToken | String | JWT 액세스 토큰 |
| refreshToken | String | JWT 리프레시 토큰 |

---

### 1.2 로그인
```
POST /api/auth/login
```
| 인증 | 필요 없음 |
|------|-----------|

**Request Body**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| userId | String | O | 아이디 |
| password | String | O | 비밀번호 |

**Response**: `LoginResponse` (위와 동일)

---

### 1.3 로그아웃
```
DELETE /api/auth/logout
```
| 인증 | 필요 |
|------|------|

**Request Body**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| refreshToken | String | O | 리프레시 토큰 |

---

### 1.4 토큰 재발급
```
POST /api/auth/reissue
```
| 인증 | 필요 없음 |
|------|-----------|

**Request Body**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| refreshToken | String | O | 리프레시 토큰 |

**Response**: `TokenReissueResponse`
| 필드 | 타입 | 설명 |
|------|------|------|
| accessToken | String | 새 액세스 토큰 |
| refreshToken | String | 새 리프레시 토큰 |

---

### 1.5 프로필 조회
```
GET /api/auth/profile
```
| 인증 | 필요 |
|------|------|

**Response**: `ProfileResponse`
| 필드 | 타입 | 설명 |
|------|------|------|
| userId | String | 아이디 |
| email | String | 이메일 |
| name | String | 이름 |
| birth | String | 생년월일 |

---

### 1.6 프로필 수정
```
PATCH /api/auth/profile
```
| 인증 | 필요 |
|------|------|

**Request Body**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| name | String | X | 이름 |
| birth | String | X | 생년월일 |
| currentPassword | String | X | 현재 비밀번호 (변경 시) |
| newPassword | String | X | 새 비밀번호 |

---

## 2. 이메일 인증 (Email)

### 2.1 인증 코드 발송
```
POST /api/email/verification/send
```
| 인증 | 필요 없음 |
|------|-----------|

**Request Body**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| email | String | O | 이메일 주소 |

---

### 2.2 인증 코드 확인
```
POST /api/email/verification/verify
```
| 인증 | 필요 없음 |
|------|-----------|

**Request Body**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| email | String | O | 이메일 주소 |
| verificationCode | String | O | 인증 코드 (6자리) |

**Response**: `EmailVerificationResponse`
| 필드 | 타입 | 설명 |
|------|------|------|
| verified | boolean | 인증 성공 여부 |
| expiresInSec | long | 만료까지 남은 시간(초) |

---

## 3. 상품 (Product)

### 3.1 상품 목록 조회
```
GET /api/products?page=0&size=20&sort=createdAt,desc
```
| 인증 | 필요 없음 |
|------|-----------|

**Query Parameters**
| 파라미터 | 타입 | 기본값 | 설명 |
|----------|------|--------|------|
| page | int | 0 | 페이지 번호 |
| size | int | 20 | 페이지 크기 |
| sort | String | - | 정렬 기준 |

**Response**: `Page<ProductListResponse>`
| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 상품 ID |
| name | String | 상품명 |
| price | Integer | 가격 |
| thumbnailUrl | String | 썸네일 URL |
| categoryName | String | 카테고리명 |
| isNew | Boolean | 신상품 여부 |
| isHot | Boolean | 인기상품 여부 |
| isSale | Boolean | 세일 여부 |
| discountRate | Integer | 할인율 (%) |
| productUrl | String | 상품 URL |
| createdAt | DateTime | 등록일 |

---

### 3.2 상품 상세 조회
```
GET /api/products/{id}
```
| 인증 | 필요 없음 |
|------|-----------|

**Response**: `ProductDetailResponse`
| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 상품 ID |
| name | String | 상품명 |
| description | String | 상품 설명 |
| price | Integer | 가격 |
| stockQuantity | Integer | 재고 수량 |
| categoryName | String | 카테고리명 |
| thumbnailUrl | String | 썸네일 URL |
| isNew | Boolean | 신상품 여부 |
| isHot | Boolean | 인기상품 여부 |
| isSale | Boolean | 세일 여부 |
| discountRate | Integer | 할인율 (%) |
| productUrl | String | 상품 URL |
| avgRating | Double | 평균 평점 (0.0~5.0) |
| reviewCount | Long | 리뷰 수 |
| options | List | 옵션 목록 |
| images | List | 이미지 목록 |

**options 항목**
| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 옵션 ID |
| size | String | 사이즈 |
| color | String | 색상 |
| additionalPrice | Integer | 추가 금액 |
| stockQuantity | Integer | 옵션 재고 |

**images 항목**
| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 이미지 ID |
| imageUrl | String | 이미지 URL |
| imageOrder | Integer | 정렬 순서 |

---

### 3.3 상품 검색
```
GET /api/products/search?keyword={keyword}&page=0&size=20
```
| 인증 | 필요 없음 |
|------|-----------|

**Response**: `Page<ProductListResponse>` (3.1과 동일)

---

### 3.4 상품 필터링
```
GET /api/products/filter?categoryId={id}&minPrice={min}&maxPrice={max}&page=0&size=20
```
| 인증 | 필요 없음 |
|------|-----------|

**Query Parameters** (모두 선택)
| 파라미터 | 타입 | 설명 |
|----------|------|------|
| categoryId | Long | 카테고리 ID |
| minPrice | Integer | 최소 가격 |
| maxPrice | Integer | 최대 가격 |

---

### 3.5 큐레이션 상품 조회
```
GET /api/products/curated
```
| 인증 | 필요 없음 |
|------|-----------|

**Response**: `Page<ProductListResponse>` (최신 10개)

---

### 3.6 인기 상품 조회
```
GET /api/products/popular?page=0&size=20
```
| 인증 | 필요 없음 |
|------|-----------|

**Response**: `Page<ProductListResponse>` (리뷰 수 기준 내림차순)

---

## 4. 카테고리 (Category)

### 4.1 카테고리 목록 조회
```
GET /api/categories
```
| 인증 | 필요 없음 |
|------|-----------|

**Response**: `List<CategoryResponse>`
| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 카테고리 ID |
| name | String | 카테고리명 |
| children | List | 하위 카테고리 목록 |

---

## 5. 장바구니 (Cart)

### 5.1 장바구니 추가
```
POST /api/cart
```
| 인증 | 필요 |
|------|------|

**Request Body**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| productId | Long | O | 상품 ID |
| productOptionId | Long | X | 옵션 ID |
| quantity | Integer | O | 수량 |

**Response**: `CartResponse`

---

### 5.2 장바구니 조회
```
GET /api/cart
```
| 인증 | 필요 |
|------|------|

**Response**: `List<CartResponse>`
| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 장바구니 항목 ID |
| productId | Long | 상품 ID |
| productName | String | 상품명 |
| thumbnailUrl | String | 썸네일 URL |
| productOptionId | Long | 옵션 ID |
| size | String | 사이즈 |
| color | String | 색상 |
| price | Integer | 상품 가격 |
| additionalPrice | Integer | 추가 금액 |
| quantity | Integer | 수량 |

---

### 5.3 장바구니 수량 변경
```
PATCH /api/cart/{id}
```
| 인증 | 필요 |
|------|------|

**Request Body**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| quantity | Integer | O | 변경할 수량 |

---

### 5.4 장바구니 삭제
```
DELETE /api/cart/{id}
```
| 인증 | 필요 |
|------|------|

---

## 6. 찜 목록 (Wishlist)

### 6.1 찜 추가
```
POST /api/wishlist
```
| 인증 | 필요 |
|------|------|

**Request Body**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| productId | Long | O | 상품 ID |

---

### 6.2 찜 목록 조회
```
GET /api/wishlist
```
| 인증 | 필요 |
|------|------|

**Response**: `List<WishlistResponse>`
| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 찜 ID |
| productId | Long | 상품 ID |
| productName | String | 상품명 |
| price | Integer | 가격 |
| thumbnailUrl | String | 썸네일 URL |

---

### 6.3 찜 삭제
```
DELETE /api/wishlist/{productId}
```
| 인증 | 필요 |
|------|------|

---

### 6.4 상품별 찜 수 조회
```
GET /api/wishlist/counts
```
| 인증 | 필요 없음 |
|------|-----------|

**Response**: `Map<Long, Long>`
```json
{
  "1": 5,
  "3": 12,
  "7": 3
}
```

---

## 7. 주문 (Order)

### 7.1 주문 생성
```
POST /api/orders
```
| 인증 | 필요 |
|------|------|

> 장바구니의 전체 상품으로 주문이 생성됩니다. 주문 후 장바구니는 비워집니다.

**Request Body**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| address | String | O | 배송 주소 |
| phone | String | O | 전화번호 |
| recipientName | String | O | 수령인 이름 |

**Response**: `OrderResponse`

---

### 7.2 주문 목록 조회
```
GET /api/orders?page=0&size=10
```
| 인증 | 필요 |
|------|------|

**Response**: `Page<OrderResponse>`
| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 주문 ID |
| totalPrice | Integer | 총 금액 |
| status | String | 주문 상태 (PENDING/PAID/SHIPPING/DELIVERED/CANCELLED) |
| address | String | 배송 주소 |
| phone | String | 전화번호 |
| recipientName | String | 수령인 |
| createdAt | DateTime | 주문일시 |
| items | List | 주문 항목 |

**items 항목**
| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 주문 항목 ID |
| productId | Long | 상품 ID |
| productName | String | 상품명 |
| thumbnailUrl | String | 썸네일 URL |
| size | String | 사이즈 |
| color | String | 색상 |
| quantity | Integer | 수량 |
| price | Integer | 금액 |

---

### 7.3 주문 상세 조회
```
GET /api/orders/{id}
```
| 인증 | 필요 |
|------|------|

**Response**: `OrderResponse` (위와 동일)

---

### 7.4 주문 취소
```
PATCH /api/orders/{id}/cancel
```
| 인증 | 필요 |
|------|------|

> PENDING 상태의 주문만 취소 가능

**Response**: `OrderResponse`

---

## 8. 리뷰 (Review)

### 8.1 리뷰 작성
```
POST /api/reviews
```
| 인증 | 필요 |
|------|------|

**Request Body**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| productId | Long | O | 상품 ID |
| orderId | Long | O | 주문 ID |
| rating | Integer | O | 평점 (1~5) |
| content | String | O | 리뷰 내용 |
| imageUrl | String | X | 이미지 URL |

---

### 8.2 상품별 리뷰 조회
```
GET /api/reviews/product/{productId}?page=0&size=10
```
| 인증 | 필요 없음 |
|------|-----------|

**Response**: `Page<ReviewResponse>`
| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | 리뷰 ID |
| userId | String | 작성자 ID |
| productId | Long | 상품 ID |
| rating | Integer | 평점 |
| content | String | 내용 |
| imageUrl | String | 이미지 URL |
| createdAt | DateTime | 작성일 |

---

### 8.3 리뷰 수정
```
PATCH /api/reviews/{id}
```
| 인증 | 필요 |
|------|------|

**Request Body**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| rating | Integer | X | 평점 |
| content | String | X | 내용 |
| imageUrl | String | X | 이미지 URL |

---

### 8.4 리뷰 삭제
```
DELETE /api/reviews/{id}
```
| 인증 | 필요 |
|------|------|

---

## 9. 마이페이지 (MyPage)

### 9.1 마이페이지 조회
```
GET /api/mypage
```
| 인증 | 필요 |
|------|------|

**Response**: `MyPageResponse`
| 필드 | 타입 | 설명 |
|------|------|------|
| userId | String | 아이디 |
| name | String | 이름 |
| email | String | 이메일 |
| orderCount | long | 총 주문 수 |
| reviewCount | long | 총 리뷰 수 |
| wishlistCount | long | 총 찜 수 |
| recentOrders | List | 최근 주문 5건 |

---

## 10. AI 스타일링 (AI Styling)

### 10.1 AI 가상 피팅
```
POST /api/ai/styling
```
| 인증 | 필요 없음 |
|------|-----------|

**Request Body**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| petImageBase64 | String | O | 반려동물 사진 (Base64) |
| clothImageBase64 | String | O | 옷 사진 (Base64) |

**Response**: `StyleResponse`
| 필드 | 타입 | 설명 |
|------|------|------|
| resultImageBase64 | String | 합성 결과 이미지 (Base64) |

---

## 에러 코드

| 코드 | HTTP | 설명 |
|------|------|------|
| AUTH001 | 400 | 잘못된 비밀번호 |
| AUTH002 | 409 | 이미 존재하는 아이디 |
| AUTH003 | 404 | 사용자를 찾을 수 없음 |
| AUTH004 | 401 | 유효하지 않은 토큰 |
| AUTH005 | 400 | 이메일 인증 필요 |
| PRODUCT001 | 404 | 상품을 찾을 수 없음 |
| PRODUCT002 | 404 | 상품 옵션을 찾을 수 없음 |
| PRODUCT003 | 400 | 상품 재고 부족 |
| CART001 | 404 | 장바구니 항목을 찾을 수 없음 |
| CART002 | 409 | 이미 장바구니에 있는 상품 |
| ORDER001 | 404 | 주문을 찾을 수 없음 |
| ORDER002 | 400 | 장바구니가 비어있음 |
| ORDER003 | 400 | 상품 재고 부족 |
| ORDER004 | 400 | 이미 취소된 주문 |
| ORDER005 | 400 | 취소할 수 없는 주문 상태 |
| REVIEW001 | 404 | 리뷰를 찾을 수 없음 |
| REVIEW002 | 409 | 이미 작성한 리뷰 |
| WISHLIST001 | 404 | 찜을 찾을 수 없음 |
| WISHLIST002 | 409 | 이미 찜한 상품 |
