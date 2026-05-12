# HAN-50 작업 정리

## 이슈

- 제목: `[인증] Flutter 소셜 로그인 토큰 기반 백엔드 로그인 API 추가`
- 범위: Google / Kakao / Naver 소셜 로그인 토큰 검증 및 자동 로그인/회원가입
- 작업 저장소: `book_read_management`

## 이번 작업에서 처리한 범위

- 소셜 로그인 API 추가
- Google idToken 검증 로직 추가
- Kakao accessToken 사용자 정보 조회 로직 추가
- Naver accessToken 사용자 정보 조회 로직 추가
- 소셜 회원 자동 가입 처리
- 동일 이메일의 다른 provider 가입 충돌 시 409 응답 처리
- `USER` 엔티티에 소셜 provider/id 저장 필드 추가
- 인증 필터에서 소셜 로그인 API는 JWT 없이 API-Key만으로 호출 가능하도록 처리

## API 스펙

### 소셜 로그인

- Endpoint: `POST /api/v1/auth/social-login`
- Header:
  - `Content-Type: application/json`
  - `API-Key: {apiKey}`
- JWT `Authorization` 헤더는 필요하지 않음

요청 예시:

```json
{
  "provider": "GOOGLE",
  "token": "...",
  "platform": "IOS"
}
```

provider별 token:

- `GOOGLE`: `idToken`
- `KAKAO`: `accessToken`
- `NAVER`: `accessToken`

platform:

- `IOS`: iOS 앱에서 발급받은 소셜 토큰
- `AOS`: Android 앱에서 발급받은 소셜 토큰
- Google 로그인에서는 platform에 따라 검증할 OAuth client id를 선택한다.

성공 응답:

```json
{
  "status": "success",
  "data": {
    "accessToken": "...",
    "refreshToken": "..."
  }
}
```

다른 provider로 이미 가입된 이메일:

```json
{
  "code": "SOCIAL_PROVIDER_MISMATCH",
  "registeredProvider": "KAKAO",
  "message": "이미 카카오 계정으로 가입된 이메일입니다."
}
```

## 백엔드 처리 흐름

1. 클라이언트가 `provider`, `token`을 전송한다.
2. 백엔드가 provider별 방식으로 토큰을 검증하고 소셜 프로필을 조회한다.
3. 소셜 프로필에서 `email`, `name`, `socialId`를 추출한다.
4. `socialProvider + socialId`로 기존 회원을 먼저 조회한다.
5. 기존 소셜 회원이면 로그인 처리 후 토큰을 발급한다.
6. 소셜 회원이 없으면 이메일로 회원을 조회한다.
7. 같은 이메일이 다른 provider 또는 일반 계정으로 존재하면 409를 반환한다.
8. 회원이 없으면 자동 회원가입 후 토큰을 발급한다.

## Provider별 검증 방식

### Google

- 클라이언트가 전달한 `idToken`은 JWT이다.
- 백엔드는 Google JWK 공개키를 조회해 JWT 서명을 검증한다.
- 검증 후 payload에서 `email`, `name`, `sub`를 추출한다.
- `sub`를 `socialId`로 저장한다.
- `email_verified`가 true인 토큰만 인정한다.
- `issuer`는 Google issuer만 인정한다.
- `audience`는 요청 platform에 맞는 Google client id와 일치해야 한다.

`google client id`가 필요한 이유:

- idToken payload는 디코딩만 하면 읽을 수 있지만, 디코딩은 신뢰 검증이 아니다.
- 서명 검증은 "Google이 발급한 토큰"인지 확인한다.
- audience 검증은 "우리 서버용 client id 대상으로 발급된 토큰"인지 확인한다.
- `aud` 검증이 없으면 다른 Google OAuth client를 대상으로 발급된 idToken도 백엔드에서 받아들일 수 있다.
- Flutter Android에서 `serverClientId` 기준으로 idToken을 발급한다면, 백엔드는 같은 값을 `GOOGLE_AOS_CLIENT_ID` 또는 기존 호환 환경변수 `GOOGLE_SERVER_CLIENT_ID`로 설정해야 한다.
- iOS에서 iOS OAuth client id 기준으로 idToken이 발급된다면, 백엔드는 같은 값을 `GOOGLE_IOS_CLIENT_ID`로 설정해야 한다.

운영 설정:

