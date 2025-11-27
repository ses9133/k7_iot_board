package org.example.boardback.service.payment.gateway;

import lombok.RequiredArgsConstructor;
import org.example.boardback.common.enums.payment.PaymentMethod;
import org.springframework.stereotype.Component;

/**
 * == 결제 수단에 맞는 Gateway 를 선택 ===
 * : 결제 수단에 따라 적절한 PaymentGateway 구현체를 반환하는 Resolver
 *
 * - PaymentService는 어떤 PG 를 사용할 지 알 필요 없이  단순히 method만 넘기면 Resolver가 알맞는 Gateway를 반환함
 * */
@Component
@RequiredArgsConstructor
public class PaymentGatewayResolver {

    private final MockPaymentGateway mockPaymentGateway;
    private final TossPayGateway tossPayGateway;
    private final KakaoPayGateway kakaoPayGateway;

    // 결제 수단에 따라 대응되는 gateway 구현체 반환
    public PaymentGateway resolve(PaymentMethod method) {
        return switch (method) {
            case MOCK -> mockPaymentGateway;
            case TOSS_PAY -> tossPayGateway;
            case KAKAO_PAY -> kakaoPayGateway;
        };
    }
}
