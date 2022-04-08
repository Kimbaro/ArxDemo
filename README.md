# ArxSimulator
BLE센서를 활용해 실시간 데이터를 조회할 수 있는 데모용 시뮬레이터 Backend-API 입니다.

## 필수설정요소
1. application.conf 에서 포트를 지정해야 합니다

## 구조
-
-
-

## 기능
1. Data 추가, 읽기, 편집, 삭제, 시뮬레이터 동작
2. Facility 추가, 읽기, 편집, 삭제
3. Sensor 추가, 읽기, 편집, 삭제
4. Workplace 추가, 읽기, 편집, 삭제
5.
6.

## 실행방법
``` shell
gradlew clean
gradlew build

cd /build/libs/
java -jar {프로젝트명}-all.jar
```

## 버전
1. openjdk 11.0.12

## 프레임워크
1. ktor_version=1.6.7
2. kotlin_version=1.6.10

## 툴
1. H2 DB
2. PostgresQL

## 라이브러리
1. logback_version=1.2.3
2. kotlin.code.style=official
3. exposed_version=0.37.+
4. h2_version=2.1.+
5. psql_version=42.2.+
6. hikari_version=5.0.+
7. log_version=1.2.+
8. jackson_version=2.13.0
9. ktorVersion=1.4.0




내용 작성중.