---
name: kotest-writing
description: "KoTest 테스트 코드 작성 가이드. 이 프로젝트의 테스트 컨벤션, Spec 스타일 선택 기준, Mockk 패턴, Fixture 작성법, DB 통합 테스트 설정, Value Object 테스트 전략을 포함한다. kotest-writer 에이전트가 테스트를 작성할 때 항상 참조해야 하는 스킬이다."
---

# KoTest 테스트 작성 가이드

이 프로젝트의 테스트 코드를 작성할 때 따라야 하는 구체적인 패턴과 규칙을 정의한다.
REST Docs 관련 내용은 별도 스킬에서 다루므로 여기서는 제외한다.

## Spec 스타일 선택 기준

테스트 대상의 성격에 따라 적절한 KoTest Spec 스타일을 선택한다.

| 테스트 대상 | Spec 스타일 | 이유 |
|------------|-----------|------|
| Value Object (원시값 포장) | `FunSpec` + `context` | 생성 성공/실패 케이스를 context로 분리 |
| Domain 모델 행위 | `FunSpec` + `context` | 도메인 메서드별 context 그룹핑 |
| DB DAO 통합 테스트 | `DatabaseTestConfig` 상속 (FunSpec 기반) | Exposed ORM 스키마 관리 필요 |
| 유틸리티 / 단순 함수 | `FunSpec` | 간단한 입출력 검증 |

이 프로젝트에서는 `FunSpec`이 기본이다. `BehaviorSpec`(Given/When/Then)은 에이전트 정의에 있지만, 실제 코드베이스에서는 `FunSpec` + `context` 패턴을 사용하고 있으므로 이를 따른다.

## 테스트 파일 구조

### 기본 구조
```kotlin
class SomeServiceTest : FunSpec({

    // 1. mock 선언
    val mockRepository = mockk<SomeRepository>()
    val service = SomeService(mockRepository)

    // 2. context로 메서드/시나리오 그룹핑
    context("메서드명 또는 시나리오") {

        test("성공 케이스를 설명하는 한국어") {
            // Given
            every { mockRepository.findById(1L) } returns someEntity

            // When
            val result = service.doSomething(1L)

            // Then
            result shouldBe expected
        }

        test("실패 케이스를 설명하는 한국어") {
            every { mockRepository.findById(999L) } returns null

            shouldThrow<IllegalArgumentException> {
                service.doSomething(999L)
            }.message shouldBe "존재하지 않는 엔티티입니다."
        }
    }
})
```

### 테스트 대상 선정 기준

테스트는 **비즈니스 로직이 있는 코드에만** 작성한다. 단순 값 매핑, 위임만 하는 코드는 테스트하지 않는다.

| 테스트 여부 | 대상 | 예시 |
|:-:|------|------|
| O | DAO (DB 통합 테스트) | `PostLikeDao`, `CommentCommandDao` — 실제 DB 쿼리가 의도대로 동작하는지 검증 |
| O | 값 변환/계산이 있는 코드 | `TimeAgoCalculator.calculate()`, `PostResponse.from(postWithAuthor)` (timeAgo 변환) |
| O | 유효성 검증이 있는 도메인 객체 | `FourDigit`, `Year`, `Comment.validateCanBeParent()` |
| O | 조합 로직이 있는 Service | `PostQueryService.find()` (조회 + 닉네임 조합) |
| O | 분기/판단이 있는 도메인 컴포넌트 | `CommentValidator.validatePostExists()` |
| X | 단순 값 매핑만 하는 Response DTO | `PostLikeResponse.from(result)` — liked, likeCount를 그대로 옮기기만 함 |
| X | 생성자 호출만 하는 팩토리 메서드 | `PostLikeResult.liked(likeCount)` — 인자를 그대로 전달 |
| X | 비즈니스 로직 없는 Service | 포트 호출 → 결과 반환만 하는 Service. mock verify만으로 구성된 테스트는 가치 없음. `PostLikeService.like()`, `CommentLikeService.unlike()` 등 |
| X | 단순 위임만 하는 Repository | DAO 호출 + toDomain() 변환만 하는 포트 구현체 |

**판단 기준: "이 코드가 잘못 구현되면 테스트 없이도 바로 알 수 있는가?"** 단순 매핑은 컴파일러가 잡아주므로 테스트가 불필요하다.

### 필수 테스트 케이스 범위

테스트는 **성공 케이스와 실패 케이스를 모두** 작성한다. 성공만 테스트하고 실패를 생략하지 않는다.

