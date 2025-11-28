package org.example.boardback.dto.payment.response;

import org.example.boardback.common.enums.payment.PaymentMethod;
import org.example.boardback.common.enums.payment.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResponseDto(
        Long id,
        String orderId,
        String paymentKey,
        Long amount,
        PaymentMethod method,
        PaymentStatus status,
        String productCode,
        String productName,
        Long userPointBalance,
        LocalDateTime requestedAt,
        LocalDateTime approvedAt
) { }
