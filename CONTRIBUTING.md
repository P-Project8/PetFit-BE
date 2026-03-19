# PetFit Backend - 코드 컨벤션 & 개발 가이드

## 아키텍처

### 레이어드 아키텍처 (단방향 의존성)

```
presentation → application → domain
```

- **domain**은 어떤 프레임워크도 의존하지 않아야 합니다 (순수 비즈니스 로직).
- **트랜잭션 경계**는 UseCase 단위로 설정합니다.

### 레이어별 역할

| 레이어 | 패키지 | 역할 | 네이밍 규칙 |
|--------|--------|------|-------------|
| **Presentation** | `presentation/` | HTTP 입출력 어댑터. 파싱, 검증, 상태코드 매핑만 담당. 얇게 유지 | `XXXController` |
| **Application** | `application/usecase/` | 시나리오 오케스트레이션, 트랜잭션 경계, 권한/비즈니스 규칙 위임 | `XXXUseCase` |
| **Domain** | `domain/entity/`, `domain/repository/`, `domain/service/` | 비즈니스 규칙의 중심. 엔티티는 불변 | `XXXEntity`, `XXXRepository`, `XXXService` |
| **Infra** | `infra/` | 레포지토리 구현체, 외부 시스템 어댑터 | `XXXAdapter`, `XXXPublisher` |

### DTO 위치

- DTO(Request/Response)는 **presentation** 레이어에 위치합니다.

```
presentation/
├── XXXController.java
└── dto/
    ├── request/
    │   └── XXXRequest.java
    └── response/
        └── XXXResponse.java
```

---

## 도메인 패키지 구조 (예시)

```
com.PetFit.backend.{도메인}/
├── application/
│   └── usecase/
│       └── XXXUseCase.java
├── domain/
│   ├── entity/
│   │   └── XXX.java
│   ├── repository/
│   │   └── XXXRepository.java
│   └── service/
│       └── XXXService.java
├── presentation/
│   ├── XXXController.java
│   └── dto/
│       ├── request/
│       └── response/
└── infra/                          # 필요 시
```

---

## 공통 규칙

### 응답 형식

모든 API 응답은 `BaseResponse`로 감싸서 반환합니다.

```json
{
  "isSuccess": true,
  "code": "200",
  "message": "요청에 성공하였습니다.",
  "result": { ... }
}
```

### 예외 처리

- 도메인별 `XXXErrorStatus` enum을 `global/exception/code/status/`에 정의합니다.
- `RestApiException`을 throw하면 `ExceptionAdvice`가 자동으로 처리합니다.

```java
throw new RestApiException(ProductErrorStatus.PRODUCT_NOT_FOUND);
```

### Soft Delete

- 모든 엔티티는 `BaseEntity`를 상속하여 `createdAt`, `updatedAt`, `deletedAt`을 갖습니다.
- 삭제 시 `deletedAt`에 시간을 기록하고, 조회 시 `deletedAt IS NULL` 조건을 사용합니다.

### Swagger

- 각 도메인 컨트롤러는 `global/swagger/XXXApi.java` 인터페이스를 구현합니다.
- API 문서는 `/swagger-ui/index.html`에서 확인 가능합니다.

### 인증

- JWT 기반 인증 (Access Token + Refresh Token)
- 인증이 필요한 API에서는 `@CurrentUser String userId` 파라미터로 사용자 ID를 주입받습니다.
- 인증 제외 경로는 `application.yml`의 `exclude-auth-path-patterns`에 설정합니다.

---

## 브랜치 전략

- `main`: 배포용 브랜치
- `feat/xxx`: 기능 개발 브랜치
- PR 생성 후 리뷰를 거쳐 merge합니다.

## 커밋 메시지

```
feat: 새로운 기능 추가
fix: 버그 수정
refactor: 코드 리팩토링
docs: 문서 수정
chore: 빌드, 설정 등 기타 작업
```
