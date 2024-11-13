import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    { duration: '10s', target: 20 },
    { duration: '20s', target: 40 }, // max users are ~80
    { duration: '15s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['med<1500', 'p(95)<2000', 'p(99)<3000'],
    http_req_failed: ['rate<0.01'],
  },
};

export default function () {
  const username = __ENV.USERNAME;
  const password = __ENV.PASSWORD;
  const host = __ENV.BACKEND_URL || 'localhost';
  const port = __ENV.BACKEND_PORT || 8080;

  const response = http.post(`http://${host}:${port}/api/v1/auth/authenticate`,
    JSON.stringify({
      username: username,
      password: password
    }),
    { headers: { 'Content-Type': 'application/json' } },
  );
  check(response, {
    'status is 200': (r) => r.status === 200,
    'transaction time OK': (r) => r.timings.duration < 2000,
  });
  sleep(1);
}