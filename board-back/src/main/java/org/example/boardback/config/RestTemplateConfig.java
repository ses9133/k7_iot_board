package org.example.boardback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/*
 * === RestTemplateConfig ===
 * Rest: HTTP REST 요청을 보내는
 * Template: 도구에 대한
 * Config: 설정
 *
 * >> HTTP 요청을 보내는 도구(RestTemplate)를 스프링이 미리 만들어서
 *       , 전체 프로젝트 어디에서나 편하게 쓸 수 있도록 하는 설정 코드
 *
 * >> 결제 시스템에서 PG사(토스페이, 카카오페이, 카드사 API 등)에 요청 보낼 때 반드시 필요!
 * */
@Configuration
// 클래스 역할 명시: "설정 역할을 하는 클래스"
//                  >> 스프링 프로젝트 시작 시 해당 어노테이션의 클래스를 읽고
//                      , 내부의 @Bean 들을 찾아서 스프링 컨테이너(Bean 창고)에 등록
public class RestTemplateConfig {

    @Bean   // 해당 메서드가 리턴하는 객체를 스프링 컨테이너에 "Bean"으로 등록
    public RestTemplate restTemplate() {
        // RestTemplate 클래스
        // : HTTP 요청을 보내고 응답을 받는 클라이언트 도구
        //      EX) PG사 결제 API에 POST/GET 요청 보내기
        //      EX) 외부 서버로 주문 정보 전송하기 등
        return new RestTemplate();
    }
}

// ※ 프로젝트 어디서나 쓸 수 있는 RestTemplate 객체를 하나 만들어서, 스프링이 관리하게 등록하는 설정 클래스 ※