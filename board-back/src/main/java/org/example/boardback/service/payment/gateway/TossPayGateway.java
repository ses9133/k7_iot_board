package org.example.boardback.service.payment.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boardback.dto.payment.request.PaymentCreateRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * == Toss 결제 게이트 웨이 ==
 * : Toss Payment의 결제 승인 API를 호출하는 역할
 * */
@Component
@RequiredArgsConstructor
@Slf4j
public class TossPayGateway implements PaymentGateway {

    @Value("${payment.toss.secret-key}")
    private String secretKey;

    @Value("${payment.toss.base-url}")
    private String baseUrl;

    @Override
    public PaymentResult pay(PaymentCreateRequestDto request) {
        try {

        } catch (Exception e) {
            log.error("{TossPayGateway ERROR}, ", e);
            return PaymentResult.builder()
                    .success(false)
                    .failureCode("TOSS_ERROR")
                    .failureMessage(e.getMessage())
                    .build();
        }
        return null;
    }
}
