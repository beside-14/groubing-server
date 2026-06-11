# Design: 회원 식별을 email → loginId로 전환

> 작성일: 2026-06-07
> 상태: Draft

---

## 1. 설계 개요 / 목표 / 범위

### 1.1 개요
출시 초기 가입 마찰을 줄이기 위해 **이메일 인증 / 이메일 로그인을 제거**하고, 일반(CLASSIC) 회원을 **아이디(`loginId`) + 비밀번호** 기반으로 가입/로그인하도록 전환한다. `email` 컬럼·도메인 필드·DTO·아이디찾기(`find-email`) API를 전면 제거하고, 소셜 코드에서도 email 파라미터를 제거한다(소셜 식별은 `socialId + socialType`이라 무관).

### 1.2 목표
- 일반 회원 식별 키를 `email` → `loginId`로 교체.
- `Member.loginId`는 **nullable + 값이 있을 때만 unique**(소셜 회원은 loginId 없음).
- `email` 흔적을 백엔드/프론트에서 완전 제거.
- 운영(prod, `ddl-auto=none`)·로컬(H2 create + `data-local.sql`) DB 마이그레이션 동반.

### 1.3 범위 (포함)
- 백엔드: member/auth 도메인·인프라·웹 레이어의 email → loginId 전환 및 email 제거.
- 백엔드: `find-email`(아이디찾기) API/Service/Request/Response 삭제, SecurityConfig 화이트리스트 정리.
- 백엔드: friend/blockedmember 프로젝션·응답에서 email 컬럼 셀렉트 제거(MemberEntity.email 컬럼이 사라지므로 강제 변경분).
- 백엔드: 영향받는 테스트(ApiTest/Service/Persistence/Fixtures) + REST Docs(.adoc, Vocabulary) 정리.
- 프론트(Flutter): login/signup 화면 email → loginId, 아이디찾기/비번찾기 화면·라우트 제거(숨김), `auth_api.dart` 요청 바디 전환 및 `find-email` 호출 제거.
- DB: local `data-local.sql` 수정 + prod 수동 DDL(컬럼 DROP/ADD + unique index).

### 1.4 범위 (제외)
- **계정 복구(아이디/비번 찾기) v1 제거** — 휴대폰/이메일 기반 복구는 앱 성장 후 별도 과제.
- **소셜 로그인 정식화 보류** — `socialId` 기반 로직은 보존하되 email 파라미터만 제거. 프론트 SNS 버튼은 현행 "준비중" 유지(노출 X), v1 정식 동작 안 함.
- **푸시/FCM, 회원 탈퇴 로직** — 변경 없음(탈퇴는 Apple 5.1.1(v) 요건 충족 위해 그대로 유지).
- Google Play 웹 계정삭제 URL — 출시 전 TODO로 8절에만 명시.

---

## 2. 확정 결정 표

| # | 항목 | 확정값 |
|---|------|--------|
| 1 | 식별/로그인 키 | 일반 회원: `loginId` + `password`로 가입/로그인 (email 로그인 제거) |
| 2 | `loginId` 제약 | **nullable + 값이 있을 때만 unique**. 소셜 회원은 `socialId+socialType` 식별이라 loginId=null. NOT NULL/빈문자열("") 금지 |
| 3 | email 제거 범위 | email 컬럼·도메인 필드·DTO·`find-email` API/Service/Request/Response, 소셜 코드의 email 파라미터(`SocialInfo.email`, `SocialLoginCommand/Request.email`, `NewMember.email`) 전부 삭제 |
| 4 | 계정 복구 v1 | 제거. find-email 백엔드 제거 + 프론트 아이디찾기/비번찾기 제거(숨김). **로그인 후 설정>비밀번호 변경(`PATCH /api/members/{id}/password`, 기존 비번 요구)은 유지** |
| 5 | 소셜 로그인 | socialId 기반 로직 보존, email 파라미터만 제거. 프론트 SNS 버튼은 "준비중" 유지(노출 X) |
| 6 | 회원 탈퇴 | 유지 (Apple 인앱 계정삭제 요건). Google Play 웹 삭제 URL은 별도 TODO |
| 7 | DDL | prod 수동 DDL(`EMAIL` DROP + `LOGIN_ID` ADD nullable + unique index). local은 H2 create + `data-local.sql` 동시 수정 |

---

## 3. 아키텍처

### 3.1 영향 레이어 흐름 (회원가입/로그인 예시)

