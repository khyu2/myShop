# Project - myShop


간단한 쇼핑몰 프로젝트


## 제작 기한

**2024-05-04 ~ ing**

**개발 환경**

- Java 17
- JDK 18
- IDE Intellij
- Spring boot 3.2.5

**기술 스택**

- Java
- Spring boot
- Spring Security, JWT
- Spring Data JPA
- Mysql

## 주요 기능

1. 로그인, 회원가입
    - JWT를 통한 토큰 관리, 사용자 정보 관리
    - Spring Security + Jwt
    - Authentication 관련 로직 처리 (Custom Filter 등)
2. 상품 관리 - 상품 수정, 등록, 삭제 등
3. 주문 관리 - 주문을 받고 관리할 수 있는 기능. 주문 취소 시 전부 취소되어야 함
4. 장바구니 - 사용자가 장바구니에 상품을 담고 나중에 결제할 수 있는 기능 추가
   - 모든 주문은 장바구니를 통해 관리된다
   - 상품 선택 시, 장바구니, 바로 구매 선택
   - 장바구니 선택 시, 선택한 상품 장바구니에 추가
   - 바로 구매 선택 시, 현재까지 장바구니에 담은 상품 + 현재 상품, 이후 결제 페이지로 이동
5. 결제 시스템 관리 - 결제 기능을 통해 사용자가 쇼핑한 상품 결제, 신용카드, 페이팔 또는 기타 결제 수단 지원
6. 쿠폰 할인 시스템 - 쿠폰별 최종 금액 다르게 산정하기
7. 리뷰 및 평가 - 사용자들이 리뷰를 작성하고 평가할 수 있는 기능 제공
8. 검색 및 필터 - 쉽게 찾을 수 있도록 검색 기능 제공, 필터링 기능도 추가하면 좋음
9. 알림 기능 - 판매자 입장에서 판매 알림, 구매자에게 특가 알림 등 구매자 입장에서 배송 절차에 따른 알림 등
10. View - thymeleaf, tailwind css 적용해보기

파일 구조
```text
├── MyShopApplication.java
├── domain
│   ├── coupon
│   │   ├── dto
│   │   │   └── CouponRequest.java
│   │   ├── entity
│   │   │   ├── Coupon.java
│   │   │   └── CouponStatus.java
│   │   └── repoAndService
│   │       ├── CouponRepository.java
│   │       └── CouponService.java
│   ├── member
│   │   ├── controller
│   │   │   └── AuthController.java
│   │   ├── dto
│   │   │   └── MemberDefaultDto.java
│   │   ├── entity
│   │   │   ├── Member.java
│   │   │   ├── MemberDetails.java
│   │   │   └── MemberStatus.java
│   │   ├── exception
│   │   │   ├── MemberException.java
│   │   │   └── MemberExceptionType.java
│   │   ├── repository
│   │   │   └── MemberRepository.java
│   │   └── service
│   │       ├── JwtService.java
│   │       ├── MemberDetailsService.java
│   │       └── MemberService.java
│   ├── order
│   │   ├── dto
│   │   │   ├── OrderProductRequest.java
│   │   │   └── OrderRequest.java
│   │   ├── entity
│   │   │   ├── Order.java
│   │   │   ├── OrderProduct.java
│   │   │   └── OrderStatus.java
│   │   ├── exception
│   │   │   ├── OrderException.java
│   │   │   └── OrderExceptionType.java
│   │   ├── repository
│   │   │   ├── OrderProductRepository.java
│   │   │   └── OrderRepository.java
│   │   └── service
│   │       ├── OrderProductService.java
│   │       └── OrderService.java
│   ├── payment
│   │   ├── dto
│   │   │   └── PaymentRequest.java
│   │   ├── entity
│   │   │   ├── Payment.java
│   │   │   ├── PaymentGateway.java
│   │   │   └── PaymentMethod.java
│   │   ├── repository
│   │   │   └── PaymentRepository.java
│   │   └── service
│   │       └── PaymentService.java
│   └── product
│       ├── dto
│       │   ├── ProductRequest.java
│       │   └── ProductResponse.java
│       ├── entity
│       │   ├── Cart.java
│       │   ├── Category.java
│       │   └── Product.java
│       ├── exception
│       │   ├── ProductException.java
│       │   └── ProductExceptionType.java
│       ├── repository
│       │   ├── CartRepository.java
│       │   └── ProductRepository.java
│       └── service
│           ├── CartService.java
│           └── ProductService.java
└── global
    ├── common
    │   ├── JsonUsernamePasswordAuthenticationFilter.java
    │   ├── JwtAuthenticationEntryPoint.java
    │   ├── JwtAuthenticationProcessingFilter.java
    │   ├── JwtLoginSuccessHandler.java
    │   ├── JwtLoginSuccessProviderHandler.java
    │   └── LoginFailureHandler.java
    ├── config
    │   ├── SecurityConfig.java
    │   └── SwaggerConfig.java
    └── exception
        ├── BaseException.java
        ├── BaseExceptionType.java
        └── ErrorResponse.java

34 directories, 56 files

```