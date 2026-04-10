# HAN-47 작업 정리

## 이슈

- 제목: `[보안] 로그인/회원가입 비밀번호 전송 암호화`
- 범위: 인증 관련 비밀번호 전송 보호 강화
- 작업 저장소: `book_read_management`

## 이번 작업에서 처리한 범위

- RSA 공개키 조회 API 추가
- 서버 테스트/Swagger 검증용 비밀번호 암호화 API 추가
- 로그인/회원가입/비밀번호 변경에서 암호화된 비밀번호 수용
- 기존 FE 호환을 위한 평문 비밀번호 병행 수용
- 설정값으로 평문 비밀번호 차단 가능하도록 구성
- RSA 복호화 동작 단위 테스트 추가

## 구현 내용

### 1. 공개키 조회 API 추가

- 엔드포인트: `GET /api/v1/auth/public-key`
- 목적: 클라이언트가 비밀번호 암호화에 사용할 RSA 공개키 제공
- 토큰 인증 없이 호출 가능
- 응답 공개키는 정식 PEM 형식으로 반환
  - `-----BEGIN PUBLIC KEY-----`
  - Base64 body with line breaks
  - `-----END PUBLIC KEY-----`

응답 필드:

- `keyId`: 키 식별자
- `algorithm`: 암호화 알고리즘
- `publicKey`: PEM 형식 공개키

응답 예시:

```json
{
  "success": true,
  "data": {
    "keyId": "primary",
    "algorithm": "RSA/ECB/OAEPWithSHA-256AndMGF1Padding",
    "publicKey": "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkq...\n-----END PUBLIC KEY-----"
  }
}
```

관련 파일:

- [`src/main/kotlin/com/liber/book_read_management/controller/AuthApiController.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/controller/AuthApiController.kt)
- [`src/main/kotlin/com/liber/book_read_management/dto/AuthPublicKeyResponse.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/dto/AuthPublicKeyResponse.kt)
- [`src/main/kotlin/com/liber/book_read_management/auth/AuthFilter.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/auth/AuthFilter.kt)

### 2. 서버 테스트용 암호화 API 추가

- 엔드포인트: `POST /api/v1/auth/encrypt-password`
- 목적: Swagger 또는 수동 테스트에서 공개키 암호화 결과를 빠르게 확인하기 위한 보조 API
- 토큰 인증 없이 호출 가능
- 실제 앱에서는 사용하지 않음

요청 예시:

```json
{
  "password": "password1234!"
}
```

응답 예시:

```json
{
  "success": true,
  "data": {
    "keyId": "primary",
    "algorithm": "RSA/ECB/OAEPWithSHA-256AndMGF1Padding",
    "encryptedPassword": "Base64..."
  }
}
```

관련 파일:

- [`src/main/kotlin/com/liber/book_read_management/controller/AuthApiController.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/controller/AuthApiController.kt)
- [`src/main/kotlin/com/liber/book_read_management/dto/EncryptPasswordRequest.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/dto/EncryptPasswordRequest.kt)
- [`src/main/kotlin/com/liber/book_read_management/dto/EncryptPasswordResponse.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/dto/EncryptPasswordResponse.kt)
- [`src/main/kotlin/com/liber/book_read_management/auth/AuthFilter.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/auth/AuthFilter.kt)

### 3. 인증 암호화 서비스 추가

신규 `AuthEncryptionService`를 추가했다.

주요 역할:

- RSA 공개키/개인키 로딩
- 공개키 응답을 PEM 형식으로 정규화
- 테스트용 비밀번호 암호화
- `encryptedPassword` 복호화
- 설정에 따라 평문 `password` 허용 또는 차단

관련 파일:

- [`src/main/kotlin/com/liber/book_read_management/service/AuthEncryptionService.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/service/AuthEncryptionService.kt)

### 4. 요청 DTO 확장

기존 `password` 필드 외에 `encryptedPassword` 필드를 추가했다.

적용 대상:

- 로그인
- 회원가입
- 비밀번호 변경

호환 정책:

- `encryptedPassword`가 있으면 우선 사용
- 없으면 `password` 사용
- 단, 설정으로 평문 비밀번호를 비활성화할 수 있음

관련 파일:

- [`src/main/kotlin/com/liber/book_read_management/dto/LoginRequest.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/dto/LoginRequest.kt)
- [`src/main/kotlin/com/liber/book_read_management/dto/SignUpRequest.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/dto/SignUpRequest.kt)
- [`src/main/kotlin/com/liber/book_read_management/dto/UpdatePasswordRequest.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/dto/UpdatePasswordRequest.kt)

### 5. 실제 인증 로직 반영

아래 로직에서 저장 또는 비교 전에 복호화된 비밀번호를 사용하도록 변경했다.

- `signUp`
- `login`
- `updatePassword`

관련 파일:

- [`src/main/kotlin/com/liber/book_read_management/service/UserServiceImpl.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/service/UserServiceImpl.kt)