| 테스트 대상 | 필수 성공 케이스 | 필수 실패 케이스 |
|------------|----------------|----------------|
| DAO (DB 통합) | CRUD 동작 검증, 조건 쿼리 결과 | 존재하지 않는 데이터 조회, 유니크 제약 위반 |
| 도메인 모델 생성 | 정상 생성 + 필드 검증 | 유효성 검증 실패 (빈값, 초과, 형식 오류) |
| 도메인 행위 메서드 | 정상 동작 | 예외 발생 조건 |
| Service | 정상 흐름 (조합 결과) | 의존 포트에서 예외 전파 |
| API Controller | 정상 요청 → 2xx | 유효성 검증 실패 → 400, 인증 실패 → 401 등 |
| Value Object | 유효한 값 생성 | 경계값 실패, 형식 오류 |

```kotlin
context("create") {
    test("정상적으로 생성한다") { ... }          // 성공
    test("제목이 40자를 초과하면 400을 반환한다") { ... }  // 실패
    test("내용이 비어있으면 400을 반환한다") { ... }        // 실패
}
```

### 테스트 이름 규칙
- 한국어로 작성 (프로젝트 컨벤션)
- 메서드명은 context에, 구체적인 동작은 test에 기술
- 예: `context("validateNewMember")` → `test("중복된 닉네임이 있으면 예외가 발생한다")`

### 파일 배치
소스 코드의 패키지 구조를 그대로 미러링한다:
```
src/main/kotlin/com/konkuk/ma/domain/member/domain/Member.kt
→ src/test/kotlin/com/konkuk/ma/domain/member/domain/MemberTest.kt
```

## Mockk 사용 패턴

### 기본 스터빙
```kotlin
val mockRepo = mockk<SomeRepository>()

// 반환값 스터빙
every { mockRepo.findById(1L) } returns entity

// void 메서드
every { mockRepo.delete(any()) } just runs

// 예외 스터빙
every { mockRepo.findById(999L) } throws NotFoundException("not found")

// Boolean 반환
every { mockRepo.existsByEmail("test@example.com") } returns false
```

### mock 선언 위치
- FunSpec 람다 최상단에 선언 (모든 테스트에서 공유)
- 테스트 대상 객체도 같은 위치에서 mock을 주입받아 생성

```kotlin
class MemberValidatorTest : FunSpec({
    val memberQueryRepository = mockk<MemberQueryRepository>()
    val smsRepository = mockk<SmsRepository>()
    val memberValidator = MemberValidator(memberQueryRepository, smsRepository)

    context("validateNewMember") {
        test("...") { /* ... */ }
    }
})
```

### 주의사항
- `relaxed = true`는 가급적 사용하지 않는다. 명시적 스터빙이 테스트 의도를 더 잘 드러낸다
- `@MockkBean`은 `@WebMvcTest` 기반 API 테스트에서만 사용한다 (이 스킬 범위 밖)
- `verify { ... }`는 부수효과 검증이 필요한 경우에만 사용한다

## Fixture 패턴

테스트에서 반복되는 도메인 객체 생성은 Fixture `object`로 추출한다.

### Fixture 작성 규칙

```kotlin
object SomeFixture {
    fun create(
        // 모든 파라미터에 기본값 제공
        id: Long = 1L,
        name: String = "기본이름",
        email: String = "default@example.com",
        // nullable 필드도 기본값 제공
        optionalField: String? = null
    ): SomeDomainObject {
        return SomeDomainObject(
            id = id,
            name = name,
            email = email,
            optionalField = optionalField
        )
    }
}
```

핵심 원칙:
- `object` 키워드 사용 (싱글톤)
- `create()` 팩토리 메서드 하나로 통일
- 모든 파라미터에 합리적인 기본값을 제공하여, 테스트에서 관심 있는 필드만 오버라이드할 수 있게 한다
- Fixture 파일은 같은 모듈의 `test/kotlin/.../fixture/` 패키지에 배치

### Fixture 사용 예시
테스트에서는 검증하려는 필드만 명시적으로 전달한다:
```kotlin
// 이름과 성별만 관심 있는 테스트
val targetInfo = TargetInfoFixture.create(
    targetName = "홍길동",
    targetGender = Gender.MALE
)

// null 케이스 테스트
val targetInfo = TargetInfoFixture.create(
    middleNumber = null,
    lastNumber = null
)
```

### 하드코딩 최소화 규칙
테스트 코드에서 같은 값을 여러 곳에 반복하지 않는다. Fixture가 반환한 객체의 프로퍼티를 참조하여 mock 스터빙과 검증에 사용한다.

**나쁜 예 (하드코딩 반복):**
```kotlin
val targetInfo = TargetInfoFixture.create(targetName = "홍길동")
val member = MemberFixture.create(name = "홍길동", email = "target@example.com")

// "홍길동"과 "target@example.com"이 여러 곳에 반복됨
every { memberQueryRepository.findByNames(setOf("홍길동")) } returns listOf(member)
result.data[0].targetEmail shouldBe "target@example.com"
```

