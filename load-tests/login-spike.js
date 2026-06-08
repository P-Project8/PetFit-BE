// 로그인 스파이크 테스트
//
// 시나리오: "오픈 직후 동시 다발 로그인" — DB 조회 + BCrypt + JWT 발급 부하 측정
// 부하 특성: 쓰기 X, 그러나 BCrypt 연산 무거움
//
// 실행: k6 run --env TEST_USER=test1234 --env TEST_PASS=password1! load-tests/login-spike.js
//
// 주의: 실제 가입한 계정 1개 필요. 매번 같은 계정으로 로그인.

import http from 'k6/http';
import { check } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const TEST_USER = __ENV.TEST_USER || 'test1234';
const TEST_PASS = __ENV.TEST_PASS || 'password1!';

const loginLatency = new Trend('login_latency_ms');
const errorRate = new Rate('login_errors');

export const options = {
  stages: [
    { duration: '20s', target: 100 },   // 짧은 시간 안에 100명
    { duration: '30s', target: 100 },   // 30초간 유지
    { duration: '10s', target: 0 },     // 종료
  ],
  thresholds: {
    http_req_failed: ['rate<0.05'],            // 에러율 5% 미만 (스파이크라 약간 여유)
    http_req_duration: ['p(95)<1000'],         // p95 < 1초
  },
};

export default function () {
  const payload = JSON.stringify({
    userId: TEST_USER,
    password: TEST_PASS,
  });

  const params = {
    headers: { 'Content-Type': 'application/json' },
    tags: { name: 'Login' },
  };

  const res = http.post(`${BASE_URL}/api/auth/login`, payload, params);
  loginLatency.add(res.timings.duration);

  const ok = check(res, {
    'status 200': (r) => r.status === 200,
    'has accessToken': (r) => {
      try {
        const body = r.json();
        return body.result && body.result.accessToken;
      } catch { return false; }
    },
  });
  errorRate.add(!ok);
}

export function handleSummary(data) {
  return {
    'load-tests/results/login-summary.json': JSON.stringify(data, null, 2),
    stdout: textSummary(data),
  };
}

function textSummary(data) {
  const fmt = (v) => (v == null ? 'N/A' : v.toFixed(1));
  const d = data.metrics.http_req_duration?.values || {};
  const lines = [];
  lines.push('');
  lines.push('===== Login Spike Test Summary =====');
  lines.push(`Total Logins: ${data.metrics.http_reqs?.values?.count ?? 0}`);
  lines.push(`Error Rate: ${((data.metrics.http_req_failed?.values?.rate ?? 0) * 100).toFixed(2)}%`);
  lines.push(`Avg Response: ${fmt(d.avg)}ms`);
  lines.push(`p95 Response: ${fmt(d['p(95)'])}ms`);
  lines.push('');
  lines.push('💡 BCrypt 비용이 보통 100~300ms 차지. p95가 그 이상이면 DB 또는 JVM 풀이 병목.');
  lines.push('');
  return lines.join('\n');
}
