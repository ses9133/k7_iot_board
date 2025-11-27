package org.example.boardback.service.payment.gateway;

import org.example.boardback.dto.payment.request.PaymentCreateRequestDto;

/**
 *  == PG 연동을 추상화한 인터페이스 ==
 *  결제 게이트 웨이 공통 인터페이스
 *  - 실제 PG(토스, 카카오 등) 또는 모의 결제(MOCK) 같은 형태를 추상화
 *  - 새로운 결제 수단이 추가 되더라도 해당 결제 수단  PaymentService 가 해당 인터페이스 구현하면 됨
 * */
public interface PaymentGateway {
    /**
     * 실제 결제 수행 메서드
     *
     * - 요청 DTO 를 받아 PG API를 호출하고 그 결과를 PaymentResult 로 감싸서 반환
     * +) 우리 학습 단계에서는, 준비 + 승인 과정을 해당 메서드 내부에서 한 번에 처리
     *  (실제 프로덕션에서는 주로 1) 결제 준비, 2) 결제 승인 2단계로 나눔)
     * */
    PaymentResult pay(PaymentCreateRequestDto request);
}