**좋은 예 (객체 프로퍼티 참조):**
```kotlin
val targetInfo = TargetInfoFixture.create()
val member = MemberFixture.create(name = targetInfo.targetName)

// Fixture 객체의 프로퍼티를 참조하여 값 동기화
every { memberQueryRepository.findByNames(targetInfos.extractTargetNames()) } returns listOf(member)
result.data[0].targetEmail shouldBe member.email
```

핵심 원칙:
- Fixture 기본값으로 충분하면 파라미터를 전달하지 않는다
- 테스트에서 관계가 있는 값은 객체 프로퍼티를 참조하여 연결한다 (예: `member.email`, `targetInfo.targetName`)
- mock 스터빙에도 도메인 객체의 메서드를 활용한다 (예: `targetInfos.extractTargetNames()`)
- 검증(assertion)에서도 문자열 리터럴 대신 객체 프로퍼티를 참조한다

## Value Object 테스트 전략

원시값 포장 클래스(`FourDigit`, `Year`, `PhoneNumber` 등)는 생성 시 유효성 검증이 핵심이므로, 아래 구조를 따른다:

```kotlin
class PhoneNumberTest : FunSpec({

    context("PhoneNumber 객체 생성 테스트") {
        // 1. 정상 케이스 (다양한 입력 형식)
        test("유효한 11자리 휴대폰 번호로 객체 생성 성공") { /* ... */ }
        test("하이픈이 포함된 번호로 객체 생성 성공") { /* ... */ }

        // 2. 경계값 / 엣지 케이스
        test("10자리 번호로 객체 생성 성공") { /* ... */ }

        // 3. 실패 케이스 (다양한 잘못된 입력)
        test("허용되지 않는 앞자리로 객체 생성 실패") {
            shouldThrow<IllegalArgumentException> {
                PhoneNumber("01112345678")
            }.message shouldBe "앞자리는 010만 허용됩니다."
        }

        // 4. 빈값 / 극단적 입력
        test("빈 문자열로 객체 생성 실패") { /* ... */ }
    }

    context("파생 속성 테스트") {
        test("formatted는 하이픈으로 구분된 형식을 반환한다") { /* ... */ }
    }
})
```

커버해야 하는 것:
- 정상 생성 + 프로퍼티 값 검증
- 다양한 입력 형식 (하이픈, 공백, 혼합 등)
- init 블록의 require/check 조건별 실패 케이스
- 예외 메시지까지 검증 (`shouldThrow<...> { ... }.message shouldBe "..."`)
- 파생 속성(computed property) 검증

## DB 통합 테스트 (Exposed ORM)

이 프로젝트는 JPA가 아닌 Jetbrains Exposed ORM을 사용한다. DB 테스트는 특수한 설정이 필요하다.

### 설정 구조
```kotlin
@ContextConfiguration(classes = [TestDatabaseConfig::class, SomeDao::class])
@DatabaseTest
class SomeDaoTest(
    private val someDao: SomeDao
) : DatabaseTestConfig() {

    init {
        test("조회 성공 케이스") {
            // Given - Exposed DSL로 직접 데이터 삽입
            SomeTable.insert {
                it[name] = "테스트"
                it[email] = "test@example.com"
            }

            // When
            val result = someDao.findByName("테스트")

            // Then
            result shouldBe true
        }
    }
}
```

핵심 포인트:
- `DatabaseTestConfig` 상속 필수 — `SpringExtension`, 스키마 생성/삭제를 자동 처리
- `@DatabaseTest` — 인메모리 DB, test 프로필, `@Transactional` 설정
- `@ContextConfiguration` — 테스트에 필요한 Config와 DAO 클래스만 명시
- 테스트 데이터는 `Table.insert { }` DSL로 직접 삽입 (Repository 사용 금지)
- `init { }` 블록 안에 테스트 작성 (DatabaseTestConfig이 FunSpec 기반이므로)

### Insert 헬퍼 함수 패턴
DB 통합 테스트에서는 도메인 Fixture를 사용할 수 없다 (Exposed DSL로 직접 삽입해야 하므로). 대신 테스트 클래스 안에 `insertXxx()` 헬퍼 함수를 만들어 하드코딩 중복을 줄인다.

```kotlin
@DatabaseTest
class MemberQueryDaoTest(
    private val memberQueryDao: MemberQueryDao
) : DatabaseTestConfig() {

    // 관심 있는 필드만 파라미터로, 나머지는 기본값
    private fun insertMember(
        email: String = "test@example.com",
        nickname: String = "testNickname"
    ) {
        MemberTable.insert {
            it[MemberTable.email] = email
            it[password] = "password123"
            it[MemberTable.nickname] = nickname
            it[gender] = "MALE"
            it[phoneNumber] = "01012345678"
            it[name] = "김테스트"
            it[birthDate] = LocalDate.of(1990, 1, 1)
            it[region] = "SEOUL"
        }
    }

    init {
        test("닉네임이 존재하는 경우 true를 반환한다") {
            val nickname = "testNickname"
            insertMember(nickname = nickname)  // 관심 필드만 전달

            memberQueryDao.existsByNickname(nickname) shouldBe true
        }
    }
}
```

