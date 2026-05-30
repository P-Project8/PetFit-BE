# Frontend Patches

PetFit 프론트엔드(PetFit-Front) 레포에 직접 푸시하지 않고도 EC2에서 빌드가 가능하도록 만든 백엔드 측 패치 모음.

## 왜 필요한가

- 프론트엔드 분이 작업한 master 브랜치에는 EC2 빌드를 깨뜨리는 몇 가지 이슈가 있다.
- 프론트 분 작업에 영향을 주지 않고 백엔드/AI 팀에서 EC2를 운영해야 한다.
- 따라서 별도 GitHub PR 없이, 백엔드 레포의 deploy.sh 가 프론트 레포 clone 후 이 폴더의 파일을 덮어쓰도록 한다.

## 포함된 파일

| 파일 | 목적 |
|------|------|
| `Dockerfile` | Nginx 기반 SPA 서빙 (Vite build → Nginx) |
| `nginx.conf` | API 프록시 + SPA 라우팅 |
| `.env.production` | `VITE_API_BASE_URL` 설정 (EC2 백엔드 주소) |
| `src/store/productStore.ts` | `_placeholder` 만 있던 store를 mock 데이터 기반으로 복원 (`products`, `getProductById`) |
| `src/components/ai/ProductSelectionModal.tsx` | `ProductGrid` 타입 호환성 (type assertion) |
| `src/components/ai/ResultSection.tsx` | `ProductGrid` 타입 호환성 (type assertion) |
| `src/pages/StyleGuidePage.tsx` | `ProductCard` 타입 호환성 (type assertion) |
| `src/services/aiStylingService.ts` | 클라이언트 사이드 Gemini 호출을 백엔드 `/api/ai/styling` 경유로 변경 (API 키 보호, S3 저장, 이력 기록) |

## 적용 방법

`deploy.sh` 가 자동으로 처리한다.

```bash
# (deploy.sh 내부 의사 코드)
PATCH_DIR="$BACKEND_DIR/frontend-patches"
for patch in $(find "$PATCH_DIR" -type f -not -name 'README.md'); do
    rel="${patch#$PATCH_DIR/}"
    target="$FRONTEND_DIR/$rel"
    mkdir -p "$(dirname "$target")"
    cp "$patch" "$target"
done
```

## 프론트 분이 같은 파일을 수정했을 때

`deploy.sh` 는 덮어쓰기 전에 변경 사항을 비교해 경고를 띄운다. 충돌 가능성이 있는 파일을 만나면 패치가 멈추므로, 운영자가 수동으로 병합해야 한다.

## 패치가 더 이상 필요 없어지면

프론트 master 가 동일한 수정을 반영하면 그 파일을 이 폴더에서 제거하면 된다.
