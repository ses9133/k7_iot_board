package org.example.boardback.controller.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.boardback.common.apis.payment.PaymentApi;
import org.example.boardback.dto.ResponseDto;
import org.example.boardback.dto.payment.request.PaymentApproveRequestDto;
import org.example.boardback.dto.payment.request.PaymentCreateRequestDto;
import org.example.boardback.dto.payment.request.PaymentRefundRequestDto;
import org.example.boardback.dto.payment.response.PaymentResponseDto;
import org.example.boardback.security.user.UserPrincipal;
import org.example.boardback.service.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * === 결제 관련 요청(결제 생성, 내 결제 조회, 환불 요청)을 받아
 *      , 실제 일을 처리하는 PaymentService에게 전달하는 역할 ===
 *
 * >> 프론트엔드에서 백엔드로 요청이 들어오는 입구
 * */
@RestController                     // 해당 클래스가 API 요청을 받아서 JSON 형태로 응답을 하는 컨트롤러임을 명시
@RequiredArgsConstructor            // 필수 객체 자동 주입 (의존성 주입)
@RequestMapping(PaymentApi.ROOT)    // 컨트롤러 공통 URL prefix 명시
public class PaymentController {

    private final PaymentService paymentService; // 생성자 주입

    /** 1) create (Mock + KakaoReady) */
    @PostMapping
    public ResponseEntity<ResponseDto<?>> createPayment(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody PaymentCreateRequestDto req
    ) {
        return ResponseEntity.ok(paymentService.createPayment(user, req));
    }

    /** 2) approve (Toss + KakaoPay + Mock 승인 완료 처리) */
    @PostMapping("/approve")
    public ResponseEntity<ResponseDto<?>> approvePayment(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody PaymentApproveRequestDto req
    ) {
        return ResponseEntity.ok(paymentService.approvePayment(user, req));
    }

    /** 내 결제 목록 조회 API: GET - api/v1/payments/me 요청이 오면 해당 메서드 실행 */
    @GetMapping(PaymentApi.MY_LIST)
    public ResponseEntity<ResponseDto<List<PaymentResponseDto>>> getMyPayments(
            @AuthenticationPrincipal UserPrincipal userPrincipal         // 로그인 된 사용자 정보
    ) {
        ResponseDto<List<PaymentResponseDto>> result = paymentService.getMyPayments(userPrincipal);
        return ResponseEntity.ok(result);
    }

    /** 결제 환불 API: POST - api/v1/payments/{paymentId}/refund 요청이 오면 해당 메서드 실행 */
    // +) 환불이라는 새로운 데이터가 생성되므로 POST 사용
    //      payment의 세부 데이터가 '변경'되는 것 보다 paymentRefund의 새로운 데이터 '생성'이 더 중요
    @PostMapping(PaymentApi.REFUND)
    public ResponseEntity<ResponseDto<Void>> refundPayment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,           // 로그인 된 사용자 정보
            @PathVariable Long paymentId,                                   // 특정 결제 Id
            @Valid @RequestBody PaymentRefundRequestDto request             // 환불 사유 등 요청 바디 데이터
    ) {
        ResponseDto<Void> result = paymentService.refundPayment(userPrincipal, paymentId, request);
        return ResponseEntity.ok(result);
    }
}

