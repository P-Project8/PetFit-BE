# PetFit 부하 테스트 (k6)

## 목적
실무자 피드백("기술 선택 근거 + 트래픽 대응") 반영. 시나리오별 병목 식별 및
용량 산정.

## 도구
[k6](https://k6.io/) — JavaScript 시나리오 기반 부하 테스트.

## 설치 (Ubuntu / EC2)
```bash
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update
sudo apt-get install k6
```

## 시나리오

| 파일 | 설명 | 동접 | 기간 |
|------|------|------|------|
| `feed-load.js` | 갤러리 피드 조회 (가장 빈번한 GET) | 0→50→0 | 2분 |
| `login-spike.js` | 로그인 동시 다발 (DB + JWT 발급) | 0→100→0 | 1분 |
| `ai-styling.js` | AI 스타일링 호출 (외부 API + 결제 등급 필요) | 0→10→0 | 5분 |

## 실행
```bash
# 환경변수로 백엔드 URL 지정 (선택, 기본값: localhost)
export BASE_URL=http://54.180.118.43:8080

# 시나리오 실행
k6 run load-tests/feed-load.js
k6 run load-tests/login-spike.js
k6 run --env JWT_TOKEN=<발급받은_액세스_토큰> load-tests/ai-styling.js

# 리포트 파일로 저장
k6 run --summary-export=load-tests/results/feed-summary.json load-tests/feed-load.js
```

## 결과 해석

### 좋은 지표
- p95 < 500ms (사용자 체감 빠름)
- 에러율 < 1%
- 동접 증가에도 응답 시간 안정

### 위험 지표
- p95 > 2s → DB 쿼리 또는 외부 API 지연
- 5xx 발생 → 백엔드 한계 초과
- 응답 시간이 동접에 정비례 → 병렬 처리 한계

## ⚠️ AI 시나리오 주의사항

- Gemini API는 호출당 약 $0.04 비용 발생
- `ai-styling.js`는 10 동접 × 30초 = 약 100건 호출 → **약 $4 비용**
- 부하 테스트는 **개발/평가 단계에만 실행**, 평소엔 피드 시나리오만 권장
