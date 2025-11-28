package org.example.boardback.dto.payment.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// 환불 요청 DTO
public record PaymentRefundRequestDto(
        @NotNull(message = "환불 금액은 필수입니다.")
        @Min(value = 1, message = "환불 금액은 1 이상이어야 합니다.")
        Long amount,
        String reason
) { }
