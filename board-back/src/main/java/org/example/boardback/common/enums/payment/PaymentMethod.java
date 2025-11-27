package org.example.boardback.common.enums.payment;

/**
 * 결제 수단(PaymentMethod)
 *
 * MOCK: 실제 결제 연동없이 테스트/모의 결제를 위한 가짜 결제 방식
 * TOSS_PAY
 * KAKAO_PAY
 * */
public enum PaymentMethod {
    MOCK, TOSS_PAY, KAKAO_PAY
}
