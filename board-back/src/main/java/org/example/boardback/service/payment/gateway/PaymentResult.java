package org.example.boardback.service.payment.gateway;

import lombok.Builder;

@Builder
public record PaymentResult(
        boolean success,
        String paymentKey,
        String failureCode,
        String failureMessage
) {

    public static PaymentResult ok(String paymentKey) {
        return PaymentResult.builder()
                .success(true)
                .paymentKey(paymentKey)
                .build();
    }

    public static PaymentResult fail(String failureCode, String failureMessage) {
        return PaymentResult.builder()
                .success(false)
                .failureCode(failureCode)
                .failureMessage(failureMessage)
                .build();
    }
}

