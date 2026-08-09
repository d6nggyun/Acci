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

## 문제 해결

### 1. AI 서버 장애가 전체 API로 번지는 문제

AI 분석 서버는 유일한 외부 의존성인데, WebClient `.timeout(30s)`로 호출하는 동안 Tomcat 스레드를 그대로 점유했습니다. <br>
장애가 누적되면 분석과 무관한 API까지 응답하지 못하는 cascade failure로 이어졌고, 일시 오류에도 재시도 없이 그냥 실패했습니다. <br>

Resilience4j 네 가지를 조합했습니다. <br>

- Retry — 3회, 500ms → 1000ms 지수 백오프, ±50% jitter
- CircuitBreaker — slidingWindow 10, failureRate 50%, slowCall 3s/80%, openWait 10s
- Bulkhead — maxConcurrentCalls 10, maxWait 0 (대기 없이 즉시 거절)
- Fallback — OPEN/포화 시 `AI_SERVER_CIRCUIT_OPEN(503)`, `AI_SERVER_BULKHEAD_FULL(429)`로 전환

fallback에서 `CallNotPermittedException` / `BulkheadFullException`을 원본 그대로 재throw하고 Retry의 `ignoreExceptions`에 등록해서, OPEN이나 포화 상태에서는 재시도 없이 바로 전파되게 했습니다. <br>

k6 + Toxiproxy + WireMock으로 세 가지 시나리오를 측정했습니다. <br>

| 시나리오 | 결과 |
| --- | --- |
| 일시 장애 (연결 드랍 30%) | 분당 성공 요청 16건 → 173건 |
| 완전 장애 (프록시 차단) | CLOSED → OPEN 전환 후 즉시 차단, failed_without_retry 35.4건/분 |
| 정상 | p99 응답시간 변화 없음 (AOP 데코레이터 오버헤드 측정 불가 수준) |

 <br>
 
### 2. 외부 LLM 호출로 인한 DB 커넥션 풀 고갈

분석 결과 조회 UseCase 전체가 하나의 `@Transactional`로 묶여 있었고, 그 안에서 Gemini 호출을 `Mono.block()`으로 동기 대기했습니다. <br>
외부 응답을 기다리는 최대 30초 동안 DB 커넥션이 반환되지 않아, 조회 요청이 쌓이면 커넥션 풀 10개가 금방 고갈됐습니다. <br>
운영 로그 스택 트레이스에서 `@Transactional` 메서드 안에 `Mono.block()`이 있는 걸 확인하고 원인을 특정했습니다. <br>

UseCase 전체를 감싸던 `@Transactional`을 걷어내고 트랜잭션 경계를 개별 DB 작업 단위로 좁혔습니다. <br>
LLM 호출은 트랜잭션 밖으로 빼고, 정합성이 필요한 저장 작업만 별도 트랜잭션으로 묶었습니다. <br>
RAG 수행 중 어떤 예외가 나더라도 진행 상태 락은 해제되도록 처리했습니다. <br>

커넥션 점유 시간이 최대 30초에서 ms 단위로 줄었고, LLM 지연이 다른 API로 전파되지 않게 됐습니다. <br>

 <br>

### 3. RAG 동시 요청 중복 수행

같은 analysis를 여러 사용자가 동시에 조회하면 각 요청이 독립적으로 RAG 파이프라인을 돌려서, embedding API 비용이 중복으로 나가고 결과 저장 시 race condition이 생겼습니다. <br>
엔티티에 요약 상태를 관리하는 필드가 없어 요약 전/진행 중/완료를 구분할 수 없던 게 원인이었습니다. <br>

`ragStatus` 필드를 추가하고 `NONE → IN_PROGRESS → DONE / FAILED` 상태머신을 정의했습니다. <br>
CAS 기반 업데이트로 NONE에서만 IN_PROGRESS 전환이 가능하게 해서 단 하나의 요청만 실제 수행 권한을 갖도록 했고, 나머지는 DB polling이나 저장된 결과 재사용으로 처리했습니다. <br>

동일 analysis에 대한 RAG 연산이 한 번만 수행되고, race condition도 사라졌습니다. <br>

 <br>

### 4. LLM 환각을 막기 위한 pgvector RAG 파이프라인

기존에는 AI 서버가 도출한 사고 유형만 LLM에 넘기고 응답을 그대로 신뢰했습니다. <br>
검증할 참조 문서가 없으니 법규·판례 부분에서 환각이 나올 수 있었고, 이건 "근거 기반 판단"이라는 서비스의 전제를 무너뜨리는 문제였습니다. <br>
그렇다고 전체 판례 PDF를 context에 넣기엔 토큰 한도와 비용이 걸렸습니다. <br>

- PostgreSQL + pgvector를 벡터DB로 채택
- PDFBox로 과실비율 분쟁심의위원회 교통사고 판례기준 PDF에서 텍스트 추출 → 의미 단위 chunking → Gemini Embedding으로 임베딩 생성 → `legal_chunks` 테이블 적재
- 질의 시 사고유형 필터링 + topK 유사도 검색으로 무관한 문서 노이즈 제거
- `accident_page_map.json`으로 사고유형 ↔ PDF 페이지 범위를 사전 매핑해, 불필요한 페이지는 인덱싱에서 제외

문서 근거가 붙으면서 환각 응답을 차단했고, 선택적 인덱싱으로 embedding 호출 비용과 처리 시간을 줄였습니다. <br>

 <br>

### 5. Hexagonal Architecture로 외부 의존성 분리

AI 서버, pgvector, MySQL 같은 인프라 의존성이 서비스 로직과 직접 붙어 있었습니다. <br>
Service 계층이 WebClient와 JPA Repository, 외부 SDK를 그대로 참조하다 보니 인프라를 건드릴 때마다 도메인 로직까지 같이 수정해야 했고, 테스트도 어려웠습니다. <br>
여러 관심사가 한 Service에 모이면서 UseCase 단위 책임 분리도 안 돼 있었습니다. <br>

외부 연동 지점을 Port 인터페이스(`AiClientPort`, `AnalysisEventPort` 등)로 추상화하고, WebClient 기반 AI 호출·SSE 이벤트 전송·DB 접근을 각각 Adapter로 분리했습니다. <br>
서비스 로직은 구현체가 아니라 Port에만 의존합니다. 동시에 한 Service에 몰려 있던 책임을 UseCase 단위로 나눴습니다. <br>

Mock Adapter로 단위 테스트를 작성할 수 있게 됐고, 벡터DB나 LLM Provider 교체가 어댑터 수준에서 끝나게 됐습니다. <br>

 <br>

## 부하 테스트 / 모니터링

`load-test/`에 k6 시나리오와 Toxiproxy, WireMock 구성이 있습니다.

```bash
docker compose -f docker-compose.load-test.yml up
```

`monitoring/`에 Prometheus와 Grafana 설정이 있습니다.

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



