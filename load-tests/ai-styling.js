// AI 스타일링 호출 부하 테스트
//
// 시나리오: "로그인 → AI 스타일링 생성" — 외부 API + 결제 비용 발생
// 부하 특성: 외부 의존, 응답 시간 길음, 동시 호출 시 Gemini Quota 영향
//
// ⚠️ 비용 주의: Gemini 이미지 1건 약 $0.04 → 10 VU × 30초 ≈ $4
// ⚠️ FREE 플랜은 월 3회 제한 → PREMIUM 사용자 또는 테스트용 계정 필요
//
// 실행:
//   JWT_TOKEN=$(curl -s -X POST $BASE_URL/api/auth/login \
//     -H 'Content-Type: application/json' \
//     -d '{"loginId":"test1234","password":"password1!"}' \
//     | jq -r .result.accessToken)
//   k6 run --env JWT_TOKEN=$JWT_TOKEN load-tests/ai-styling.js

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';
import encoding from 'k6/encoding';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const JWT = __ENV.JWT_TOKEN;

if (!JWT) {
  throw new Error('JWT_TOKEN 환경변수 필요. README 참고.');
}

const aiLatency = new Trend('ai_latency_ms');
const aiErrors = new Rate('ai_errors');

// 1x1 빨간 PNG (테스트용 더미 — 실제 이미지로 교체 시 base64-image.de 활용)
const TINY_PNG_BASE64 = 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==';

export const options = {
  stages: [
    { duration: '10s', target: 5 },    // 천천히 5명까지
    { duration: '30s', target: 10 },   // 30초간 10 동접 (10건 × 30초 ≈ 100건 호출)
    { duration: '10s', target: 0 },    // 종료
  ],
  thresholds: {
    'ai_latency_ms': ['p(95)<60000'],  // p95 < 60초 (Gemini 응답 한계)
    'ai_errors': ['rate<0.20'],        // 에러율 20% 미만 (Gemini 429/타임아웃 가능)
  },
};

export default function () {
  const payload = JSON.stringify({
    petImageBase64: TINY_PNG_BASE64,
    clothImageBase64: TINY_PNG_BASE64,
    productId: 1,
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${JWT}`,
    },
    timeout: '120s',
    tags: { name: 'AiStyling' },
  };

  const res = http.post(`${BASE_URL}/api/ai/style`, payload, params);
  aiLatency.add(res.timings.duration);

  const ok = check(res, {
    'status 200 or 402(credit)': (r) => r.status === 200 || r.status === 402,
    'response received': (r) => r.body && r.body.length > 0,
  });
  aiErrors.add(!ok);

  sleep(1);
}

export function handleSummary(data) {
  return {
    'load-tests/results/ai-summary.json': JSON.stringify(data, null, 2),
    stdout: textSummary(data),
  };
}

function textSummary(data) {
  const lines = [];
  lines.push('');
  lines.push('===== AI Styling Load Test Summary =====');
  lines.push(`Total Calls: ${data.metrics.http_reqs.values.count}`);
  lines.push(`Error Rate: ${(data.metrics.http_req_failed.values.rate * 100).toFixed(2)}%`);
  lines.push(`Avg Response: ${(data.metrics.http_req_duration.values.avg / 1000).toFixed(1)}s`);
  lines.push(`p95 Response: ${(data.metrics.http_req_duration.values['p(95)'] / 1000).toFixed(1)}s`);
  lines.push('');
  lines.push(`💰 추정 비용: ${(data.metrics.http_reqs.values.count * 0.04).toFixed(2)} USD`);
  lines.push('');
  return lines.join('\n');
}