```env
GOOGLE_AOS_CLIENT_ID=...
GOOGLE_IOS_CLIENT_ID=...
```

### Kakao

- 백엔드가 Kakao API를 직접 호출한다.
- 호출 API: `GET https://kapi.kakao.com/v2/user/me`
- Authorization: `Bearer {accessToken}`
- 응답에서 `id`, `kakao_account.email`, profile nickname을 추출한다.
- `id`를 `socialId`로 저장한다.

### Naver

- 백엔드가 Naver API를 직접 호출한다.
- 호출 API: `GET https://openapi.naver.com/v1/nid/me`
- Authorization: `Bearer {accessToken}`
- 응답에서 `response.id`, `response.email`, `response.name` 또는 `response.nickname`을 추출한다.
- `response.id`를 `socialId`로 저장한다.

## 신규 소셜 회원 처리 방식

이번 구현에서는 방법 A를 선택했다.

- 신규 소셜 회원은 자동 회원가입한다.
- 응답은 기존 로그인과 동일하게 200으로 반환한다.
- 요청에 닉네임이 없으므로 소셜 프로필의 name을 기반으로 닉네임을 생성한다.
- 닉네임이 중복되면 숫자 suffix를 붙여 중복을 피한다.

## DB 변경 필요 사항

`USER` 테이블에 소셜 로그인 식별 필드를 추가해야 한다.

```sql
ALTER TABLE `USER`
  ADD COLUMN `social_provider` VARCHAR(20) NULL,
  ADD COLUMN `social_id` VARCHAR(191) NULL,
  ADD INDEX `idx_user_social_provider_id` (`social_provider`, `social_id`);
```

저장 값:

- `social_provider`: `GOOGLE`, `KAKAO`, `NAVER`
- `social_id`: 각 provider의 고유 사용자 id

일반 회원은 `social_provider`, `social_id`가 null이다.
provider mismatch 응답에서는 일반 회원을 `LOCAL`로 처리한다.

## 구현 파일

- [`src/main/kotlin/com/liber/book_read_management/controller/AuthApiController.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/controller/AuthApiController.kt)
- [`src/main/kotlin/com/liber/book_read_management/service/UserService.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/service/UserService.kt)
- [`src/main/kotlin/com/liber/book_read_management/service/UserServiceImpl.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/service/UserServiceImpl.kt)
- [`src/main/kotlin/com/liber/book_read_management/service/SocialProfileService.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/service/SocialProfileService.kt)
- [`src/main/kotlin/com/liber/book_read_management/entities/User.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/entities/User.kt)
- [`src/main/kotlin/com/liber/book_read_management/repository/UserRepository.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/repository/UserRepository.kt)
- [`src/main/kotlin/com/liber/book_read_management/auth/AuthFilter.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/auth/AuthFilter.kt)
- [`src/main/kotlin/com/liber/book_read_management/exception/GlobalExceptionHandler.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/exception/GlobalExceptionHandler.kt)
- [`src/main/kotlin/com/liber/book_read_management/enums/SocialProvider.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/enums/SocialProvider.kt)
- [`src/main/kotlin/com/liber/book_read_management/dto/SocialLoginRequest.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/dto/SocialLoginRequest.kt)
- [`src/main/kotlin/com/liber/book_read_management/dto/SocialLoginResponse.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/dto/SocialLoginResponse.kt)
- [`src/main/kotlin/com/liber/book_read_management/dto/SocialProviderMismatchResponse.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/dto/SocialProviderMismatchResponse.kt)
- [`src/main/kotlin/com/liber/book_read_management/exception/SocialProviderMismatchException.kt`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/kotlin/com/liber/book_read_management/exception/SocialProviderMismatchException.kt)
- [`src/main/resources/application.yml`](/Users/digitalmedic_hw/hwdev/book_read_management/src/main/resources/application.yml)

## 검증 결과

```bash
./gradlew compileKotlin
./gradlew test
```

둘 다 성공했다.

## 남은 운영 확인 사항

- 운영/개발 환경에 `GOOGLE_AOS_CLIENT_ID`, `GOOGLE_IOS_CLIENT_ID` 설정 필요
- DB 스키마 반영 필요
- Kakao/Naver 앱 설정에서 이메일 제공 동의 항목 확인 필요
- 기존 일반 이메일 회원과 소셜 회원 간 정책을 제품 관점에서 확정 필요
