package org.example.boardback.dto.payment.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaymentRefundRequestDto(
    // 환불할 금액
    @NotNull(message = "환불 금액은 필수입니다.")
    @Min(value = 1, message = "환불 금액은 1이상이어야합니다.")
    Long amount,

    // 환불 사유
    String reason
) { }
