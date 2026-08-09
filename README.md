# Acci

블랙박스 영상을 올리면 AI가 사고 상황을 분석해 과실비율과 근거, 관련 판례를 돌려주는 서비스입니다. <br>
차량 파손 3D 모델을 기반으로 수리비 견적을 예측하는 기능도 함께 제공합니다.

|  |  |
| --- | --- |
| 기간 | 2025.10 – 2026.05 |
| 구성 | 백엔드 2, 프론트 2, AI 1 |
| 담당 | 백엔드: 영상 업로드부터 사고 분석, 결과 반환, RAG 기반 판례/법규 요약까지의 분석 플로우 전 구간 |
| 비고 | 인천대학교 컴퓨터공학부 캡스톤디자인 대상 |

 <br>

## 기술 스택

| 영역 | 사용 기술 |
| --- | --- |
| Backend | Java 17, Spring Boot 3.5 |
| Database | MySQL, PostgreSQL + pgvector |
| RAG | Gemini Embedding, PDFBox |
| 안정성 | Resilience4j (Retry / CircuitBreaker / Bulkhead) |
| 테스트 | JUnit 5, k6, Toxiproxy, WireMock |
| 모니터링 | Actuator, Micrometer, Prometheus, Grafana |
| Infra | Docker, AWS EC2, S3, Caddy |

 <br>

## 아키텍처

<img width="2058" height="1218" alt="image" src="https://github.com/user-attachments/assets/ea7714bd-614a-4f04-ba96-781d5e7ba73b" />

Spring Boot 애플리케이션이 AI 서버와 벡터DB(pgvector), S3, MySQL을 각각 어댑터를 통해 호출합니다. <br>
Caddy를 리버스 프록시로 두고, GitHub Actions로 EC2에 배포합니다.

 <br>

## 분석 요청 처리 흐름

영상 분석은 30초 이상 걸립니다. 동기로 처리하면 그동안 Tomcat 스레드가 묶여 있어, 분석 요청이 몰리면 로그인·조회 같은 무관한 API까지 밀립니다. <br>
그래서 요청 접수와 분석 수행을 분리했습니다.

1. 영상 업로드 → S3 저장
2. Job 생성 후 `202 Accepted` + Job ID 즉시 반환
3. AI 서버에 분석 요청
4. 클라이언트는 Job ID로 SSE 구독, 진행 상태 수신
5. 완료 시 결과 저장 후 SSE로 전달

 <br>

## 화면

<p align="left">
  <img src="https://github.com/user-attachments/assets/875a5713-9579-412c-b5de-88d486774e98" width="900"/>
</p>

<p align="left">
  <img src="https://github.com/user-attachments/assets/28277ff1-9f63-4905-a52a-a979b6a338f1" width="900"/>
</p>

<p align="left">
  <img src="https://github.com/user-attachments/assets/1c121623-1fe6-4ea2-9693-87eb63f06dc8" width="900"/>
</p>