## 설정 추가

`application.yml`에 아래 설정을 추가했다.

```yaml
auth:
  encryption:
    key-id: ${AUTH_ENCRYPTION_KEY_ID:primary}
    algorithm: ${AUTH_ENCRYPTION_ALGORITHM:RSA/ECB/OAEPWithSHA-256AndMGF1Padding}
    public-key: ${AUTH_ENCRYPTION_PUBLIC_KEY:}
    private-key: ${AUTH_ENCRYPTION_PRIVATE_KEY:}
    allow-plain-password: ${AUTH_ENCRYPTION_ALLOW_PLAIN_PASSWORD:true}
```

설명:

- `public-key`: 공개키 PEM
- `private-key`: 개인키 PEM
- `allow-plain-password`: `true`면 기존 평문 요청 허용, `false`면 암호화된 비밀번호만 허용

관련 파일:

- [`src/main/resources/application.yml`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/resources/application.yml)

GitHub Actions 배포 시크릿:

- `AUTH_ENCRYPTION_KEY_ID`
- `AUTH_ENCRYPTION_ALGORITHM`
- `AUTH_ENCRYPTION_PUBLIC_KEY`
- `AUTH_ENCRYPTION_PRIVATE_KEY`
- `AUTH_ENCRYPTION_ALLOW_PLAIN_PASSWORD`

워크플로우 반영 파일:

- [`.github/workflows/deploy.yml`](/Users/digitalmedic_hw/hwdev/book_read_management/.github/workflows/deploy.yml)

## FE 연동 방식

실제 앱 플로우:

1. 앱이 `GET /api/v1/auth/public-key` 호출
2. 응답으로 받은 공개키로 비밀번호를 RSA 암호화
3. 로그인/회원가입/비밀번호 변경 요청 시 `encryptedPassword` 전송
4. 서버는 개인키로 복호화한 뒤 기존 로직 수행

클라이언트 역할:

- `publicKey`만 알면 됨
- `privateKey`는 알면 안 됨
- 실제 앱에서는 서버의 `encrypt-password` API를 사용하지 않고, 클라이언트에서 직접 암호화해야 함

서버 테스트 플로우:

1. Swagger에서 `POST /api/v1/auth/encrypt-password` 호출
2. 응답의 `encryptedPassword`를 복사
3. 로그인/회원가입/비밀번호 변경 요청의 `encryptedPassword`에 사용
4. 서버가 복호화 후 기존 로직 수행

전환 순서 권장:

1. FE에서 `encryptedPassword` 연동
2. 운영 환경에 공개키/개인키 설정 배포
3. 충분한 호환 기간 확인
4. `AUTH_ENCRYPTION_ALLOW_PLAIN_PASSWORD=false` 적용

## 검증 결과

추가 테스트:

- 평문 비밀번호 허용 시 정상 처리
- RSA 암호문 복호화 성공
- 평문 비밀번호 차단 시 예외 발생

테스트 파일:

- [`src/test/kotlin/com/liber/book_read_management/service/AuthEncryptionServiceTest.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/test/kotlin/com/liber/book_read_management/service/AuthEncryptionServiceTest.kt)

실행 결과:

- `./gradlew test` 통과
- `./gradlew compileKotlin` 통과

## 이번 작업에 포함되지 않은 항목

이 저장소 범위 밖이라 이번 변경에는 포함하지 않았다.

- Flutter `.env`의 `BASE_URL=https://` 강제
- Android `network_security_config.xml` cleartext 차단
- iOS `Info.plist` ATS 설정 확인

## 추가로 보안 검토가 필요한 항목

비밀번호 외에도 민감도 높은 데이터가 있다.

- `accessToken`, `refreshToken`
- `Authorization` 헤더
- 이메일 인증 값
- `fcmToken`

특히 이메일 인증은 현재 쿼리스트링으로 `email`, `code`를 전달하고 있어 설계 개선이 필요하다.

관련 파일:

- [`src/main/kotlin/com/liber/book_read_management/service/EmailServiceImpl.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/service/EmailServiceImpl.kt)

권장 후속 작업:

- 이메일 인증 링크를 단일 일회용 토큰 방식으로 변경
- 운영/개발 환경 모두 HTTPS 강제
- 프록시/서버 로그에서 토큰, Authorization 헤더 마스킹 확인

## 요약

이번 작업으로 백엔드는 RSA 공개키 기반 비밀번호 암호화 전송을 수용할 수 있게 되었다.
기존 클라이언트와의 호환은 유지하면서, 설정만으로 평문 비밀번호 전송을 차단할 수 있는 상태까지 반영했다.
