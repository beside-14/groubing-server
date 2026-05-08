# Onboarding — groubing-server

새로 합류한 개발자가 **이 문서 하나만 읽고도** 코드 구조를 이해하고, 빌드해서 실행하고, 첫 PR을 올릴 수 있도록 작성했다. 의도적으로 길게 썼지만 섹션별로 끊어 읽어도 된다.

---

## 1. 한눈에 보기 (5분)

### 무엇

**Groubing** 은 빙고를 매개로 한 그룹 챌린지 서비스다. 본 저장소는 그 백엔드 API 서버.

핵심 도메인은 빙고(Bingo) — 사용자가 빙고 보드를 만들고, 친구들과 함께 아이템 9개(또는 16개)를 채우며, 한 줄을 완성할 때마다 알림이 발송되는 흐름.

### 왜 이런 구조

- **헥사고날(포트-어댑터) 멀티모듈** — 도메인 로직을 인프라(JPA/Spring Web/FCM/JWT)에서 분리.
- **`gb-domain-core` 모듈은 Spring Web / JPA 의존이 0**. 순수 Kotlin + Spring Boot starter + spring-tx 만 있다. JPA Entity 는 별도 모듈(`gb-db-core`)에서 도메인을 미러한다.
- 의도: 도메인 규칙은 Entity/Adapter 가 바뀌어도 흔들리지 않게. 새 ORM 으로 갈아끼우거나, 다른 boot 모듈(예: 배치)에서 같은 도메인을 재사용해도 도메인 코드 수정 없이 가능.

### 기술 스택