```
┌────────────────────────────────────────────────────────────────────┐
│ boot/gb-boot-web                                                     │
│  SignUpApi / LoginApi                                                │
│    Request(loginId, password[, nickname]) → command() → Service     │
│    MemberResponse.of(authenticatedMember)  // email 필드 제거         │
│  MemberEmailFindApi  ─────────────────────────────────── [삭제]      │
│  SecurityConfig POST 화이트리스트에서 /api/members/find-email 제거    │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ (command / port)
┌──────────────────────────────▼──────────────────────────────────────┐
│ domain/gb-domain-core                                                │
│  SignUpService → SignUpValidator(loginId 중복검사)                    │
│  LoginService  → MemberQueryRepository.findOneByLoginId(loginId)     │
│  MemberEmailFindService ──────────────────────────────── [삭제]      │
│  Member(loginId: String?), NewMember(loginId: String?)              │
│  SocialInfo(email 제거), SocialLoginCommand(email 제거)             │
│  port: MemberQueryRepository (findByEmail* → findByLoginId*)        │
└──────────────────────────────┬──────────────────────────────────────┘
                               │ (implements)
┌──────────────────────────────▼──────────────────────────────────────┐
│ infrastructure/storage/gb-db-core                                   │
│  MemberEntity(EMAIL 컬럼 삭제 → LOGIN_ID 컬럼 nullable+unique)       │
│  MemberJpaRepository(findByEmail* → findByLoginId*)                 │
│  MemberRepositoryAdapter(findByEmail* 구현 → findByLoginId*)        │
│  SocialInfoEntity(email 컬럼 삭제)                                   │
│  FriendFindDao / BlockedMemberFindDao (memberEntity.email 셀렉트 제거)│
└──────────────────────────────────────────────────────────────────────┘
```

### 3.2 식별 키 의미 변화

- **CLASSIC 회원**: `loginId`(NOT NULL 값) + password로 식별. 기존 `findByEmailAndMemberType(email, CLASSIC)` → `findOneByLoginId(loginId)` (또는 `findOneByLoginIdAndMemberType`).
- **SOCIAL 회원**: 기존과 동일하게 `socialId + socialType`으로 식별. `loginId = null`, `password = ""`, `nickname = ""` 빈 채로 생성되는 현 패턴 유지.

---

## 4. 백엔드 변경 — 파일별

> 레이어 순서: domain-core → gb-db-core → gb-boot-web → 테스트/문서.
> email → loginId **변환 규칙 요약**:
> - `email: String?`(Member/NewMember) → `loginId: String?` (nullable 유지: 소셜 회원 null).
> - `email: String`(CLASSIC 전용 Command/Request) → `loginId: String` (non-null, `@field:NotBlank`).
> - `@field:Email` 검증 → 제거 후 `@field:Pattern`/`@field:Length`(loginId 형식 규칙)로 대체.
> - `findByEmail*` / `existsByEmail` → `findOneByLoginId*` / `existsByLoginId`.
> - `maskEmail()`, `MemberEmailFind*`, `email` 응답 필드 → 전부 삭제.

### 4.1 도메인 (gb-domain-core)

#### 4.1.1 `domain/member/domain/Member.kt` — 수정
- 필드 `val email: String?` → `val loginId: String?` (nullable 유지).
- `fun maskEmail(): String` **삭제** (아이디찾기 응답 전용이었음, find-email 제거로 불필요).
- 나머지 `withX()` 메서드는 영향 없음.

#### 4.1.2 `domain/member/domain/NewMember.kt` — 수정
- `val email: String?` → `val loginId: String?` (소셜 회원 생성 시 null).

#### 4.1.3 `domain/member/domain/port/MemberQueryRepository.kt` — 수정
- `fun findByEmail(email: String): Member` **삭제** (find-email 전용).
- `fun findByEmailAndMemberType(email: String, memberType: MemberType): Member` → `fun findOneByLoginId(loginId: String): Member`.
  - 일반 로그인은 항상 CLASSIC이고 loginId가 unique이므로 memberType 파라미터 불필요. 단건·non-null 반환(미존재 시 어댑터가 `MemberInputException`).
  - 메서드명: 단건 조회 규칙(rule 13) `findOne` 적용.
- `fun existsByEmail(email: String): Boolean` → `fun existsByLoginId(loginId: String): Boolean`.
- `findById`, `findAll*`, `existsByNickname`, `count` 등은 변경 없음.

#### 4.1.4 `domain/member/domain/port/MemberCommandRepository.kt` — 변경 없음
- `save(newMember)` / `update(member)` 시그니처 유지(내부 필드만 loginId로 교체).

#### 4.1.5 `domain/auth/application/command/SignUpCommand.kt` — 수정
- `val email: String` → `val loginId: String` (CLASSIC 전용, non-null).
- `toNewMember(passwordEncryptor)`에서 `NewMember(email = email, ...)` → `NewMember(loginId = loginId, ...)`.

#### 4.1.6 `domain/auth/application/SignUpService.kt` — 수정
- 로직 흐름 유지. `catch (DataIntegrityViolationException)` 메시지 `"중복된 email / 닉네임 입니다."` → `"중복된 아이디 / 닉네임 입니다."`.

#### 4.1.7 `domain/auth/domain/SignUpValidator.kt` — 수정
- `validate(newMember)`:
  - `if (newMember.email != null && existsByEmail(newMember.email))` → `if (newMember.loginId != null && existsByLoginId(newMember.loginId))`.
  - 예외 메시지 `"이미 가입된 email 주소입니다."` → `"이미 사용 중인 아이디입니다."`.
  - 닉네임 중복 검사 블록은 유지.
  - **포인트**: 소셜 회원은 `loginId = null`이라 이 분기를 건너뛴다(현 email null 가드와 동일 구조).

#### 4.1.8 `domain/auth/application/command/LoginCommand.kt` — 수정
- `val email: String` → `val loginId: String`.

