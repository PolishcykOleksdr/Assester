import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
    stages: [
        { duration: '10s', target: 50 },
        { duration: '30s', target: 1000 },
        { duration: '10s', target: 10000 },
        { duration: '10s', target: 0 },
    ],
};

export default function () {
    const res = http.get('http://localhost:8080/catalog/courses');

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(1);
}