| 영역 | 기술 |
|---|---|
| 언어 / JDK | Kotlin 1.9.25 / JDK 21 |
| 프레임워크 | Spring Boot 3.3.4 (Web, Data JPA, Security 6, Tx, Async) |
| ORM | JPA(Hibernate) + QueryDSL 5.1.0 (read-only DAO 용) |
| DB | local: H2(in-memory) / prod: MySQL |
| 인증 | JWT (HS512, 1년 만료) — stateless |
| 푸시 | Firebase Admin (FCM) |
| 비밀번호 | BCrypt |
| 테스트 | KoTest (BehaviorSpec) + MockK + springmockk |
| API 문서 | Spring REST Docs (테스트 → snippets → AsciiDoc → static/docs/*.html) |
| 빌드 | Gradle Kotlin DSL, multi-module |

---

## 2. 5분 안에 띄우기

### 사전 준비

- JDK 21 설치 (`brew install openjdk@21` 또는 sdkman)
- 저장소 clone

### 첫 실행 — local profile (H2 메모리 DB)

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

`local` 프로파일은 H2 in-memory(`jdbc:h2:mem:groubing`)를 사용하고, JPA 가 스키마를 자동 생성한 뒤 `data-local.sql` 로 시드 데이터를 넣는다. 별도 DB 셋업 불필요.

뜬 직후:
- 서버: `http://localhost:8080`
- API 문서 (REST Docs): `http://localhost:8080/docs/api.html`
- H2 콘솔이 필요하면 `application-local.yml` 에서 활성화

### 자주 쓰는 커맨드

```bash
# 전체 테스트
./gradlew test

# 한 테스트 클래스만
./gradlew test --tests 'com.beside.groubing.domain.bingo.api.BingoBoardCreateApiTest'

# REST Docs 만 새로 생성 (테스트 → snippets → asciidoctor → static/docs)
./gradlew asciidoctor copyDocument

# 빌드 (api.jar 생성, build/libs/api.jar)
./gradlew build

# QueryDSL Q 클래스가 IntelliJ 에서 안 보일 때
./gradlew :infrastructure:storage:gb-db-core:kaptKotlin
```

> **주의**: `application.yml` 에는 datasource 가 없다. 프로파일을 안 주면 부팅 실패. 항상 `--spring.profiles.active=local` 필요.

---

## 3. 모듈 구조

### 의존 방향

```
                  boot/gb-boot-web
                  (Controller, Response DTO, REST Docs, SecurityConfig)
                          │
            ┌─────────────┼─────────────┐
            │             │             │
            ▼             ▼             ▼
    gb-domain-core   gb-db-core    gb-jwt-core
    (Service,           ▲          gb-crypto-core
     도메인,             │          gb-fcm-sender
     Port,              └── api    (Adapters of ports)
     Validator)
            ▲
            │ (모든 인프라 모듈은 도메인 포트를 의존)
            │
    config/* — gb-config-yaml-importer, gb-config-logging
```

규칙:
- `gb-domain-core` 는 다른 모듈을 import 하지 않는다 (인프라/웹 무관).
- `gb-db-core` / `gb-jwt-core` / `gb-crypto-core` / `gb-fcm-sender` 는 `gb-domain-core` 의 포트를 구현한다.
- `gb-boot-web` 만 모든 모듈을 알고 있고, 의존성 주입을 통해 Controller 에 Service 를 꽂는다.

### 모듈별 책임

| 모듈 | 책임 |
|---|---|
| `boot/gb-boot-web` | Spring Boot 앱 진입점. `@RestController`, Request/Response DTO, `SecurityConfig`, `GlobalExceptionHandler`, REST Docs source, FCM 푸시 발송 핸들러 |
| `domain/gb-domain-core` | **순수 도메인** — 도메인 객체, VO, 일급 컬렉션, `@Service` 애플리케이션 레이어, 포트 인터페이스, `@Component` Validator, 도메인 이벤트, 예외, Command DTO. JPA / Spring Web 의존 없음 |
| `infrastructure/storage/gb-db-core` | JPA Entity, QueryDSL DAO, Repository Adapter, `BaseEntity` 계열. `@Embeddable` VO 도 여기 |
| `infrastructure/support/gb-jwt-core` | `JwtProvider` + `JwtTokenManager` 어댑터 (`TokenManager` 포트 구현) |
| `infrastructure/support/gb-crypto-core` | `BCryptPasswordEncryptor` 어댑터 (`PasswordEncryptor` 포트 구현) |
| `infrastructure/support/gb-fcm-sender` | Firebase Admin 초기화 + 발송 |
| `config/gb-config-yaml-importer` | `application*.yml`, `data-local.sql` |
| `config/gb-config-logging` | `logback-spring.xml` |

### 한 도메인 컨텍스트의 패키지 레이아웃

도메인 컨텍스트 7개: `auth`, `member`, `bingo`, `friend`, `blockedmember`, `feed`, `notification`.

`bingo` 를 예시로:

```
gb-domain-core/com/beside/groubing/domain/bingo/
├── application/        # @Service per use case (Create, Delete, Find, ListFind, Update, ItemComplete, ItemUpdate, ItemShuffle, MemberLeave)
├── domain/             # 순수 도메인 (BingoBoard, BingoItem, BingoMember, BingoBoardDetail, BingoBoards, ...)
│   ├── map/            # BingoMap, BingoLine, Direction — 빙고판 알고리즘
│   └── port/           # BingoBoardCommandRepository, BingoBoardQueryRepository
├── event/              # BingoCompleteEvent 등 (toMessage 메서드도 여기)
├── exception/          # BingoInputException, BingoIllegalStateException
└── payload/command/    # BingoBoardCreateCommand 등 — Service 입력 DTO

gb-db-core/com/beside/groubing/domain/bingo/
├── entity/             # BingoBoardEntity + BingoItemEntity + BingoMemberEntity + BingoCompleteMemberEntity + *Embeddable VO
├── repository/         # BingoBoardJpaRepository (JPA) + BingoBoardRepositoryAdapter (포트 구현)
└── dao/                # QueryDSL read DAO (BingoBoardListFindDao 등)

gb-boot-web/com/beside/groubing/domain/bingo/
├── api/                # @RestController (BingoBoardCreateApi, BingoBoardDeleteApi, ...)
└── payload/            # request/ + response/ DTO (응답 DTO 는 도메인 객체로부터 .of(...) 팩토리로 변환)
```

> **주의**: 모듈 3개에 같은 패키지 경로(`com.beside.groubing.domain.bingo`)를 쓴다. **split-package 가 아니라 의도된 parallel-shared 구조** — leaf 만 다르다 (`application/` vs `entity/` vs `api/`). meet-again-backend 의 동일 패턴.

---

## 4. 한 use case 가 어떻게 흘러가는가

가장 단순한 use case 인 **회원가입 (POST /api/members)** 흐름을 따라가 보자. 이걸 이해하면 나머지 use case 도 같은 모양이다.

### 4.1 요청 도착

`gb-boot-web/.../domain/member/api/SignUpApi.kt`

```kotlin
@PostMapping
fun signUp(@RequestBody @Validated request: SignUpRequest): ApiResponse<MemberResponse> {
    val authenticatedMember = signUpService.signUp(request.command())
    return ApiResponse.OK(MemberResponse.of(authenticatedMember))
}
```

**컨트롤러는 Service 만 호출하고, 도메인 객체를 응답 DTO 로 매핑한다.** 그 외 일은 안 한다.

### 4.2 Service — 조합만

`gb-domain-core/.../domain/auth/application/SignUpService.kt`

```kotlin
@Service @Transactional
class SignUpService(
    private val memberCommandRepository: MemberCommandRepository,  // 포트
    private val signUpValidator: SignUpValidator,                  // 도메인 컴포넌트
    private val passwordEncryptor: PasswordEncryptor,              // 포트
    private val tokenManager: TokenManager                         // 포트
) {
    fun signUp(signUpCommand: SignUpCommand): AuthenticatedMember {
        val newMember = signUpCommand.toNewMember(passwordEncryptor)
        signUpValidator.validate(newMember)
        return try {
            val savedMember = memberCommandRepository.save(newMember)
            AuthenticatedMember(savedMember, tokenManager.generateAccessToken(savedMember.id, savedMember.role.name))
        } catch (e: DataIntegrityViolationException) {
            throw MemberInputException("중복된 email / 닉네임 입니다.")
        }
    }
}
```

**Service 는 어떻게 하는지 모르고, 무엇을 하는지만 안다.**
- 비밀번호 암호화 → `passwordEncryptor.encode` (포트)
- 검증 → `signUpValidator.validate` (도메인 컴포넌트)
- 저장 → `memberCommandRepository.save` (포트)
- 토큰 발급 → `tokenManager.generateAccessToken` (포트)

### 4.3 Validator — 검증만

`gb-domain-core/.../domain/auth/domain/SignUpValidator.kt`

```kotlin
@Component
class SignUpValidator(private val memberQueryRepository: MemberQueryRepository) {
    fun validate(newMember: NewMember) {
        if (newMember.email != null && memberQueryRepository.existsByEmail(newMember.email)) {
            throw MemberInputException("이미 가입된 email 주소입니다.")
        }
        if (memberQueryRepository.existsByNickname(newMember.nickname)) {
            throw MemberInputException("이미 사용 중인 닉네임 입니다.")
        }
    }
}
```

여러 저장소를 조회해 분기-throw 하는 검증은 모두 `XxxValidator` 도메인 컴포넌트로 분리한다 (rule 1-1).

### 4.4 포트 → 어댑터

`MemberCommandRepository` (포트, `gb-domain-core`) → `MemberRepositoryAdapter` (어댑터, `gb-db-core`)

```kotlin
// 포트 (gb-domain-core)
interface MemberCommandRepository {
    fun save(newMember: NewMember): Member
    fun update(member: Member): Member
    fun editProfileOrNull(memberId: Long, newProfile: FileInfo): FileInfo?
    fun deleteProfileOrNull(memberId: Long): FileInfo?
}

// 어댑터 (gb-db-core)
@Repository
class MemberRepositoryAdapter(private val memberJpaRepository: MemberJpaRepository) : MemberCommandRepository, MemberQueryRepository {
    override fun save(newMember: NewMember): Member {
        return memberJpaRepository.save(MemberEntity.from(newMember)).toDomain()
    }
    // ...
}
```

**어댑터는 DAO/JpaRepository 호출 + `toDomain()` 변환만 한다. 비즈니스 분기는 금지.** 단건 조회는 non-null, 없으면 어댑터에서 `XxxInputException` 으로 throw. nullable 이 정말 필요할 때만 메서드명에 `OrNull` 접미사.

### 4.5 Entity ↔ Domain 변환

`gb-db-core/.../domain/member/entity/MemberEntity.kt`

```kotlin
@Entity
class MemberEntity(...) {
    fun toDomain(): Member { /* ... */ }
    fun applyChanges(domain: Member) { /* update path */ }

    companion object {
        fun from(newMember: NewMember): MemberEntity { /* create path */ }
    }
}
```

세 메서드 패턴:
- `toDomain()` — entity → 도메인 (read 후 hydrate)
- `companion.from(domain)` — 도메인 → entity (create 경로)
- `applyChanges(domain)` — entity 의 var 필드를 도메인 값으로 업데이트 (update 경로)

### 4.6 응답 매핑

`MemberResponse.of(authenticatedMember)` 가 `AuthenticatedMember(member, accessToken)` 도메인 DTO 를 받아 응답 스키마로 변환.

서비스는 **응답 DTO 를 모른다.** 항상 도메인 타입을 반환하고, 컨트롤러가 `Response.of(...)` 팩토리로 매핑한다.

---

## 5. 새 기능 추가 가이드 — 체크리스트

새 use case (예: `POST /api/something`) 를 만들 때:

### 5.1 도메인이 새로 필요하면 (`gb-domain-core`)

1. **도메인 객체** `domain.<ctx>.domain.Xxx`
   - `private constructor` + `create(...)` (신규) / `of(...)` (rehydration) 팩토리
   - 행위는 메서드로 (`xxx.complete(memberId)`), getter 노출 후 외부 분기 금지
   - VO 가 필요하면 `init { ... }` 으로 invariant 검증
2. **포트** `domain.<ctx>.domain.port.<Xxx>CommandRepository` / `QueryRepository`
   - 단건 non-null. nullable 만 `OrNull` 접미사
   - 파라미터/반환은 도메인 타입. 엔티티 노출 금지
3. **예외** `domain.<ctx>.exception.XxxInputException` (사용자 입력) / `XxxIllegalStateException` (도메인 invariant 위반)
4. **Validator** 가 필요하면 `domain.<ctx>.domain.XxxValidator` (`@Component`)

### 5.2 `@Service` (gb-domain-core)

`domain.<ctx>.application.XxxYyyService` — **use case 당 하나**.
- 포트 + Validator + 다른 도메인 컴포넌트만 주입
- 메서드 안에 `if`, `filter`, `map { ... }` 같은 가공/분기 로직 금지 → 도메인/Validator 로 위임
- 반환은 도메인 객체 (`Member`, `BingoBoard`, …) 또는 도메인 DTO (`AuthenticatedMember`, `BingoBoardDetail` 같이 여러 도메인을 묶은 read 모델)

### 5.3 어댑터 (gb-db-core)

1. **Entity** `domain.<ctx>.entity.XxxEntity` — `@Entity`, `BaseEntity` 또는 `BaseAggregateRoot` 상속, `toDomain()` / `from(domain)` / `applyChanges(domain)` 3종
2. **JpaRepository** `domain.<ctx>.repository.XxxJpaRepository` — Spring Data 인터페이스
3. **Adapter** `domain.<ctx>.repository.XxxRepositoryAdapter` — `@Repository`, 포트 구현
4. (필요 시) **DAO** `domain.<ctx>.dao.XxxFindDao` — QueryDSL `@Repository`, projection 반환

> 같은 aggregate 안에서만 `@OneToMany` / `@ManyToOne` 사용. **다른 aggregate 와는 ID 참조 (`memberId: Long`) 만**. 응답에 다른 aggregate 의 필드가 필요하면 `@QueryProjection` 으로 평탄화.

### 5.4 Controller (gb-boot-web)

1. `domain.<ctx>.api.XxxApi` — `@RestController`, **Service 만 의존**
2. `domain.<ctx>.payload.request.XxxRequest` — Bean Validation 어노테이션, `command(memberId)` 메서드로 `XxxCommand` 변환
3. `domain.<ctx>.payload.response.XxxResponse` — `companion.of(domain)` 팩토리. `ApiResponse.OK(...)` 로 감싸서 반환
4. 인증이 필요 없으면 `SecurityConfig.GET/POST/PATCH_AUTH_WHITELIST` 에 경로 추가
5. REST Docs 테스트 작성 (필수, 빌드 게이트)

### 5.5 사이드이펙트가 있을 때

- **파일 업로드**: `MultipartFile` 은 `gb-boot-web` 에만 둔다. 컨트롤러에서 `FileProvider.upload(multipart)` → `FileInfo` 로 변환 → Service 에 전달
- **파일 삭제**: 도메인 서비스가 직접 `FileStorage.delete(fileInfo)` 호출 (pure NIO 헬퍼, `gb-domain-core`)
- **푸시 발송**: 도메인 이벤트 발행 → `BingoEventHandler` 가 `@EventListener` 로 받아서 처리 (트랜잭션 커밋 후 `@Async`)

---

## 6. 도메인 컨텍스트 한 줄 요약

| 컨텍스트 | 책임 | 핵심 도메인 |
|---|---|---|
| `auth` | 인증 / 토큰 발급 | `SignUp/Login/SocialLogin/PasswordReset/EmailFind` 서비스, `AuthenticatedMember`, `SocialInfo`, `PasswordVerifier`, `SignUpValidator`, `TokenManager`(port), `PasswordEncryptor`(port) |
| `member` | 회원 정보 관리 | `Member`, `NewMember`, `MemberRole/Type`, 닉네임/알림/프로필/탈퇴 서비스, `NicknameUniquenessValidator` |
| `bingo` | 빙고 보드 + 아이템 | `BingoBoard` (aggregate root), `BingoItem`, `BingoMember`, `BingoCompleteMember`, VO 4개(`BingoSize/Goal/Period/BoardType`), `BingoMap`/`BingoLine` (알고리즘), 9개 use case 서비스 |
| `friend` | 친구 요청/수락/거절/차단해제 | `Friend`, `FriendStatus`, `FriendMember` (projection-like), `FriendRelations` (일급 컬렉션), `FriendAddValidator` |
| `blockedmember` | 회원 차단 | `BlockedMember`, `BlockedMemberTarget`, `BlockMemberValidator` |
| `feed` | 친구/일반 피드 | `FeedEntry`, `FeedItem`, 두 개의 `*FeedListFindService` |
| `notification` | 빙고 이벤트 알림 | `Notification`, `NotificationItem`, `BingoEventHandler` (이벤트 리스너) |

> 아키텍처 다이어그램은 위 4번 흐름이 본질이라 별도 안 그렸다. 각 컨텍스트는 동일한 모양의 6단계 흐름을 따른다.

---

## 7. 꼭 알아야 할 컨벤션

### 7.1 포트 메서드 네이밍 (rule 13)

- 단건: `findOne(id)` (return type 으로 파라미터 의미 충분히 전달되면 `findById` 같이 안 씀)
- 복수: `find(...)`, `findAll(...)`
- 같은 타입 파라미터로 다른 조건이 필요하면(오버로드 구분) 그때만 `findByEmail` / `findByEmailAndMemberType` 처럼 `By` 접미사 허용
- nullable: `findOneOrNull` / `editProfileOrNull` 처럼 `OrNull` 접미사

### 7.2 Service 는 조합만 (rule 1)

```
BAD : 서비스 안에 .filter, .map 후 데이터 가공, if-throw 검증
GOOD: 도메인 메서드 / Validator / 포트 호출만 줄세움
```

판단 기준: 서비스 메서드를 읽었을 때 **무엇을** 하는지만 보여야 한다. **어떻게** 가 보이면 도메인으로 위임.

### 7.3 도메인 객체에 행위 부여 (rule 2)

```kotlin
// BAD
if (!blockedMember.isBlockedMember(requesterId)) throw ...

