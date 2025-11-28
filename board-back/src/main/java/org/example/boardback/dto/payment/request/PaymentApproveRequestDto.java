package org.example.boardback.dto.payment.request;

import jakarta.validation.constraints.NotNull;
import org.example.boardback.common.enums.payment.PaymentMethod;

/**
 * === 공통 승인 DTO ===
 * : Toss + Kakao + Mock 승인용 공통 DTO
 *
 * 1) Toss 승인 시 사용 필드
 *  paymentKey, orderId, amount, method=TOSS_PAY
 *
 * 2) Kakao 승인 시 사용 필드
 *  tid, pgToken, method=KAKAO_PAY
 *
 * 3) Mock 결제 시
 *  paymentKey만 있으면 동작
 * */
public record PaymentApproveRequestDto(

        // Toss / Mock 공통
        String paymentKey,          // Toss/Mock
        String orderId,             // Toss
        Long amount,                // Toss

        // Kakao 전용
        String tid,                 // KakaoPay 승인 시 필요
        String pgToken,             // KakaoPay 승인 시 필요

        // 공통
        @NotNull(message = "결제 수단은 필수입니다.")
        PaymentMethod method,

        // 결제 상품 정보
        String productCode,
        String productName
) { }