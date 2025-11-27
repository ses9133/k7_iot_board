package org.example.boardback.common.enums.payment;

/**
 * 환불 상태(Refund Status)
 *
 * - PaymentStatus가 REFUNDED 로 바뀌기까지의 환불 프로세스를 세분화하여 관리하는 상태값
 *
 * 상태 흐름 예시)
 *  REQUESTED -> (PG 환불 API 호출 시도) -> COMPLETED OR FAILED
 * */
public enum RefundStatus {
    REQUESTED, // 환불 요청 들어옴
    COMPLETED, // 환불 요청 완료
    FAILED // 환불 실패(ex. 환불 가능 기간 지남 등...)
}