// GOOD
blockedMember.validateUnblockAuthority(requesterId)
```

### 7.4 어댑터는 변환만 (rule 1 의 어댑터 영역)

`@Repository` 안에 `if/else`, `filter`, `map { 가공 }` 금지. DAO 호출 + `toDomain()` 만.

### 7.5 응답 DTO 는 팩토리 + ApiResponse (rule 17)

```kotlin
// 컨트롤러
val saved = service.create(command)
return ApiResponse.OK(BingoBoardResponse.fromBingoBoard(saved, memberId))
```

응답 DTO 는 도메인을 받아 `.of(...)` / `.fromXxx(...)` 팩토리로 변환. `ApiResponse.OK(data)` 래퍼는 컨트롤러 책임.

### 7.6 사이드이펙트 경계

| 책임 | 위치 |
|---|---|
| `MultipartFile` 업로드 | `boot-web` (`FileProvider.upload`) — 컨트롤러에서 `FileInfo` 로 변환 후 서비스에 전달 |
| 파일 시스템 삭제 | `gb-domain-core` (`FileStorage.delete`) — 서비스에서 직접 호출 가능 (pure NIO) |
| 푸시 발송 | 도메인 이벤트 → `@EventListener @Async` 핸들러 |
| JWT 토큰 | `TokenManager` 포트 → `gb-jwt-core` 어댑터 |
| 비밀번호 암호화 | `PasswordEncryptor` 포트 → `gb-crypto-core` 어댑터 |

### 7.7 Spring Security

- Stateless JWT, `JwtAuthenticationFilter` 가 `UsernamePasswordAuthenticationFilter` 앞에서 토큰 파싱
- `@AuthenticationPrincipal memberId: Long` 으로 컨트롤러에서 회원 ID 받음
- 인증 면제 경로는 **`SecurityConfig.GET/POST/PATCH_AUTH_WHITELIST` 배열에 하드코딩**. 어노테이션으로 풀지 않는다.
- CSRF 비활성화 (stateless API)

### 7.8 트랜잭션

- `application.yml` 의 `open-in-view: false` — **트랜잭션 밖에서 lazy 컬렉션 접근하면 즉시 throw**. 컨트롤러에서 도메인 객체의 `@OneToMany` 같은 거 펼치면 안 됨. Service 안에서 처리해서 평탄화된 DTO 반환.

---

## 8. 테스트 작성 가이드

### 8.1 기본 스택

- **KoTest BehaviorSpec** (`Given / When / Then`) 이 디폴트
- **MockK** + `springmockk` (Mockito 는 의도적으로 제외 — 다시 추가하지 말 것)
- `KotestConfig.kt` 가 `IsolationMode.InstancePerLeaf` + Spring 확장 설정

### 8.2 Controller 슬라이스 테스트 (REST Docs 필수)

```kotlin
@ApiTest                          // 메타 어노테이션 — @WithAuthMember(memberId=1) + SecurityConfig + AutoConfigureRestDocs
@WebMvcTest(controllers = [XxxApi::class])
class XxxApiTest(
    private val mockMvc: MockMvc,
    @MockkBean private val xxxService: XxxService
) : BehaviorSpec({
    Given("...") {
        every { xxxService.do(...) } returns 도메인객체
        When("...") {
            Then("...") {
                mockMvc.perform(...)
                    .andExpect(status().isOk)
                    .andDocument(
                        "xxx-do",
                        requestBody(...),
                        responseBody(...)
                    )
            }
        }
    }
})
```

`andDocument` 가 REST Docs snippet 을 `build/generated-snippets/xxx-do/` 에 생성. AsciiDoctor 가 이걸 흡수해서 `static/docs/xxx.html` 로 만든다. **모든 컨트롤러는 REST Docs 테스트 필수.**

### 8.3 Persistence 슬라이스

- `@LocalPersistenceTest` — H2 + `@DataJpaTest` 기반. `@EnableJpaAuditing`. 어댑터/서비스가 필요하면 `@Import(XxxRepositoryAdapter::class)` 로 명시 임포트해야 한다 (슬라이스가 자동으로 안 끌어옴)
- `@ProductPersistenceTest` — 진짜 MySQL 사용. 운영 스키마 검증용, 자주 쓰지 않는다

### 8.4 JWT 가 필요한 테스트

`extension/JwtExpression.kt` 에 `getHttpHeaderJwt(memberId)` 가 있다. MockMvc 호출에 `Authorization` 헤더로 붙이면 끝.

### 8.5 자주 쓰는 픽스처

`test/.../BingoBoardFixtures.kt` — `aEmptyBingo()`, `aEnglishStudyBingoBoard()` 등 자주 쓰는 BingoBoard 시드.

---

## 9. 트러블슈팅 / 흔한 실수

| 증상 | 원인 / 해결 |
|---|---|
| 부팅 시 `Failed to configure a DataSource` | `--spring.profiles.active=local` (또는 `prod`) 안 줬음 |
| IntelliJ 에서 `QXxxEntity` 빨갛게 표시 | `./gradlew :infrastructure:storage:gb-db-core:kaptKotlin` 한 번 |
| 테스트에서 `LazyInitializationException` | `open-in-view: false` 라서 트랜잭션 밖 lazy 접근 불가. Service 안에서 끌어와서 평탄화 |
| 인증 면제 경로 추가했는데 401 | `SecurityConfig.GET/POST/PATCH_AUTH_WHITELIST` 배열에 안 추가됨. 어노테이션으로는 안 됨 |
| `BingoBoardEntity` 가 child collection 을 안 비움 | adapter `update(domain)` 의 `syncChildren` 이 id 매칭으로 add/update/orphanRemove. 도메인이 child 의 id 를 잃으면 inserts 만 발생 → bug. 도메인 mutator 가 id 를 보존하는지 확인 |
| `MemberEntity.findById(id)` 가 가입한 회원 못 찾음 | `@SQLRestriction("active = true")` 때문에 탈퇴 회원은 모든 쿼리에서 보이지 않음 |
| 만료된 빙고 조회 시 예외 | (이미 fix 됨) `BingoPeriod.of(since, until)` 로 rehydration 시 invariant 우회. `create()` 는 사용자 입력용 |
| FCM 토큰 만료/실패 | `FirebaseMessagingException` → `GlobalExceptionHandler` 에서 `SERVER_ERROR` 500 으로 매핑. 운영 시 알림 실패는 따로 모니터링 필요 |

---

## 10. 더 알아보기

- [`CLAUDE.md`](../CLAUDE.md) — Claude Code AI 용 프로젝트 가이드. 컨벤션 요약본
- `.claude/skills/code-implementation-rules/SKILL.md` — **OOP / 헥사고날 19개 규칙 본문**. 새 기능 짤 때 반드시 참조
- `.claude/skills/clean-code/SKILL.md` — Robert C. Martin Clean Code 원칙
- `.claude/skills/full-code-rules/SKILL.md` — 프로젝트 특화 Kotlin 패턴 (VO, 일급 컬렉션, 디미터, 팩토리)
- `.claude/skills/kotest-writing/SKILL.md` — 테스트 컨벤션
- `.claude/skills/rest-docs-writing/SKILL.md` — REST Docs 작성 컨벤션
- `boot/gb-boot-web/src/docs/asciidoc/` — REST Docs 소스. 빌드하면 `build/docs/asciidoc/*.html` 로 변환
- `meet-again-backend` (외부 참조 프로젝트) — `/Users/jowonjin/IdeaProjects/konkuk/meet-again-backend/` (로컬). 본 프로젝트의 헥사고날 패턴은 이걸 참조해서 구축됨

---

## 11. 자주 보는 코드 위치 — 빠른 점프

| 찾을 것 | 경로 |
|---|---|
| Spring Boot 진입점 | `boot/gb-boot-web/src/main/kotlin/com/beside/groubing/GroubingServerApplication.kt` |
| Security 설정 + 인증 면제 화이트리스트 | `boot/gb-boot-web/.../global/config/SecurityConfig.kt` |
| 전역 예외 핸들러 | `boot/gb-boot-web/.../global/handler/GlobalExceptionHandler.kt` |
| JWT 토큰 발급/검증 | `infrastructure/support/gb-jwt-core/.../JwtProvider.kt` |
| 비밀번호 BCrypt | `infrastructure/support/gb-crypto-core/.../BCryptPasswordEncryptor.kt` |
| FCM 초기화 | `infrastructure/support/gb-fcm-sender/.../FcmConfig.kt` |
| BingoBoard aggregate root | `domain/gb-domain-core/.../bingo/domain/BingoBoard.kt` |
| 빙고 알고리즘 | `domain/gb-domain-core/.../bingo/domain/map/BingoMap.kt`, `BingoLine.kt` |
| 응답 공통 래퍼 | `boot/gb-boot-web/.../global/response/ApiResponse.kt` |
| 페이지 응답 래퍼 | `boot/gb-boot-web/.../global/response/PageResponse.kt` |
| REST Docs DSL | `boot/gb-boot-web/src/test/.../docs/RestDocsExtensions.kt` |
| 테스트용 ApiTest 메타 어노테이션 | `boot/gb-boot-web/src/test/.../config/ApiTest.kt` |

---

## 12. 첫 PR 까지의 흐름 (요약)

1. 이 문서 한번 훑기 (15분)
2. `./gradlew bootRun --args='--spring.profiles.active=local'` 띄워서 `http://localhost:8080/docs/api.html` 열기 — 어떤 API 가 있는지 감 잡기 (10분)
3. 한 use case (예: `BingoBoardCreateApi`) 의 컨트롤러 → 서비스 → 어댑터 → 엔티티 한번 따라가 보기 (20분)
4. `.claude/skills/code-implementation-rules/SKILL.md` 의 19개 규칙 읽기 (15분)
5. 작은 변경부터 — 새 검증 추가, 새 응답 필드 추가 같은 1-파일 변경
6. PR 올리기 전 `./gradlew test` 통과 확인 (REST Docs 까지 같이 빌드됨)

---

문서가 부정확하거나 빠진 부분이 있으면 PR 로 갱신해주세요.
