# AerixDemo

![Untitled](AerixDemo%20d9090654197449e8b3ccb3f82bda88de/Untitled.png)

# Introduction

Aerix Demo는 수집된 센서정보를 클라이언트에게 시각적으로 보여주기 위함에 목적을 두고 있습니다.

현재는 BLE센서의 진동 데이터만 지원합니다. 외부로부터 접근이 가능한 영역이기 때문에 JWT 및 SSL을 적용하였습니다. 

진동데이터의 실시간정보, 기간별정보, FFT정보를 계산합니다. 

FFT는.. 검증단계에서 잘못되어 수정될것 같지만 사용자에게 제공가능한 API가 구현되어 있습니다.

고객사→설비→센서 단계로 구분지어 표현하므로 여러 고객사별로 각각의 설비에 대한 다양한 센서 정보를 그래프로 표현 가능합니다.

# Getting Started

## Step 1 — requirement

- `openjdk_version=11`
- `ktor_version=1.6.7`
- `kotlin_version=1.6.10`
- `logback_version=1.2.3`
- `h2_version=2.1.+`
- `psql_version=42.2.+`
- `hikari_version=5.0.+`
- `jackson_version=2.13.0`
- `tls_certificates_version=1.4.0`

## Step 2 — build & deploy

- 설치 및 배포에 대해서 참고하세요 [ArxDemo](https://www.notion.so/ArxDemo-b1f65586102f4649a2afd412de0bff79)

# API Reference

## /demo/login

### POST

id, pw에 대한 JWT를 반환합니다.

```bash
POST https://localhost:9443/demo/login
Content-Type: application/json

{
  "user_id": "test",
  "password": "1234"
}
```

## /authenticate

### GET

JWT 토큰에 대해 유효성을 검증합니다.

*현재 API 요청에 따른 JWT 검증은 적용하고 있지는 않습니다*

```bash
GET https://localhost:9443/authenticate
Content-Type: application/json
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImF1ZCI6Imh0dHA6Ly8xMjcuMC4wLjE6OTAwMC9oZWxsbyIsInBhc3N3b3JkIjoiMTIzNCIsInVzZXJfaWQiOiJ0ZXN0IiwiaXNzIjoiaHR0cDovLzEyNy4wLjAuMTo5MDAwLyIsImV4cCI6MTY1MDUyNTQxNH0.hPlPFW25HnZ3I1UuXjqLqHAls-Vd9QdqGB1Gt2Xt0mT4_InIouaw1OfJOv6RXZ6JrC_WeN4prhNPGK4aoPm6Yg
```

## /demo/renderdatas

### GET ?id={id}

화면 렌더링을 위해 현재부터 과거 일정 기간 동안의 그래프 데이터를 불러옵니다

```bash
GET https://localhost:9443/demo/renderdatas?id={id}
Content-Type: application/json

```

## /demo/datas

### GET ?isTrend={isTrend}&id={id}&start={start}&end={end}

startData = null or notnull 에 따라서 기간별 또는 실시간 데이터를 불러옵니다.

```bash
GET https://localhost:9443/demo/datas?isTrend={isTrend}&id={id}&start={start}&end={end}
Content-Type: application/json

```

## /facility

### GET

모든 시설 정보를 불러옵니다.

```bash
GET https://localhost:9443/facility
Content-Type: application/json

```

### GET /{id}

특정 ID에 해당하는 시설 정보를 불러옵니다.

```bash
GET https://localhost:9443/facility/{id}
Content-Type: application/json

```

### POST

시설 정보를 생성합니다.

```bash
POST https://localhost:9443/facility
Content-Type: application/json

{
  "name": "test",
  "workplace_id": "1"
}
```

## /demo

### GET

모든 센서정보를 불러옵니다

```bash
GET https://localhost:9443/demo
Content-Type: application/json

```

### GET /{id}

특정 ID에 해당하는 센서정보를 불러옵니다

```bash
GET https://localhost:9443/demo/{id}
Content-Type: application/json

```

### DELETE /sensor/{id}

특정 ID에 해당하는 센서를 삭제합니다

```bash
DELETE https://localhost:9443/demo/sensor/{id}
Content-Type: application/json

```

### PATCH /sensor

특정 ID에 해당하는 센서정보를 수정합니다

```bash
PATCH https://localhost:9443/demo/sensor/{id}
Content-Type: application/json

{
  "id": 1,
  "mac": "AA:BB:CC:DD:EE:FF",
  "name": "test",
  "model": "ARX123",
  "status": "test",
  "provider": "test",
  "placeId": 1,
  "min": 10,
  "max": 20
}
```

### POST /sensor

특정 ID에 해당하는 센서정보를 생성합니다.

```bash
POST https://localhost:9443/demo/sensor
Content-Type: application/json

{
  "mac": "AA:BB:CC:DD:EE:FF",
  "name": "test",
  "model": "ARX123",
  "provider": "test",
  "placeId": 1,
}
```

### GET /places

모든 사업장을 불러옵니다.

```bash
GET https://localhost:9443/demo/places
Content-Type: application/json

```

### PATCH /place

특정 ID에 해당하는 사업장정보를 수정합니다.

```bash
PATCH https://localhost:9443/demo/place
Content-Type: application/json

{
  "id": 1,
  "name": "example workplace A"
}
```

### DELETE /place/{id}

특정 ID에 해당하는 사업장정보를 삭제합니다.

```bash
DELETE https://localhost:9443/demo/places/{id}
Content-Type: application/json
```

### POST /place

사업장정보를 생성합니다.

```bash
POST https://localhost:9443/demo/place
Content-Type: application/json

{
  "name": "example workplace A"
}
```
