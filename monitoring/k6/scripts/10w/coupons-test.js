import http from 'k6/http';
import { check } from 'k6';

const API_BASE_URL = 'http://host.docker.internal:8080';

export const options = {
  vus: 50,               // 동시 가상 사용자 수
  duration: '30s',       // 총 테스트 시간
};

export default function () {
  const userId = Math.floor(Math.random() * 1000) + 1;  // userId 1~1000 랜덤
  const payload = JSON.stringify({
    userId: userId,
    couponCode: "WELCOME10",
  });

  const res = http.post(`${API_BASE_URL}/api/v1/coupons/limited-issue/async`, payload, {
    headers: {
      'Content-Type': 'application/json',
      'X-USER-ID': userId.toString(),
    },
  });

  check(res, {
    'status is 202': (r) => r.status === 202,
    'request body echoed or accepted': (r) => !!r.body,
  });
}
