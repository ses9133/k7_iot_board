package org.example.boardback.dto.payment.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.boardback.common.enums.payment.PaymentMethod;

/**
 * === Mock + KakaoReady 요청용 DTO ===
 *
 * Mock 결제 → 이걸로 바로 결제 성공
 *
 * KakaoPay → ready 호출할 때 사용
 *
 * Toss → 사용하지 않음(FE 결제창에서 처리)
 * */
public record PaymentCreateRequestDto(
        @NotBlank(message = "상품 코드는 필수입니다.")
        String productCode,

        @NotBlank(message = "상품 이름은 필수입니다.")
        String productName,

        @NotNull(message = "결제 금액은 필수입니다.")
        @Min(value = 100, message = "최소 결제 금액은 100원입니다.")
        Long amount,

        @NotNull(message = "결제 수단은 필수입니다.")
        PaymentMethod method
) { }

