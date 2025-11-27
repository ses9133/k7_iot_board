package org.example.boardback.common.apis.payment;

import org.example.boardback.common.apis.ApiBase;

// 결제 관련 API 루트 경로
public class PaymentApi {
    String ROOT = ApiBase.BASE + "/payments";

    // 결제 생성: POST - /api/v1/payments
//    String CREATE = ROOT;

    // 내 결제 목록: GET - /api/v1/payments/me
    String MY_LIST = "/me";

    // 결제 환불 요청: POST - /api/v1/payments/{paymentId}/refund
    String REFUND = "/{paymentId}/refund";
}
