package org.example.boardback.service.payment.gateway;

import lombok.Builder;

/**
 * == PG 응답을 추상화한 결과 객체 ==
 * : 결제 처리 결과를 담는 DTO (PG 응답의 요약본)
 *
 * - 모든 PG 에서 공통으로 사용하는 응답 형태
 *      > 서비스 레이어에서는 PG 별로 다른 응답 구조를 몰라도 해당 PaymentResult 만 보고 성공/실패 분기 처리 가능
 * */
@Builder
public record PaymentResult(
        boolean success, // 결제 성공 여부
        String paymentKey, // PG 가 발급한 결제 키(또는 내부 생성키)
        String failureCode, // 실패 코드(PG 에서 온 코드 또는 내부 코드)
        String failureMessage // 실패 상세 메시지
) {

}
