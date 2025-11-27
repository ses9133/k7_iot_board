package org.example.boardback.dto.payment.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.boardback.common.enums.payment.PaymentMethod;

public record PaymentCreateRequestDto(
        // PG 에 전달할 상품코드
        @NotBlank(message = "상품 코드는 필수입니다.")
        String productCode,

        // 결제화면에 표시될 상품 이름
        @NotBlank(message = "상품 이름은 필수입니다.")
        String productName,

        // 결제 금액
        @NotNull(message = "결제 금액은 필수입니다.")
        @Min(value = 100, message = "최소 결제 금액은 100원입니다.")
        Long amount,

        @NotNull(message = "결제 수단은 필수입니다.")
        PaymentMethod Method,

        // 결제 ID값- PG사에 제공
        @NotBlank(message = "결제 키값은 필수입니다.")
        String paymentKey,

        @NotBlank(message = "주문 Id는 필수입니다.")
        String orderId
) {
}
