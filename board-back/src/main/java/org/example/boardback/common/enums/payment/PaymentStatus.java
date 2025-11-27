package org.example.boardback.common.enums.payment;

/**
 * 결제 상태(PaymentStatus)
 *
 * - 하나의 결제 엔티티가 현재 어떤 상태인지 표현
 *    : 결제의 전체 생명주기(Lift Cycle)를 나타냄
 *
 *  상태흐름예시)
 *      READY -> (PG 승인 성공) -> SUCCESS
 *      READY -> (PG 승인 실패) -> FAILED
 *      SUCCESS -> (사용자/관리자 취소) -> CANCELLED
 *      SUCCESS -> (환불 처리) -> REFUNDED
 *
 * */
public enum PaymentStatus {
    READY,
    SUCCESS,
    FAILED,
    CANCELED,
    REFUNDED
}
