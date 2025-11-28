package org.example.boardback.service.payment.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boardback.dto.payment.request.KakaoPayReadyRequestDto;
import org.example.boardback.dto.payment.request.PaymentApproveRequestDto;
import org.example.boardback.dto.payment.response.KakaoPayReadyResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoPayGateway implements PaymentGateway {

    private final RestTemplate restTemplate;

    @Value("${payment.kakao.admin-key}")
    private String adminKey;

    @Value("${payment.kakao.base-url}")
    private String baseUrl;

    /** 결제 준비 (카카오페이 only) */
    public KakaoPayReadyResponseDto ready(KakaoPayReadyRequestDto req) {
        System.out.println("abc");

        String url = baseUrl + "/v1/payment/ready";
        System.out.println("url:" + url);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + adminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("cid", "TC0ONETIME");
        body.add("partner_order_id", req.orderId());
        body.add("partner_user_id", req.userId());
        body.add("item_name", req.itemName());
        body.add("quantity", "1");
        body.add("total_amount", req.amount().toString());
        body.add("tax_free_amount", "0");
        body.add("approval_url", req.approvalUrl());
        body.add("cancel_url", req.cancelUrl());
        body.add("fail_url", req.failUrl());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(url, entity, KakaoPayReadyResponseDto.class);
    }

    /** 결제 승인 */
    @Override
    public PaymentResult approve(PaymentApproveRequestDto req, String userId) {

        try {
            String url = baseUrl + "/v1/payment/approve";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + adminKey);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("cid", "TC0ONETIME");
            body.add("tid", req.tid());
            body.add("partner_order_id", req.orderId());
            body.add("partner_user_id", userId); // 실제 유저 ID 입력
            body.add("pg_token", req.pgToken());

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

            String response = restTemplate.postForObject(url, entity, String.class);
            log.info("[KakaoPay] approve response={}", response);

            return PaymentResult.ok(req.tid());

        } catch (Exception e) {
            return PaymentResult.fail("KAKAOPAY_ERROR", e.getMessage());
        }
    }
}
