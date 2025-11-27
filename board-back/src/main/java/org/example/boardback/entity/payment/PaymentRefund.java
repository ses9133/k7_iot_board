package org.example.boardback.entity.payment;

import jakarta.persistence.*;
import lombok.*;
import org.example.boardback.common.enums.payment.RefundStatus;
import org.example.boardback.entity.base.BaseTimeEntity;

import java.time.LocalDateTime;

// PaymentRefund: 환불 요청과 처리 결과를 저장하는 엔티티
// - Payment 와 1:N 관계(하나의 결제에 여러 개의 부분 환불 가능)
@Entity
@Table(name = "payment_refunds")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PaymentRefund extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    // 어떤 결제건에서 환불 되었는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    // 환불 금액
    @Column(nullable = false)
    private Long amount;

    // 환불 사유(선택)
    @Column(length = 255)
    private String reason;

    // 환불 상태(REQUESTED -> COMPLETED || FAILED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RefundStatus status;

    // 환불 실패 시 - 실패 정보 저장(code/message)
    @Column(length = 50)
    private String failureCode;

    @Column(length = 255)
    private String failureMessage;

    // 환불 요청/완료 시간
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;

    /**
     * == 환불 상태 변경 로직 ==
     * */
    public void markCompleted() {
        this.status = RefundStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.failureCode = null;
        this.failureMessage = null;
    }

    public void markFailed(String code, String message) {
        this.status = RefundStatus.FAILED;
        this.failureCode = code;
        this.failureMessage = message;
    }
}
