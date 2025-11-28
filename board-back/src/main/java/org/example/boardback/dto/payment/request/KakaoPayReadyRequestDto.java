package org.example.boardback.dto.payment.request;

public record KakaoPayReadyRequestDto(
        String itemName,
        Long amount,
        String orderId,
        String userId,
        String approvalUrl,
        String cancelUrl,
        String failUrl
) {}