핵심 원칙:
- 도메인 Fixture의 `create()` 패턴과 동일한 아이디어 — 기본값 + 오버라이드
- 테스트마다 `MemberTable.insert { ... }` 전체를 반복하지 않는다
- 여러 테이블 insert가 필요하면 각 테이블별 헬퍼 함수를 만든다

### 주의사항
- JPA의 `@Entity`, `@Repository` 어노테이션 사용 금지
- `EntityManager`, `TestEntityManager` 사용 금지
- Exposed의 `Table` 객체와 DSL 문법 사용

## E2E 통합 테스트 (API → Service → Domain → Infrastructure)

API부터 DB까지 전체 레이어를 관통하는 통합 테스트가 필요한 경우, `@SpringBootTest`와 실제 DB(인메모리 H2)를 사용한다.

### 언제 작성하는가
- 여러 레이어가 조합되어 동작하는 핵심 시나리오 검증 (예: 회원가입, 매칭 결과 조회)
- Mock으로는 검증이 어려운 트랜잭션, 데이터 정합성 문제
- 단위 테스트만으로는 확신이 부족한 복잡한 흐름

### 설정 구조
```kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class SignUpIntegrationTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper
) : FunSpec() {

    init {
        test("회원가입 전체 흐름 - 요청부터 DB 저장까지") {
            // Given
            val request = mapOf(
                "email" to "newuser@example.com",
                "password" to "password123",
                // ...
            )

            // When - 실제 API 호출
            mockMvc.post("/api/auth/sign-up") {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsString(request)
            }

            // Then - DB에서 실제 데이터 확인
            val saved = MemberTable.select(MemberTable.email)
                .where { MemberTable.email eq "newuser@example.com" }
                .singleOrNull()

            saved shouldNotBe null
        }
    }
}
```

### 단위 테스트 vs E2E 통합 테스트 구분

| 구분 | 단위 테스트 | E2E 통합 테스트 |
|------|-----------|----------------|
| 범위 | 클래스/메서드 1개 | API → Service → Domain → DB |
| DB | Mock | 실제 인메모리 DB (H2) |
| 속도 | 빠름 | 느림 |
| 목적 | 로직 정확성 | 레이어 간 연결, 데이터 흐름 |
| 파일 위치 | 각 모듈의 test/ | boot 모듈의 test/ |

### 핵심 원칙
- 단위 테스트가 기본이다. E2E는 핵심 시나리오에만 작성한다
- `@Transactional`로 테스트 간 데이터 격리
- 외부 서비스(SMS, 파일 스토리지 등)는 Mock 또는 테스트용 구현체 사용
- E2E 테스트 파일명에 `IntegrationTest` 접미사 사용

## KoTest Matchers

프로젝트에서 사용하는 주요 matcher:

```kotlin
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldContain
import io.kotest.assertions.throwables.shouldThrow

// 동등성
result shouldBe expected
result shouldNotBe other

// Boolean
result.shouldBeTrue()
result.shouldBeFalse()

// 컬렉션
list shouldHaveSize 3
list shouldContain element

// 예외 (메시지까지 검증)
shouldThrow<IllegalArgumentException> {
    doSomething()
}.message shouldBe "에러 메시지"

// 예외가 발생하지 않음을 검증 (성공 케이스)
// → 별도 assertion 없이 메서드 호출만으로 충분
memberValidator.validateNewMember(newMember) // 예외 없으면 통과
```

## 테스트 커버리지 체크리스트

테스트를 작성할 때 아래 항목을 확인한다:

1. **Happy Path**: 정상 동작 시나리오
2. **예외/실패 케이스**: 각 예외 조건별 테스트 + 예외 메시지 검증
3. **경계값**: null, 빈 문자열, 빈 컬렉션, 최소/최대값
4. **검증 순서**: 여러 검증이 있을 때 첫 번째 실패가 올바르게 전파되는지
5. **도메인 행위**: 객체 내부 로직이 올바르게 동작하는지 (getter 꺼내서 비교 X)

## 절대 하지 말 것

- JUnit 어노테이션 (`@Test`, `@BeforeEach`, `@AfterEach`) 사용 금지
- Mockito 사용 금지 — 반드시 Mockk
- JPA/Hibernate 패턴 사용 금지 — Exposed ORM
- 테스트 간 상태 의존 금지 — 각 테스트는 독립적
- `Thread.sleep()`으로 비동기 대기 금지 (시간 기반 테스트 제외)
- 테스트에서 실제 외부 서비스(API, SMS 등) 호출 금지
