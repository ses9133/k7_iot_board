package org.example.boardback.service.payment;

import lombok.RequiredArgsConstructor;
import org.example.boardback.common.enums.user.ErrorCode;
import org.example.boardback.common.enums.payment.PaymentMethod;
import org.example.boardback.common.enums.payment.PaymentStatus;
import org.example.boardback.common.enums.payment.RefundStatus;
import org.example.boardback.dto.ResponseDto;
import org.example.boardback.dto.payment.request.KakaoPayReadyRequestDto;
import org.example.boardback.dto.payment.request.PaymentApproveRequestDto;
import org.example.boardback.dto.payment.request.PaymentCreateRequestDto;
import org.example.boardback.dto.payment.request.PaymentRefundRequestDto;
import org.example.boardback.dto.payment.response.KakaoPayReadyResponseDto;
import org.example.boardback.dto.payment.response.PaymentResponseDto;
import org.example.boardback.entity.payment.Payment;
import org.example.boardback.entity.payment.PaymentRefund;
import org.example.boardback.entity.user.User;
import org.example.boardback.exception.BusinessException;
import org.example.boardback.repository.payment.PaymentRefundRepository;
import org.example.boardback.repository.payment.PaymentRepository;
import org.example.boardback.repository.user.UserRepository;
import org.example.boardback.security.user.UserPrincipal;
import org.example.boardback.service.payment.gateway.KakaoPayGateway;
import org.example.boardback.service.payment.gateway.PaymentGateway;
import org.example.boardback.service.payment.gateway.PaymentGatewayResolver;
import org.example.boardback.service.payment.gateway.PaymentResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentRefundRepository paymentRefundRepository;
    private final PaymentGatewayResolver gatewayResolver;
    private final UserRepository userRepository;

    private final KakaoPayGateway kakaoGateway; // ready() 호출 위해 필요

    private User getUser(UserPrincipal principal) {
        Long userId = principal.getId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    // ==========================================================
    // 1. 결제 준비 (create)
    // ==========================================================
    @Transactional
    public ResponseDto<?> createPayment(
            UserPrincipal principal,
            PaymentCreateRequestDto req
    ) {
        User user = getUser(principal);

        return switch (req.method()) {
            case MOCK -> ResponseDto.success(processMockPayment(user, req));
            case KAKAO_PAY -> ResponseDto.success(processKakaoReady(user, req));
            case TOSS_PAY -> throw new BusinessException(ErrorCode.INVALID_INPUT);
        };
    }

    /**
     * MOCK 결제 처리 로직
     */
    private PaymentResponseDto processMockPayment(User user, PaymentCreateRequestDto req) {

        Payment payment = Payment.builder()
                .user(user)
                .orderId("ORD-" + UUID.randomUUID())
                .paymentKey("MOCK-" + UUID.randomUUID())
                .amount(req.amount())
                .method(PaymentMethod.MOCK)
                .status(PaymentStatus.SUCCESS)
                .productCode(req.productCode())
                .productName(req.productName())
                .build();

        payment.markSuccess();
        user.addPoint(req.amount());

        paymentRepository.save(payment);

        return toDto(payment, user.getPointBalance());
    }

    /**
     * KakaoPay Ready 처리
     */
    private KakaoPayReadyResponseDto processKakaoReady(
            User user, PaymentCreateRequestDto req
    ) {

        String orderId = "ORD-" + UUID.randomUUID();

        String approvalUrl =
                "http://localhost:5173/pay/kakao/success"
                        + "?orderId=" + orderId
                        + "&productCode=" + req.productCode()
                        + "&productName=" + req.productName()
                        + "&amount=" + req.amount();

        // Ready API 요청 DTO
        KakaoPayReadyRequestDto readyReq = new KakaoPayReadyRequestDto(
                req.productName(),
                req.amount(),
                orderId,
                user.getId().toString(),
                approvalUrl,
                "http://localhost:5173/pay/kakao/cancel",
                "http://localhost:5173/pay/kakao/fail"
        );

        // 카카오 서버 Ready API 호출 → 응답(ResponseDto)
        KakaoPayReadyResponseDto response = kakaoGateway.ready(readyReq);

        return response;
    }


    // ==========================================================
    // 2. 결제 승인 (Toss, Kakao, Mock 승인)
    // ==========================================================
    @Transactional
    public ResponseDto<PaymentResponseDto> approvePayment(
            UserPrincipal principal,
            PaymentApproveRequestDto req
    ) {
        User user = getUser(principal);

        PaymentGateway gateway = gatewayResolver.resolve(req.method());
        PaymentResult result = gateway.approve(req, principal.getId().toString());


        Payment payment = Payment.builder()
                .user(user)
                .orderId(req.orderId())
                .paymentKey(result.paymentKey())
                .amount(req.amount())
                .method(req.method())
                .status(result.success() ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .productCode(req.productCode())
                .productName(req.productName())
                .failureCode(result.failureCode())
                .failureMessage(result.failureMessage())
                .build();

        if (result.success()) {
            payment.markSuccess();
            user.addPoint(req.amount());
        }

        paymentRepository.save(payment);

        return ResponseDto.success(toDto(payment, user.getPointBalance()));
    }

    @Transactional(readOnly = true)
    public ResponseDto<List<PaymentResponseDto>> getMyPayments(UserPrincipal userPrincipal) {

        User user = getUser(userPrincipal);

        List<Payment> payments = paymentRepository.findByUserOrderByCreatedAtDesc(user);

        List<PaymentResponseDto> result = payments.stream()
                .map(payment -> new PaymentResponseDto(
                        payment.getId(),
                        payment.getOrderId(),
                        payment.getPaymentKey(),
                        payment.getAmount(),
                        payment.getMethod(),
                        payment.getStatus(),
                        payment.getProductCode(),
                        payment.getProductName(),
                        user.getPointBalance(),
                        payment.getRequestedAt(),
                        payment.getApprovedAt()
                ))
                .toList();

        return ResponseDto.success(result);
    }


    // ==========================================================
    // 3. 환불 기능
    // ==========================================================
    @Transactional
    public ResponseDto<Void> refundPayment(
            UserPrincipal principal,
            Long paymentId,
            PaymentRefundRequestDto req
    ) {
        User user = getUser(principal);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        if (!payment.getUser().getId().equals(user.getId()))
            throw new BusinessException(ErrorCode.INVALID_INPUT);

        if (payment.getStatus() != PaymentStatus.SUCCESS)
            throw new BusinessException(ErrorCode.PAYMENT_REFUND_NOT_ALLOWED);

        if (req.amount() <= 0 || req.amount() > payment.getAmount())
            throw new BusinessException(ErrorCode.PAYMENT_REFUND_AMOUNT_INVALID);

        if (user.getPointBalance() < req.amount())
            throw new BusinessException(ErrorCode.INSUFFICIENT_POINT_BALANCE);

        PaymentRefund refund = PaymentRefund.builder()
                .payment(payment)
                .amount(req.amount())
                .reason(req.reason())
                .status(RefundStatus.REQUESTED)
                .requestedAt(LocalDateTime.now())
                .build();

        refund.markCompleted();
        payment.markRefunded();
        user.subtractPoint(req.amount());

        paymentRefundRepository.save(refund);
        paymentRepository.save(payment);

        return ResponseDto.success(null);
    }


    // ==========================================================
    // 공통 DTO 변환
    // ==========================================================
    private PaymentResponseDto toDto(Payment p, Long pointBalance) {
        return new PaymentResponseDto(
                p.getId(),
                p.getOrderId(),
                p.getPaymentKey(),
                p.getAmount(),
                p.getMethod(),
                p.getStatus(),
                p.getProductCode(),
                p.getProductName(),
                pointBalance,
                p.getRequestedAt(),
                p.getApprovedAt()
        );
    }

}
