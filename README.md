# 🚗 Acci (액시) - AI 기반 교통사고 영상 분석 플랫폼

> **Acci**는 교통사고 영상을 AI가 분석하여  
> 과실비율, 분석 근거, 관련 판례를 제공하는 분쟁 심의 지원 플랫폼입니다.

---

## 📌 프로젝트 소개

교통사고 처리 과정에서 과실비율 산정은  
보험사, 법원, 당사자 간 해석 차이로 인해 분쟁이 발생하기 쉽습니다.

Acci는 블랙박스 영상을 기반으로 AI 분석을 수행하고,  
그 결과를 비동기 구조로 처리하여 사용자에게 제공합니다.

---

## 📱 서비스 화면

<p align="left">
  <img src="https://github.com/user-attachments/assets/875a5713-9579-412c-b5de-88d486774e98" width="900"/>
</p>

<p align="left">
  <img src="https://github.com/user-attachments/assets/28277ff1-9f63-4905-a52a-a979b6a338f1" width="900"/>
</p>

<p align="left">
  <img src="https://github.com/user-attachments/assets/1c121623-1fe6-4ea2-9693-87eb63f06dc8" width="900"/>
</p>

---

## 🎯 목표

- 🚘 **공정한 사고 처리**  
  → 주관적 판단이 아닌 AI 기반 분석

- ⚖️ **법적 분쟁 감소**  
  → 판례 기반 근거 제공

- 🧾 **정보 접근성 향상**  
  → 분석 결과 및 관련 정보 제공

---

## 🏗 시스템 아키텍처

<img width="2058" height="1218" alt="image" src="https://github.com/user-attachments/assets/ea7714bd-614a-4f04-ba96-781d5e7ba73b" />

---

## 🔄 분석 요청 흐름

영상 분석은 30~60초 이상 소요될 수 있어  
동기 처리 시 서버 스레드 블로킹 문제가 발생합니다.

이를 해결하기 위해 비동기 기반 Job 구조로 설계했습니다.

1. 사용자가 영상 업로드
2. Backend는 즉시 `202 ACCEPTED` 반환
3. Job ID 발급
4. AI Server에서 비동기 분석 수행
5. SSE를 통해 실시간 상태 전송
6. 분석 완료 후 결과 반환

---

### 📌 요청 흐름 다이어그램

<img width="2202" height="1052" alt="image" src="https://github.com/user-attachments/assets/ace4646f-4c2c-4c8e-bdb0-88854f36ebda" />

---

## ⚙️ 핵심 기술 설계

### 1️⃣ 비동기 Job 아키텍처 설계

- Executor 기반 Worker 처리
- `/analyze` → job_id 반환
- `/status/{job_id}` → 상태 조회
- `/result/{job_id}` → 결과 조회
- Timeout 및 최대 재시도 로직 구현

✔ 서버 스레드 블로킹 제거  
✔ 대용량 분석 환경 확장 가능 구조 확보  

---

### 2️⃣ SSE 기반 실시간 상태 알림

- SseEmitter 기반 이벤트 스트림
- 분석 시작 / 진행 / 완료 상태 실시간 전송
- Polling 제거로 네트워크 트래픽 감소

✔ 실시간 UX 개선  
✔ 불필요한 API 반복 호출 제거  

---

### 3️⃣ 책임 분리 기반 리팩토링

- Command / Query 서비스 분리
- AI 연동 로직 Worker 계층 분리
- S3 저장 로직 File Service 분리

✔ 서비스 간 결합도 감소  
✔ 테스트 및 유지보수 용이성 향상  

---

## 🛠 기술 스택

| 영역 | 기술 |
|------|------|
| Frontend | Next.js, TypeScript |
| Backend | Spring Boot |
| AI Server | Python |
| Storage | AWS S3 |
| Infra | Docker, AWS EC2 |
| Realtime | SSE |

---

## 📈 프로젝트 성과

- MVP 배포 및 실사용 환경 운영
- 비동기 기반 영상 분석 구조 안정화
- 외부 AI 서버 장애 상황을 고려한 안전 설계 적용
