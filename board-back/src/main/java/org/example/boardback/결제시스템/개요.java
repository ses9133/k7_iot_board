package org.example.boardback.결제시스템;

/**
 * == 결제 시스템 구현 ==
 * 스프링 부트 + JPA + 외부 PG(Payment Gateway) 연동
 *
 * cf) PG: 온라인 쇼핑몰에서 다양한 결제 수단(카드 결제, 계좌 이체 등)을 사용할 수 있도록 결제를 대신 처리해주는 결제 대행사
 *
 * ※ 결제 시스템 흐름
 * 사용자 -> 서버 -> PG(토스/카카오/기타) -> 서버 -> DB 구조
 *
 *  1) 사용자가 결제 버튼 클릭
 *  -> 서버로 요청 보냄(PaymentController.createPayment();)
 *
 *  2) 컨트롤러 -> 서비스 호출
 *   : 결제 가능한 사용자인지 확인, 결제 수단에 맞는 게이트 웨이 선택
 *   MOCK 서버 / KAKAO_PAY / TOSS_PAY
 *
 *  3) 선택된 PaymentGateway 에서 PG 결제 요청
 *    각 Gateway의 pay() 메서드 내부에서
 *    - 토스/카카오 REST API 호출 (RestTemplate 사용)
 *      > 응답 성공/실패에 따라 PaymentResult 생성
 *
 *  4) 서비스에서 Payment 엔티티 생성 (PaymentResult 기반으로)
 *
 *  5) 응답 DTO 생성후 클라이언트 반환
 *
 *  6) 결제 내역 조회
 *
 *  7) 환불 요청: 본인 소유 결제인지, 상태 SUCCESS 인지, 금액 유효한지 등을 확인 후 환불
 * */
public class 개요 {
}