#### 4.1.9 `domain/auth/application/LoginService.kt` — 수정
- `memberQueryRepository.findByEmailAndMemberType(loginCommand.email, MemberType.CLASSIC)` → `memberQueryRepository.findOneByLoginId(loginCommand.loginId)`.
- `MemberType` import가 더 이상 필요 없으면 제거.

#### 4.1.10 `domain/auth/application/MemberEmailFindService.kt` — **삭제**
- 아이디찾기 v1 제거. (호출처 `MemberEmailFindApi`도 삭제 — 4.3.4)

#### 4.1.11 `domain/auth/application/MemberPasswordResetService.kt` — 변경 없음
- `reset(id, beforePassword, afterPassword)`는 이미 **memberId 기반**(email 무관). 설정>비번 변경은 그대로 유지(결정 #4).

#### 4.1.12 `domain/auth/domain/SocialInfo.kt` — 수정
- 생성자 `val email: String?` 필드 삭제.
- `create(socialId, email, socialType, memberId)` → `create(socialId, socialType, memberId)`.
- `of(id, socialId, email, socialType, memberId)` → `of(id, socialId, socialType, memberId)`.

#### 4.1.13 `domain/auth/application/command/SocialLoginCommand.kt` — 수정
- `val email: String?` 필드 삭제. (`id`, `socialType`, `fcmToken`만 유지)

#### 4.1.14 `domain/auth/application/SocialLoginService.kt` — 수정
- `findOrCreateSocialInfo`에서:
  - `NewMember(email = socialLoginCommand.email, ...)` → `NewMember(loginId = null, ...)` (소셜 회원은 loginId 없음).
  - `SocialInfo.create(socialId = ..., email = socialLoginCommand.email, ...)` → `SocialInfo.create(socialId = ..., socialType = ..., memberId = ...)`.

#### 4.1.15 `domain/auth/domain/port/SocialInfoRepository.kt` — 변경 없음
- `save`, `findBySocialIdAndSocialTypeOrNull` 시그니처 유지.

#### 4.1.16 friend/blockedmember 도메인 — email 필드 제거 (강제 변경분)
> `MemberEntity.email` 컬럼이 사라지므로 이 컬럼을 셀렉트/투영하던 타입들에서 email을 제거해야 한다. **loginId로 대체하지 않고 제거**(친구/차단 목록에 아이디 노출은 불필요).

- `domain/friend/domain/FriendMember.kt` — 수정: `val email: String?` 필드 삭제.
- `domain/blockedmember/domain/BlockedMemberTarget.kt` — 수정: `val email: String?` 필드 삭제.

### 4.2 인프라 (gb-db-core)

#### 4.2.1 `domain/member/entity/MemberEntity.kt` — 수정
- `@Column(name = "EMAIL", unique = true) val email: String?` → `@Column(name = "LOGIN_ID", unique = true) val loginId: String?`.
  - **포인트**: nullable + `unique = true` 조합. JPA/Hibernate가 생성하는 unique 제약은 표준 SQL상 **NULL 다중 허용**(소셜 회원 여러 명 loginId=null 충돌 없음). 단 prod는 수동 DDL이므로 6절에서 unique index를 nullable로 직접 생성.
- `companion object.from(newMember)`: `email = newMember.email` → `loginId = newMember.loginId`.
- `toDomain()`: `email = email` → `loginId = loginId`.
- `applyChanges(member)`: loginId는 가입 후 불변이므로 반영하지 않음(기존 email도 applyChanges 대상 아님 — 유지).

#### 4.2.2 `domain/member/repository/MemberJpaRepository.kt` — 수정
- `findByEmailAndActiveTrue(email)` **삭제** (find-email 어댑터 메서드 제거에 수반).
- `findByEmailAndMemberTypeAndActiveTrue(email, memberType)` → `findByLoginIdAndActiveTrue(loginId): MemberEntity?`.
  - loginId가 unique라 memberType 불필요. (필요 시 `findByLoginIdAndMemberTypeAndActiveTrue` 오버로드도 가능하나 단일 권장)
- `existsByEmailAndActiveTrue(email)` → `existsByLoginIdAndActiveTrue(loginId): Boolean`.

#### 4.2.3 `domain/member/repository/MemberRepositoryAdapter.kt` — 수정
- `findByEmail(email)` 구현 **삭제**.
- `findByEmailAndMemberType(email, memberType)` 구현 → `findOneByLoginId(loginId)` 구현:
  - `memberJpaRepository.findByLoginIdAndActiveTrue(loginId)?.toDomain() ?: throw MemberInputException("존재하지 않는 아이디 입니다.: $loginId")`.
- `existsByEmail(email)` 구현 → `existsByLoginId(loginId)` 구현 (`existsByLoginIdAndActiveTrue` 위임).
- `MemberType` import 불필요해지면 제거.

#### 4.2.4 `domain/auth/entity/SocialInfoEntity.kt` — 수정
- `val email: String?` 필드 삭제.
- `toDomain()`: `SocialInfo.of(id, socialId, socialType, memberId)` (email 인자 제거).
- `from(socialInfo)`: `SocialInfoEntity(socialId, socialType, memberId)` (email 인자 제거).
- **포인트**: prod에서 `SOCIAL_INFOS.EMAIL` 컬럼도 DROP 필요(6절).

#### 4.2.5 `domain/auth/repository/SocialInfoRepositoryAdapter.kt` — 변경 없음
- 위임 메서드만 사용, 시그니처 영향 없음.

#### 4.2.6 `domain/friend/dao/FriendFindDao.kt` — 수정
- 3개 쿼리(`findFriends`/`findAllReceivedBy`/`findAllSentBy`)의 `@QueryProjection` select 절에서 `memberEntity.email` 라인 삭제(각 select에 1줄씩 = 3곳).
- **포인트**: `QFriendMemberInfo` 생성자 시그니처가 바뀌므로 kapt 재생성 필요(4.2.7과 연동).

#### 4.2.7 `domain/friend/dao/FriendMemberInfo.kt` — 수정
- `@QueryProjection constructor`에서 `val email: String?` 파라미터 삭제.

#### 4.2.8 `domain/friend/repository/FriendRepositoryAdapter.kt` — 수정
- `toFriendMember(info)`에서 `email = info.email` 라인 삭제.

#### 4.2.9 `domain/blockedmember/dao/BlockedMemberFindDao.kt` — 수정
- `@QueryProjection` select 절에서 `member.email` 라인 삭제.

#### 4.2.10 `domain/blockedmember/dao/BlockedMemberTargetInfo.kt` — 수정
- `@QueryProjection constructor`에서 `val email: String?` 파라미터 삭제.

#### 4.2.11 `domain/blockedmember/repository/BlockedMemberRepositoryAdapter.kt` — 수정
- `BlockedMemberTarget(...)` 매핑에서 `email = info.email` 라인 삭제.

### 4.3 웹 (gb-boot-web)

#### 4.3.1 `domain/member/payload/request/SignUpRequest.kt` — 수정
- `email: String`(`@field:Email` + `@field:NotBlank`) → `loginId: String`(`@field:NotBlank` + `@field:Length`/`@field:Pattern`로 아이디 규칙 적용).
  - **포인트**: `@field:Email`/`jakarta.validation.constraints.Email` import 제거. loginId 형식 규칙(허용 문자/길이)은 프론트 `Validate`와 합의 후 동일 정규식 사용 권장(4.4 / 6절).
- `command()`: `SignUpCommand(email, ...)` → `SignUpCommand(loginId, ...)`.

#### 4.3.2 `domain/member/payload/request/LoginRequest.kt` — 수정
- `email: String`(`@field:Email`) → `loginId: String`(`@field:NotBlank`).
- `command()`: `LoginCommand(email, ...)` → `LoginCommand(loginId, ...)`.

#### 4.3.3 `domain/member/payload/request/SocialLoginRequest.kt` — 수정
- `val email: String?` 필드 삭제.
- `command()`: `SocialLoginCommand(id, socialType, fcmToken)` (email 인자 제거).

#### 4.3.4 `domain/member/api/MemberEmailFindApi.kt` — **삭제**
- `POST /api/members/find-email` 엔드포인트 제거.

#### 4.3.5 `domain/member/payload/request/MemberEmailFindRequest.kt` — **삭제**

#### 4.3.6 `domain/member/payload/response/MemberEmailFindResponse.kt` — **삭제**

#### 4.3.7 `domain/member/payload/response/MemberResponse.kt` — 수정
- `val email: String` 필드 삭제.
- `of(authenticatedMember)`에서 `val email = checkNotNull(member.email) { ... }` 라인과 `email = email` 라인 삭제.
- **포인트**: 로그인/회원가입 응답에서 email 노출 제거. 프론트가 응답 email을 쓰지 않도록 4.4와 동기화.

#### 4.3.8 `domain/member/payload/response/SocialMemberResponse.kt` — 수정
- `val email: String?` 필드 삭제. `of(...)`에서 `email = member.email` 라인 삭제.

#### 4.3.9 `domain/member/payload/response/MemberFindResponse.kt` — 수정
- `val email: String?` 필드 삭제. `constructor(member)`에서 `email = member.email` 라인 삭제.
- **포인트**: 친구 추가용 후보 목록(`GET /api/friends/targets`) 응답. email 제거(로그인ID 노출 불필요).

#### 4.3.10 `domain/friend/payload/response/FriendResponse.kt` — 수정
- `val email: String?` 필드 삭제. `of(friendMember)`에서 `email = friendMember.email` 라인 삭제.

#### 4.3.11 `domain/friend/payload/response/FriendRequestResponse.kt` — 수정
- `val email: String?` 필드 삭제. `of(friendMember)`에서 `email = friendMember.email` 라인 삭제.

#### 4.3.12 `global/config/SecurityConfig.kt` — 수정
- `POST_AUTH_WHITELIST`에서 `"/api/members/find-email"` 제거.
- 잔여: `"/api/members"`(가입), `"/api/members/login"`, `"/api/members/social-login"` 유지.

#### 4.3.13 `resources/data-local.sql` — 수정 (6절 참조)
- INSERT 컬럼 `email` → `login_id`, 값 `'holeman@naver.com'` → `'holeman'`(아이디 값). `CREATED_BY`/`LAST_MODIFIED_BY`도 이메일 문자열을 쓰고 있으나 감사 컬럼이라 임의 값으로 교체(예: `'holeman'`).

### 4.4 프론트 변경 — 화면/모델/api 클라이언트

> 디렉토리: `/Users/jowonjin/IdeaProjects/beside/14th/groubing-app-frontend`

#### 4.4.1 `lib/api/auth_api.dart` — 수정
- `signUp({email, password, nickname})` → `signUp({loginId, password, nickname})`, body `{'email': ...}` → `{'loginId': ...}`.
- `emailLogin({email, password, fcmToken})` → `login({loginId, password, fcmToken})`(메서드명도 정리 권장), body `{'email': ...}` → `{'loginId': ...}`.
- `socialLogin({email, socialType, id, fcmToken})` → email 파라미터 삭제, body에서 `'email'` 제거.
- `findEmail({email})` 메서드 **삭제** (`POST /api/members/find-email` 호출 제거).
- `patchPasswordWithCurrent`(설정>비번 변경)는 그대로 유지(결정 #4).

#### 4.4.2 `lib/screens/auth/login_screen.dart` — 수정
- 입력 의미를 "아이디(이메일)" → "아이디"로 변경: placeholder `'아이디(이메일)'` → `'아이디'`, `isEmail: true` 제거(또는 false).
- 검증 `Validate.isEmail(id)` → loginId 형식 검증(`Validate.isLoginId`, 4.4.6)으로 교체, 에러 문구 "올바른 이메일 형식이…" → "아이디 형식이…"로 수정.
- `AuthApi.emailLogin(email: id, ...)` → `AuthApi.login(loginId: id, ...)`.
- 하단 서브버튼 **"아이디 찾기" / "비밀번호 찾기" 제거**(결정 #4). "회원가입"만 유지.
- SNS 버튼 영역은 현행 "준비중" 유지(노출 X, 변경 없음 — 결정 #5).

#### 4.4.3 `lib/screens/auth/signup_screen.dart` — 수정
- `enum _Step`에서 `email`, `emailAuth` 단계를 **`loginId` 단계로 통합/대체**:
  - 권장: `_Step { loginId, password, agree, nickname }` (이메일 인증 단계 제거).
  - `_emailCtrl` → `_loginIdCtrl`, `_authCodeCtrl` 및 관련 `_onAuthCodeNext` 제거.
- `_headline`/placeholder의 "아이디(이메일)…" → "아이디…", "인증 번호 발송" 문구 제거.
- `_onEmailNext`(이메일 정규식) → `_onLoginIdNext`(loginId 형식 검증).
- `_onSubmit`: `AuthApi.signUp(email: email, ...)` → `AuthApi.signUp(loginId: loginId, ...)`.
- `StepBar(total: _Step.values.length)`는 enum 길이 자동 반영(단계 수 5→4 감소).

#### 4.4.4 `lib/screens/auth/find_id_screen.dart` — **삭제 또는 라우트 미연결(숨김)**
- 권장: 파일 삭제 + main.dart/routes 정리. (결정 #4)

#### 4.4.5 `lib/screens/auth/find_pw_screen.dart` — **삭제 또는 라우트 미연결(숨김)**
- 이미 코드 주석에 "무인증 재설정 엔드포인트 없음"이 명시됨(동작 불가 화면). 제거. (결정 #4)

#### 4.4.6 `lib/utils/validate.dart` — 수정
- `isEmail` 사용처 제거에 맞춰, loginId 형식 검증 `isLoginId(String)` 추가(백엔드 `SignUpRequest`의 loginId 규칙과 동일 정규식 — 6절에서 규칙 확정).
- `isEmail`은 다른 사용처 없으면 삭제 가능(login/signup/find_* 전부 정리되므로).

#### 4.4.7 `lib/config/routes.dart` — 수정
- `static const String findId` / `findPw` 상수 제거(또는 주석 처리). 잔여 라우트 영향 없음.

#### 4.4.8 `lib/main.dart` — 수정
- `import 'screens/auth/find_id_screen.dart'` / `find_pw_screen.dart` 제거.
- `routes` 맵에서 `Routes.findId` / `Routes.findPw` 엔트리 제거.

#### 4.4.9 `lib/widgets/auth_input.dart` — 변경 없음(선택)
- `isEmail` 플래그는 keyboardType 용도라 유지 가능. login/signup에서 false로 호출하면 됨.

#### 4.4.10 `lib/providers/auth_provider.dart` — 변경 없음
- `userInfo`는 동적 Map이라 응답에서 email이 빠져도 무해. 화면에서 `userInfo['email']`을 참조하는 곳 없는지만 확인(현재 grep상 없음).

---

## 5. 구현 순서

| # | 파일 | 변경 유형 | 내용 |
|---|------|-----------|------|
| 1 | `domain/.../member/domain/Member.kt` | 수정 | `email` → `loginId`, `maskEmail()` 삭제 |
| 2 | `domain/.../member/domain/NewMember.kt` | 수정 | `email` → `loginId` (nullable) |
| 3 | `domain/.../member/domain/port/MemberQueryRepository.kt` | 수정 | `findByEmail` 삭제, `findByEmailAndMemberType`→`findOneByLoginId`, `existsByEmail`→`existsByLoginId` |
| 4 | `domain/.../auth/domain/SocialInfo.kt` | 수정 | `email` 필드 + `create`/`of` 인자 제거 |
| 5 | `domain/.../auth/application/command/SocialLoginCommand.kt` | 수정 | `email` 제거 |
| 6 | `domain/.../auth/application/command/SignUpCommand.kt` | 수정 | `email`→`loginId`, `toNewMember` 반영 |
| 7 | `domain/.../auth/application/command/LoginCommand.kt` | 수정 | `email`→`loginId` |
| 8 | `domain/.../auth/domain/SignUpValidator.kt` | 수정 | loginId 중복검사 + 메시지 |
| 9 | `domain/.../auth/application/SignUpService.kt` | 수정 | 예외 메시지 |
| 10 | `domain/.../auth/application/LoginService.kt` | 수정 | `findOneByLoginId` 호출 |
| 11 | `domain/.../auth/application/SocialLoginService.kt` | 수정 | NewMember(loginId=null) + SocialInfo.create email 제거 |
| 12 | `domain/.../auth/application/MemberEmailFindService.kt` | 삭제 | 아이디찾기 제거 |
| 13 | `domain/.../friend/domain/FriendMember.kt` | 수정 | `email` 제거 |
| 14 | `domain/.../blockedmember/domain/BlockedMemberTarget.kt` | 수정 | `email` 제거 |
| 15 | `db-core/.../member/entity/MemberEntity.kt` | 수정 | `EMAIL`→`LOGIN_ID` 컬럼(nullable+unique), from/toDomain |
| 16 | `db-core/.../member/repository/MemberJpaRepository.kt` | 수정 | `findByEmail*` 삭제/대체, `existsByLoginId*` |
| 17 | `db-core/.../member/repository/MemberRepositoryAdapter.kt` | 수정 | 포트 구현 loginId 전환 |
| 18 | `db-core/.../auth/entity/SocialInfoEntity.kt` | 수정 | `email` 컬럼/매핑 제거 |
| 19 | `db-core/.../friend/dao/FriendMemberInfo.kt` | 수정 | `email` 파라미터 제거 |
| 20 | `db-core/.../friend/dao/FriendFindDao.kt` | 수정 | `memberEntity.email` 셀렉트 3곳 제거 |
| 21 | `db-core/.../friend/repository/FriendRepositoryAdapter.kt` | 수정 | `email` 매핑 제거 |
| 22 | `db-core/.../blockedmember/dao/BlockedMemberTargetInfo.kt` | 수정 | `email` 파라미터 제거 |
| 23 | `db-core/.../blockedmember/dao/BlockedMemberFindDao.kt` | 수정 | `member.email` 셀렉트 제거 |
| 24 | `db-core/.../blockedmember/repository/BlockedMemberRepositoryAdapter.kt` | 수정 | `email` 매핑 제거 |
| 25 | `boot-web/.../member/payload/request/SignUpRequest.kt` | 수정 | `email`→`loginId` + 검증 교체 |
| 26 | `boot-web/.../member/payload/request/LoginRequest.kt` | 수정 | `email`→`loginId` |
| 27 | `boot-web/.../member/payload/request/SocialLoginRequest.kt` | 수정 | `email` 제거 |
| 28 | `boot-web/.../member/payload/request/MemberEmailFindRequest.kt` | 삭제 | find-email 제거 |
| 29 | `boot-web/.../member/payload/response/MemberEmailFindResponse.kt` | 삭제 | find-email 제거 |
| 30 | `boot-web/.../member/api/MemberEmailFindApi.kt` | 삭제 | find-email 엔드포인트 제거 |
| 31 | `boot-web/.../member/payload/response/MemberResponse.kt` | 수정 | `email` 제거 |
| 32 | `boot-web/.../member/payload/response/SocialMemberResponse.kt` | 수정 | `email` 제거 |
| 33 | `boot-web/.../member/payload/response/MemberFindResponse.kt` | 수정 | `email` 제거 |
| 34 | `boot-web/.../friend/payload/response/FriendResponse.kt` | 수정 | `email` 제거 |
| 35 | `boot-web/.../friend/payload/response/FriendRequestResponse.kt` | 수정 | `email` 제거 |
| 36 | `boot-web/.../global/config/SecurityConfig.kt` | 수정 | 화이트리스트에서 find-email 제거 |
| 37 | `boot-web/.../resources/data-local.sql` | 수정 | email→login_id 컬럼/값 |
| 38 | 테스트 / Fixtures / Vocabulary (7절) | 수정/삭제 | email→loginId, find-email 테스트·문서 제거 |
| 39 | REST Docs (.adoc, api.adoc) (7절) | 수정/삭제 | MemberEmailFind 문서 제거, member 응답 필드 갱신 |
| 40 | 프론트 (4.4 전체) | 수정/삭제 | 화면/모델/api loginId 전환 + find 화면 제거 |
| 41 | 운영 DDL (6절) | 수동 | EMAIL DROP / LOGIN_ID ADD + unique index |

> 의존성 순서: 도메인(Member/NewMember/port/Command) → 인프라(Entity/JpaRepository/Adapter/DAO/Projection) → 웹(Request/Response/Api/Security) → 테스트/문서 → 프론트 → 운영 DDL. **kapt Q-class 재생성** 필요(15·18·19·20·22·23 변경 후 `./gradlew :infrastructure:storage:gb-db-core:kaptKotlin`).

---

## 6. DB 마이그레이션 / DDL

### 6.1 로컬 (H2, `local` 프로필)
- `ddl-auto=create` → JPA가 엔티티 기준으로 스키마 재생성(`MEMBERS.LOGIN_ID`, `SOCIAL_INFOS`에서 EMAIL 제거 자동 반영). 추가 DDL 불필요.
- `data-local.sql` 수정(4.3.13): `INSERT INTO MEMBERS (member_id, login_id, password, nickname, role, ...) VALUES (100, 'holeman', ...)`. 감사 컬럼(`CREATED_BY`/`LAST_MODIFIED_BY`)의 이메일 문자열도 임의 값으로 교체.
- `defer-datasource-initialization: true`라 스키마 생성 후 SQL 실행되므로 순서 안전.

### 6.2 운영 (MySQL, `prod` 프로필, `ddl-auto=none`)
JPA가 스키마를 만들지 않으므로 배포 전 **수동 DDL** 적용. **출시 전 실사용자 없음 확정(2026-06-07) → 백필 불필요, 컬럼 교체만.**

```sql
-- 1) MEMBERS: loginId 컬럼 추가 (nullable)
ALTER TABLE MEMBERS ADD COLUMN LOGIN_ID VARCHAR(255) NULL;

-- 2) 백필 불필요 (실사용자 없음). 기존 더미/테스트 회원이 있으면 그냥 삭제 권장.

-- 3) loginId unique index (NULL 다중 허용)
CREATE UNIQUE INDEX UX_MEMBERS_LOGIN_ID ON MEMBERS (LOGIN_ID);

-- 4) MEMBERS.EMAIL 컬럼 제거 (기존 unique index가 있으면 먼저 DROP)
ALTER TABLE MEMBERS DROP COLUMN EMAIL;

-- 5) SOCIAL_INFOS.EMAIL 컬럼 제거
ALTER TABLE SOCIAL_INFOS DROP COLUMN EMAIL;
```

### 6.3 loginId nullable + unique 함정 (핵심)
- **MySQL/H2의 unique index는 NULL을 여러 행에 허용**한다(표준 SQL 동작). 따라서 소셜 회원 다수가 `LOGIN_ID = NULL`이어도 unique 위반 없음 → 결정 #2 충족.
- **주의**: 빈 문자열("")은 NULL이 아니므로 unique 대상 → 소셜 회원을 ""로 채우면 두 번째부터 충돌. 반드시 `null`로 저장(코드에서 `NewMember(loginId = null, ...)` 보장).
- **백필 시**: 이메일 로컬파트가 중복될 수 있음(`a@x.com`, `a@y.com` → 둘 다 `a`). unique index 생성 전 중복 해소 또는 재가입 유도 정책 결정 필요.
- 컬럼 길이: 기존 EMAIL `VARCHAR(255)` 관례를 따라 `VARCHAR(255)` 권장(실 loginId 규칙이 더 짧아도 무방).

### 6.4 loginId 형식 규칙 (백/프론트 동기화) — **확정**
- **규칙 확정: 영문 소문자 + 숫자 4~20자 = `^[a-z0-9]{4,20}$`** (2026-06-07 결정).
- 백엔드 `SignUpRequest.loginId`에 `@field:Pattern(regexp = "^[a-z0-9]{4,20}$", message = "아이디는 영문 소문자와 숫자 4~20자로 입력해 주세요.")` 적용.
- 프론트 `Validate.isLoginId`도 **동일 정규식** 사용.

---

## 7. 테스트 영향 / REST Docs 영향

### 7.1 테스트 (Kotest)
| 파일 | 처리 | 내용 |
|------|------|------|
| `domain/.../member/domain/MemberTest.kt` | 수정/축소 | `maskEmail()` 테스트 전체 삭제(메서드 제거), `aMember` 헬퍼에서 email→loginId |
| `domain/.../auth/application/SocialLoginServiceTest.kt` | 수정 | `SocialLoginCommand`/`NewMember`/`SocialInfo`에서 email 제거 반영, mock stub 갱신 |
| `db-core/.../testFixtures/MemberEntityFixtures.kt` | 수정 | `aMember(...)` 2개 오버로드의 `email` → `loginId`(또는 `loginId` 인자화), `Arb.email(...)` → loginId용 Arb |
| `boot-web/.../member/api/SignUpApiTest.kt` | 수정 | 요청 바디 `email`→`loginId`, 응답 필드 email 제거, REST Docs 필드 갱신 |
| `boot-web/.../member/api/LoginApiTest.kt` | 수정 | 동일 (loginId 로그인) |
| `boot-web/.../member/api/SocialLoginApiTest.kt` | 수정 | 요청 바디 email 제거, 응답 email 제거 |
| `boot-web/.../member/api/MemberEmailFindApiTest.kt` | 삭제 | API 제거에 수반 |
| `boot-web/.../friend/api/FriendFindApiTest.kt` | 수정 | 응답 email 필드 제거 |
| `boot-web/.../friend/api/FriendTargetsFindApiTest.kt` | 수정 | `MemberFindResponse` email 제거 반영 |
| `boot-web/.../blockedmember/api/BlockedMemberFindApiTest.kt` | 수정 | 응답 email 필드 제거 |
| `boot-web/.../config/WithAuthMember.kt` | 확인 | email 참조 있으면 정리(인증 픽스처) |
| `boot-web/.../vocabulary/MemberVocabulary.kt` | 수정 | `fun email(...)` 정의 삭제, 필요 시 `fun loginId(...)` 추가(SignUp/Login 요청 필드 문서용) |

- **포인트**: `MemberVocabulary.email()`는 여러 ApiTest가 공유하는 REST Docs 필드 정의 함수다. 삭제 시 이를 참조하던 테스트의 `requestBody`/`responseBody` 필드 목록을 함께 정리해야 빌드가 통과한다(`./gradlew build`가 test→snippets→asciidoctor 체인이라 깨진 테스트는 문서 빌드를 막음).

### 7.2 REST Docs (.adoc)
| 파일 | 처리 |
|------|------|
| `src/docs/asciidoc/member/MemberEmailFind.adoc` | 삭제 |
| `src/docs/asciidoc/api.adoc` | `[[member-email-find]]` 섹션 및 `link:member/MemberEmailFind.html` 링크 제거 |
| `src/docs/asciidoc/member/SignUpApi.adoc` / `LoginApi.adoc` / `SocialLoginApi.adoc` | operation snippet은 테스트가 재생성하므로 .adoc 본문 직접 수정은 불필요(필드 변화는 snippet 자동 반영). email 설명 텍스트가 본문에 박혀 있으면 정리 |

- 재생성: `./gradlew test` → `asciidoctor` → `copyDocument`(build가 자동 체인).

---

## 8. 리스크 & 주의

- **loginId nullable + unique 함정 (최우선)**: 소셜 회원은 반드시 `loginId = null`로 저장. ""(빈 문자열)로 채우면 unique 충돌. `NewMember(loginId = null)` 경로(SocialLoginService)와 엔티티 매핑을 코드 리뷰/테스트로 보장. unique index는 NULL 다중 허용이라 nullable과 양립(6.3).
- **email 필드의 광범위 전파**: email은 member/auth뿐 아니라 friend/blockedmember의 도메인·QueryDSL 프로젝션·응답 DTO까지 흐른다. `MemberEntity.email` 컬럼 제거 시 `memberEntity.email` 셀렉트가 컴파일/런타임에서 깨지므로 4.1.16 / 4.2.6~4.2.11 / 4.3.9~4.3.11를 빠짐없이 반영. QueryDSL Q-class kapt 재생성 필수.
- **기존 운영 데이터 처리**: **출시 전 실사용자 없음 확정** → 백필 불필요(6.2). 운영에 남은 더미/테스트 회원이 있으면 컬럼 교체 전 삭제 권장.
- **DataIntegrityViolation 메시지**: 가입 시 동시성으로 unique 위반이 잡히면 메시지를 "아이디/닉네임 중복"으로 정정. 어느 쪽 충돌인지 구분 불가는 기존과 동일(개선은 별도).
- **loginId 형식 규칙 미확정**: 백엔드 `@field:Pattern`과 프론트 `Validate.isLoginId`를 동일 정규식으로 맞춰야 함(6.4). 미합의 시 프론트 통과·백엔드 거절 불일치 발생.
- **REST Docs 빌드 체인**: 테스트가 snippet을 만들고 asciidoctor가 합치므로, email 필드를 남긴 테스트가 하나라도 있으면 `./gradlew build` 실패. Vocabulary `email()` 삭제와 참조 테스트 정리를 동시 진행.
- **프론트 SNS/소셜**: 결정 #5에 따라 SNS 버튼은 "준비중" 유지. `socialLogin` API는 보존하되 email만 제거 — v1에서 실제 호출되지 않으므로 회귀 위험 낮음.
- **TODO (출시 전, 이번 범위 외)**: Google Play 데이터 안전 섹션의 **웹 계정삭제 URL** 제공 필요(Apple은 인앱 탈퇴로 충족). 계정 복구(아이디/비번 찾기)는 휴대폰/이메일 인증 도입 시 v2로 재설계.

---

## 9. 파일 영향 요약

- **백엔드 수정**: 약 31개 (domain 12, db-core 10, boot-web 9 — Security/data-local 포함)
- **백엔드 삭제**: 4개 (`MemberEmailFindService`, `MemberEmailFindApi`, `MemberEmailFindRequest`, `MemberEmailFindResponse`) + REST Docs `MemberEmailFind.adoc`
- **테스트/문서**: 수정 ~11개, 삭제 1개(`MemberEmailFindApiTest`) + `api.adoc` 섹션 정리
- **프론트 수정**: 6개 (`auth_api.dart`, `login_screen.dart`, `signup_screen.dart`, `validate.dart`, `routes.dart`, `main.dart`)
- **프론트 삭제**: 2개 (`find_id_screen.dart`, `find_pw_screen.dart`)
- **운영 DDL(수동)**: `MEMBERS.EMAIL` DROP / `MEMBERS.LOGIN_ID` ADD(nullable) + unique index / `SOCIAL_INFOS.EMAIL` DROP (+ 선택 백필)
