import http from 'k6/http';
import { check } from 'k6';

const API_BASE_URL = 'http://host.docker.internal:8080';

const validProducts = [
    { productId: 1, size: 270 },
    { productId: 2, size: 275 },
    { productId: 3, size: 260 },
    { productId: 4, size: 270 },
    { productId: 5, size: 265 },
    { productId: 6, size: 270 },
    { productId: 11, size: 270 },
];

export const options = {
    vus: 50,
    duration: '30s',
};

export default function () {
    const userId = Math.floor(Math.random() * 10) + 100;
    const product = validProducts[Math.floor(Math.random() * validProducts.length)];

    const orderPayload = JSON.stringify({
        userId: userId,
        couponCode: "WELCOME10",
        items: [
            {
                productId: product.productId,
                quantity: 1,
                size: product.size,
            },
        ],
    });

    const res = http.post(`${API_BASE_URL}/api/v1/orders`, orderPayload, {
        headers: {
            'Content-Type': 'application/json',
            'X-USER-ID': userId.toString(),
        },
    });

    check(res, {
        'order created': (r) => r.status === 200,
    });

}
