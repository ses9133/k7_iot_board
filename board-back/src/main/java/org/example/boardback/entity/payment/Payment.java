package org.example.boardback.entity.payment;

import jakarta.persistence.*;
import lombok.*;
import org.example.boardback.common.enums.payment.PaymentMethod;
import org.example.boardback.common.enums.payment.PaymentStatus;
import org.example.boardback.entity.base.BaseTimeEntity;
import org.example.boardback.entity.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 결제를 요청한 사용자

    // 주문 번호: 가맹점 서버에서 생성하는 고유 주문 ID(PG 결제 승인시 반드시 필요)
    @Column(nullable = false, length = 100)
    private String orderId;

    // PG 에서 발급한 결제 키
    // - Toss: paymentKey
    // - Kakao: tid(transaction id)
    @Column(nullable = false, length = 100, unique = true)
    private String paymentKey;

    // 결제 금액
    @Column(nullable = false)
    private Long amount;

    // 결제 방식 (MOCK / KAKAO_PAY / TOSS_PAY)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethod method;

    // 결제 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    // 상품 코드 / 이름
    @Column(nullable = false, length = 50)
    private String productCode;

    @Column(nullable = false, length = 100)
    private String productName;

    // - 결제 실패시 PG 에서 전달한 실패 코드 / 메시지
    @Column(length = 50)
    private String failureCode;

    @Column(length = 255)
    private String failureMessage;

    // 결제 요청 시각
    @CreationTimestamp // 해당 엔티티 생성시 현재 시각 주입
    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    // 결제 승인 시각
    private LocalDateTime approvedAt;
    // 결제 취소 / 환불 완료 시각
    private LocalDateTime cancelledAt;

    /**
     * == 결제 상태 변경 로직
     * */
    // 1) 결제 성공 처리
    public void markSuccess() {
        this.status = PaymentStatus.SUCCESS;
        this.approvedAt = LocalDateTime.now();
        this.failureCode = null;
        this.failureMessage = null;
    }

    // 2) 결제 실패 처리
    public void markFailed(String code, String message) {
        this.status = PaymentStatus.FAILED;
        this.failureCode = code;
        this.failureMessage = message;
    }

    // 3) 환불 완료 처리
    public void markRefunded() {
        this.status = PaymentStatus.REFUNDED;
        this.cancelledAt = LocalDateTime.now();
    }

}
