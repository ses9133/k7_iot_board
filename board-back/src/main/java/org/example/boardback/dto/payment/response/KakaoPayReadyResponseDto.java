package org.example.boardback.dto.payment.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoPayReadyResponseDto(

        String tid,

        @JsonProperty("next_redirect_pc_url")
        String nextRedirectPcUrl,

        @JsonProperty("next_redirect_mobile_url")
        String nextRedirectMobileUrl,

        @JsonProperty("next_redirect_app_url")
        String nextRedirectAppUrl,

        @JsonProperty("created_at")
        String createdAt
) {}