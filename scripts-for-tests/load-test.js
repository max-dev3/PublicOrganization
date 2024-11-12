import http from 'k6/http';
import { sleep } from 'k6';

export let options = {
  stages: [
    { duration: '30s', target: 200 },
    { duration: '2m', target: 400 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(99)<2000'], // 99% of requests must complete below 2s
  },
};

export default function () {
  const username = __ENV.USERNAME;
  const password = __ENV.PASSWORD;
  const host = __ENV.BACKEND_URL || 'localhost';
  const port = __ENV.BACKEND_PORT || 8080;

  http.post(`http://${host}:${port}/api/v1/auth/authenticate`,
    JSON.stringify({
      username: username,
      password: password
    })
  )
  sleep(1);
}