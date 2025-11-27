package org.example.boardback.service.payment.gateway;

import org.example.boardback.dto.payment.request.PaymentCreateRequestDto;
import org.springframework.stereotype.Component;

@Component
public class KakaoPayGateway implements PaymentGateway {
    @Override
    public PaymentResult pay(PaymentCreateRequestDto request) {
        return null;
    }
}
