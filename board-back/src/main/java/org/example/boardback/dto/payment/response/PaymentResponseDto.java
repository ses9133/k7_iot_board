package org.example.boardback.dto.payment.response;

import org.example.boardback.common.enums.payment.PaymentMethod;
import org.example.boardback.common.enums.payment.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResponseDto(
        Long id,
        String orderId,
        String paymentKey, // PG 에서 승인시 필요한 KEY
        Long amount,
        PaymentMethod method,
        PaymentStatus status,
        String productCode,
        String productName,
        Long userPointBalance, // 결제 후 포인트 잔액
        LocalDateTime requestedAt,
        LocalDateTime updatedAt
) { }
