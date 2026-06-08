// 갤러리 피드 조회 부하 테스트
//
// 시나리오: "좋아요만 누르거나 피드만 보는 일반 사용자"
// 부하 특성: 읽기 위주, 캐싱 효과 측정
//
// 실행: k6 run load-tests/feed-load.js
// 인증 없이 호출 가능 (Gallery GET은 공개 API)

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const feedLatency = new Trend('feed_latency_ms');

export const options = {
  stages: [
    { duration: '30s', target: 50 },   // 30초간 0 → 50명 점진 증가
    { duration: '1m',  target: 50 },   // 1분간 50명 유지
    { duration: '30s', target: 0 },    // 30초간 종료
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],            // 에러율 1% 미만
    http_req_duration: ['p(95)<500'],          // p95 응답 시간 500ms 미만
    'feed_latency_ms': ['p(95)<500'],
  },
};

export default function () {
  // 1. 피드 페이지 1 조회
  const feedRes = http.get(`${BASE_URL}/api/gallery?page=0&size=20`, {
    tags: { name: 'GalleryFeed' },
  });
  feedLatency.add(feedRes.timings.duration);
  const feedOk = check(feedRes, {
    'feed status 200': (r) => r.status === 200,
    'feed has items': (r) => {
      try {
        const body = r.json();
        return body.result !== undefined;
      } catch { return false; }
    },
  });
  errorRate.add(!feedOk);

  sleep(1);

  // 2. 인기 게시물 조회
  const popularRes = http.get(`${BASE_URL}/api/gallery/popular?page=0&size=10`, {
    tags: { name: 'GalleryPopular' },
  });
  check(popularRes, { 'popular status 200': (r) => r.status === 200 });

  sleep(2); // 사용자가 피드 보는 시간 시뮬레이션
}

export function handleSummary(data) {
  return {
    'load-tests/results/feed-summary.json': JSON.stringify(data, null, 2),
    stdout: textSummary(data),
  };
}

function textSummary(data) {
  const fmt = (v) => (v == null ? 'N/A' : v.toFixed(1));
  const d = data.metrics.http_req_duration?.values || {};
  const lines = [];
  lines.push('');
  lines.push('===== Feed Load Test Summary =====');
  lines.push(`Total Requests: ${data.metrics.http_reqs?.values?.count ?? 0}`);
  lines.push(`Error Rate: ${((data.metrics.http_req_failed?.values?.rate ?? 0) * 100).toFixed(2)}%`);
  lines.push(`Avg Response: ${fmt(d.avg)}ms`);
  lines.push(`p95 Response: ${fmt(d['p(95)'])}ms`);
  lines.push('');
  return lines.join('\n');
}
