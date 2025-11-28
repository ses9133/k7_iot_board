package org.example.boardback.service.payment.gateway;

import org.example.boardback.dto.payment.request.PaymentApproveRequestDto;
import org.example.boardback.dto.payment.request.PaymentCreateRequestDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * == 실제 PG 없이 항상 성공하는 모의 결제 ==
 * Mock 결제 게이트 웨이
 * - 실제 PG 와 통신하지 않고, 항상 성공한 것 처럼 처리하는 테스트용 구현체
 * */
@Component
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult approve(PaymentApproveRequestDto request, String userId) {
        // 항상 성공한다고 가정하는 모의 결제
        String paymentKey = "MOCK-" + UUID.randomUUID();

        return PaymentResult.ok(paymentKey);
    }
}
