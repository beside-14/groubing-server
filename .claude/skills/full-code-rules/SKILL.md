---
name: code-implementation-rules
description: "Kotlin 코드 구현 시 따라야 하는 OOP 원칙과 패턴 가이드. 도메인 객체 행위 부여, 원시값 포장(Value Object), 일급 컬렉션, 디미터 법칙, 포트 인터페이스 규칙, 팩토리 메서드, 성능 고려사항을 포함한다. 코드를 작성하거나 수정할 때, 특히 도메인 모델, 서비스, 포트 인터페이스를 다룰 때 항상 참조해야 한다."
---

# 코드 구현 규칙 (Code Implementation Rules)

코드를 작성하거나 수정할 때 반드시 따라야 하는 OOP 원칙과 구현 패턴을 정의한다.

## 0. SOLID 원칙

다음 SOLID 원칙을 반드시 준수한다:

- **SRP (단일 책임 원칙)**: 클래스/함수는 하나의 책임만 가진다. 변경 이유가 하나여야 한다.
- **OCP (개방-폐쇄 원칙)**: 확장에는 열려 있고 수정에는 닫혀 있어야 한다. 하드코딩된 상수(PAGE_SIZE 등)를 클래스에 박아놓지 말고 파라미터로 외부에서 주입받도록 설계한다.
- **DIP (의존성 역전 원칙)**: 상위 모듈이 하위 모듈에 의존하지 않는다. 둘 다 추상화(포트 인터페이스)에 의존한다. 이 프로젝트의 헥사고날 아키텍처가 이를 따른다.

## 1. 도메인 객체에 행위 부여

외부에서 getter로 꺼내서 판단하지 말고, 객체 스스로 판단/행동하게 한다.

```kotlin
// BAD - 외부에서 판단
if (target.name == targetInfo.targetName && target.gender == targetInfo.targetGender) { ... }

// GOOD - 객체에게 메시지를 보낸다
target.matchesNameAndGender(name, gender)
```

## 2. 원시값 포장 (Value Object)

도메인에서 의미 있는 값은 Value Object로 감싼다. 생성 시 유효성 검증을 포함한다.

```kotlin
class FourDigit(val value: String) {
    init {
        require(value.length == 4 && value.all { it.isDigit() }) {
            "4자리 숫자여야 합니다: $value"
        }
    }
}

class Year(val value: Int) {
    init {
        require(value in 1900..2100) { "유효하지 않은 연도: $value" }
    }
}
```

## 3. 일급 컬렉션

컬렉션을 감싸는 도메인 객체를 만들어 관련 로직을 응집시킨다. **단, 도메인 행위가 있는 경우에만 사용한다.** 행위 없이 `List`를 감싸기만 하는 일급 컬렉션은 만들지 않는다.

### 규칙
- **행위가 있는 경우에만** 일급 컬렉션을 생성 (필터링, 변환, 집계, 조합 등)
- 행위가 없으면 `List<DomainObject>`를 그대로 사용
- 멤버 변수명은 반드시 `val data`로 통일
- `private` 접근 제한자 사용 금지 (외부에서 `data`에 접근 가능해야 함)
- 컬렉션 관련 행위(필터링, 변환, 집계)를 일급 컬렉션 내부에 정의
- `companion object` 팩토리 메서드로 생성 의도를 드러냄
- **DAO는 항상 `List<Entity>`를 반환**, Repository(포트 구현체)는 `List<DomainObject>`를 반환, Service에서 비즈니스 로직이 필요한 시점에 일급 컬렉션으로 감쌈

```kotlin
class Targets(val data: List<Target>) {
    fun filterCandidates(name: String, gender: Gender): List<Target> {
        return data.filter { it.matchesNameAndGender(name, gender) }
    }

    companion object {
        fun from(members: List<Member>): Targets {
            return Targets(members.map { Target.create(it) })
        }
    }
}

class MatchingResults(val data: List<MatchingResult>) {
    fun targetInfoIds(): List<Long> {
        return data.map { it.targetInfoId }.distinct()
    }

    fun filterNew(existing: MatchingResults): MatchingResults {
        val existingKeys = existing.data.map { it.uniqueKey() }.toSet()
        return MatchingResults(data.filter { it.uniqueKey() !in existingKeys })
    }

    companion object {
        fun merge(dataList: List<MatchingResults>): MatchingResults {
            return MatchingResults(dataList.flatMap { it.data })
        }
    }
}
```

## 4. 디미터 법칙 (Law of Demeter)

직접 협력하는 객체에게만 메시지를 보낸다. 체이닝을 통해 내부 구조를 노출하지 않는다.

```kotlin
// BAD - 내부 구조 노출
val middleNumber = member.phoneNumber.middleNumber

// GOOD - 필요한 정보만 Target으로 변환하여 사용
val target = Target.create(member)
```

## 5. 포트(Port) 인터페이스는 도메인 타입 사용

포트 인터페이스의 파라미터와 반환 타입은 도메인 객체를 사용한다. 단, **일급 컬렉션은 포트 반환 타입으로 사용하지 않는다.** 포트는 `List<DomainObject>`를 반환하고, 비즈니스 로직이 필요한 시점에 Service에서 일급 컬렉션으로 감싸서 사용한다.

```kotlin
// BAD - 포트가 일급 컬렉션 반환
interface MatchingResultRepository {
    fun saveAll(matchingResults: MatchingResults)
    fun findExistingMatchingResults(targetInfoIds: List<Long>): MatchingResults
}

// GOOD - 포트는 List 반환, Service에서 일급 컬렉션으로 감싸서 사용
interface MatchingResultRepository {
    fun saveAll(matchingResults: List<MatchingResult>)
    fun findExistingMatchingResults(targetInfoIds: List<Long>): List<MatchingResult>
}

// Service에서 비즈니스 로직이 필요한 시점에 일급 컬렉션으로 감쌈
val matchingResults = MatchingResults(matchingResultRepository.find(email))
val filtered = matchingResults.filterNew(existing)
```

단건 조회 포트 메서드(`findById`, `findByEmail` 등)는 **non-null을 반환**한다. 엔티티가 없으면 **Repository 구현체에서 예외를 던진다**. Service에서 null 체크를 하지 않는다.

nullable 반환이 필요한 경우 메서드명에 `OrNull` 접미사를 붙인다: `findByIdOrNull`, `findByEmailOrNull`.

```kotlin
// BAD - Service에서 null 체크 + 예외 처리
fun findDetailById(id: Long): MatchingResult {
    val result = matchingResultRepository.findById(id)
        ?: throw EntityNotFoundException("MatchingResult", "id", id.toString())  // 서비스에 비즈니스 로직
    return result
}

// GOOD - 포트는 non-null 반환, 예외는 Repository 구현체에서
// 포트
interface MatchingResultRepository {
    fun findById(id: Long): MatchingResult  // non-null
}

// Repository 구현체
override fun findById(id: Long): MatchingResult {
    return dao.findById(id)?.toDomain()
        ?: throw EntityNotFoundException("MatchingResult", "id", id.toString())
}

// Service - null 체크 없이 깔끔
fun findDetailById(id: Long): MatchingResult {
    val result = matchingResultRepository.findById(id)
    result.validateOwnership(email)
    return result
}
```

## 6. 비즈니스 로직은 도메인 객체 안에

Writer/Controller 같은 인프라 계층에 비즈니스 로직을 두지 않는다. 도메인 객체에게 위임한다.

```kotlin
// BAD - Writer에서 비즈니스 로직 처리
fun matchingWriter(): ItemWriter<TargetInfo> {
    return ItemWriter { chunk ->
        chunk.items.forEach { targetInfo ->
            val members = memberQueryRepository.findByNames(setOf(targetInfo.targetName))
            members.filter { it.name == targetInfo.targetName && it.gender == targetInfo.targetGender }
                .forEach { member -> /* 매칭 로직... */ }
        }
    }
}

// GOOD - 도메인 객체에 위임
fun matchingWriter(): ItemWriter<TargetInfo> {
    return ItemWriter { chunk ->
        val targetInfos = TargetInfos(chunk.items)
        val targets = Targets.from(memberQueryRepository.findByNames(targetInfos.targetNames()))
        val matchingResults = targetInfos.makeMatchingResults(targets)

        val existing = MatchingResults(matchingResultRepository.findExistingMatchingResults(matchingResults.targetInfoIds()))
        val newResults = matchingResults.filterNew(existing)
        matchingResultRepository.saveAll(newResults.data)
    }
}
```

## 7. 팩토리 메서드 활용

복잡한 객체 생성은 `companion object`의 팩토리 메서드로 의도를 드러낸다. **팩토리 메서드는 반드시 자기 자신의 단일 인스턴스를 반환**한다. `List<T>`를 반환하는 것은 팩토리 메서드가 아니라 컬렉션 조립 로직이므로, 일급 컬렉션이나 호출하는 쪽에서 처리한다.

```kotlin
// GOOD - 팩토리 메서드는 단일 인스턴스 반환
class Target(
    val email: String,
    val name: String,
    val gender: Gender,
    // ...
) {
    companion object {
        fun create(member: Member): Target {
            return Target(
                email = member.email,
                name = member.name,
                gender = member.gender,
                middleNumber = member.phoneNumber.middleNumber,
                lastNumber = member.phoneNumber.lastNumber,
                year = member.getYear(),
                month = member.getMonth(),
                day = member.getDay(),
                region = member.region
            )
        }
    }
}

// BAD - companion object에서 List<T>를 반환하는 메서드
class PostSummary(...) {
    companion object {
        fun listFrom(posts: List<Post>, members: List<Member>): List<PostSummary>  // 팩토리가 아닌 컬렉션 조립
    }
}

// GOOD - 컬렉션 변환은 일급 컬렉션의 책임
class Posts(val data: List<Post>) {
    fun combineWithAuthors(members: Members): List<PostWithAuthor> {
        return data.map { post ->
            PostWithAuthor(
                post = post,
                nickname = members.findNicknameByEmail(post.authorEmail),
            )
        }
    }
}
```

## 8. DAO는 Entity를 반환, Entity에서 도메인으로 변환

QueryDao는 도메인 객체를 직접 생성하지 않는다. Entity 클래스를 반환하고, Entity의 `toDomain()` 메서드로 도메인 객체로 변환한다. 이렇게 하면 DB 컬럼 매핑(인프라 관심사)과 도메인 변환 로직이 분리된다.

```kotlin
// BAD - DAO에서 도메인 객체를 직접 생성
class MatchingResultQueryDao {
    fun find(email: String): List<MatchingResult> {
        return MatchingResultTable.select(...)
            .where { MatchingResultTable.registerEmail eq email }
            .map { row ->
                MatchingResult(
                    registerEmail = row[MatchingResultTable.registerEmail],
                    targetInfoId = row[MatchingResultTable.targetInfoId],
                    // ... 매핑 로직이 DAO에 흩어짐
                )
            }
    }
}

// GOOD - DAO는 Entity 반환, Entity가 toDomain() 제공
class MatchingResultQueryDao {
    fun find(email: String): List<MatchingResultEntity> {
        return MatchingResultTable.select(...)
            .where { MatchingResultTable.registerEmail eq email }
            .map { row -> MatchingResultEntity.from(row) }
    }
}

class MatchingResultEntity(
    val registerEmail: String,
    val targetInfoId: Long,
    // ...
) {
    fun toDomain(): MatchingResult { ... }

    companion object {
        fun from(row: ResultRow): MatchingResultEntity { ... }
    }
}
```

규칙:
- QueryDao의 반환 타입은 `List<XxxEntity>` 또는 `XxxEntity?`
- Entity는 `toDomain()` 메서드로 도메인 객체 변환
- Entity는 `companion object`의 `from(row: ResultRow)` 팩토리 메서드로 ResultRow에서 생성
- Repository(포트 구현체)에서 `entity.toDomain()` 호출하여 도메인 객체로 변환 후 반환

## 9. 하드코딩 지양 — 상수 또는 파라미터 사용

매직 넘버, 매직 스트링을 코드에 직접 쓰지 않는다. 의미를 가진 상수로 추출하되, 변동 가능성이 높은 값은 외부에서 주입받도록 파라미터로 처리한다.

```kotlin
// BAD - 하드코딩
val token = jwtGenerator.generate(email, 3600)
val results = repository.findAll().take(10)

// GOOD - 고정값은 상수로
companion object {
    private const val TOKEN_EXPIRY_SECONDS = 3600L
    private const val DEFAULT_PAGE_SIZE = 10
}

val token = jwtGenerator.generate(email, TOKEN_EXPIRY_SECONDS)
val results = repository.findAll().take(DEFAULT_PAGE_SIZE)

// GOOD - 변동 가능성 높은 값은 파라미터로
class MatchingConfig(
    val showingExpiryDays: Long = 30,
    val matchingExpiryDays: Long = 210,
)
```

판단 기준:
- **상수**: 비즈니스 규칙상 거의 바뀌지 않는 값 (예: 전화번호 자릿수, 비밀번호 최소 길이)
- **파라미터**: 운영 중 변경될 수 있는 값 (예: 만료일, 페이지 크기, 재시도 횟수)

**반환 타입은 의도를 드러내는 객체로** — `Pair`, `Triple`, `Map<Long, Int>`, `List<Pair<A, B>>` 등 제네릭 컬렉션을 반환하지 않는다. 호출부에서 `first`, `second`, `key`, `value`로는 의미를 파악할 수 없다. 의도를 드러내는 도메인 객체나 전용 클래스를 만들어 반환한다.

```kotlin
// BAD - 의도 불명확
fun previewFor(parentId: Long): Pair<List<Comment>, Int>
fun countByPostIds(postIds: List<Long>): Map<Long, Int>

// GOOD - 의도를 드러내는 객체 반환
fun previewFor(parent: Comment): GroupedComment
fun countByPostIds(postIds: List<Long>): List<PostLikeCount>
```

## 10. Validation 메시지와 패턴은 상수로 관리

Request DTO의 `@NotBlank`, `@Pattern`, `@Email` 등 Bean Validation 어노테이션의 `message`와 `regexp`는 하드코딩하지 않는다. `ValidationMessages`와 `ValidationPatterns`에 정의된 상수를 사용한다.

```kotlin
// BAD - 하드코딩
@field:NotBlank(message = "이메일은 필수입니다.")
@field:Email(message = "유효하지 않은 이메일 형식입니다.")

// GOOD - 상수 참조
@field:NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
@field:Email(message = ValidationMessages.EMAIL_INVALID)
@field:Pattern(regexp = ValidationPatterns.PASSWORD, message = ValidationMessages.PASSWORD_INVALID)
```

- `ValidationPatterns` — 정규표현식 패턴 (`NICKNAME`, `PASSWORD`, `PHONE_NUMBER` 등)
- `ValidationMessages` — 검증 실패 메시지 (`EMAIL_REQUIRED`, `PASSWORD_INVALID` 등)
- 새로운 검증이 필요하면 해당 object에 상수를 먼저 추가한 후 사용

## 11. Service는 Service를 참조하지 않는다

Service가 다른 Service를 의존하면 테스트 시 Mock 체인이 깊어지고, 순환 참조 위험이 생긴다. Service는 **포트(인터페이스)**만 의존한다.

```kotlin
// BAD - Service가 Service를 참조
class SignUpService(
    private val memberPhotoService: MemberPhotoService  // Service → Service 참조
)

// GOOD - Service는 포트만 참조
class SignUpService(
    private val fileStorage: FileStorage,               // 포트
    private val memberPhotoRepository: MemberPhotoRepository  // 포트
)
```

로직 재사용이 필요하면 도메인 객체에 행위를 부여하거나, 공통 로직을 도메인 서비스(순수 함수)로 분리한다.

## 12. Service는 비즈니스 로직을 포함하지 않는다 — 조합만 담당

Service 클래스는 비즈니스 로직을 직접 구현하지 않는다. 비즈니스 로직을 담당하는 도메인 객체/컴포넌트들을 **조합(orchestrate)**하는 역할만 한다.

```kotlin
// BAD - Service에 비즈니스 로직이 직접 존재
@Service
class MemberPhotoService(
    private val fileStorage: FileStorage,
    private val thumbnailGenerator: ThumbnailGenerator,
    private val memberPhotoRepository: MemberPhotoRepository
) {
    fun upload(email: String, photoFile: PhotoFile) {
        // 디렉토리 생성, 파일 저장, 썸네일 생성, 파일명 생성... 비즈니스 로직이 서비스에 가득
        val directory = StoragePath.of(StorageDomainType.MEMBER, StorageUsageType.PROFILE, email)
        val filePath = fileStorage.store(directory.value, photoFile)
        val thumbnailBytes = thumbnailGenerator.generate(photoFile.content, 400)
        val thumbnailDir = StoragePath.of(StorageDomainType.MEMBER, StorageUsageType.THUMBNAIL, email)
        val thumbnailPath = fileStorage.storeBytes(thumbnailDir.value, "thumb_${photoFile.originalFileName}", thumbnailBytes)
        memberPhotoRepository.save(NewPhoto.create(email, filePath, photoFile.originalFileName, thumbnailPath))
    }
}

// GOOD - Service는 도메인 컴포넌트를 조합만 한다
@Service
class MemberPhotoService(
    private val memberPhotoProcessor: MemberPhotoProcessor,  // 파일 처리 담당 도메인 컴포넌트
    private val memberPhotoRepository: MemberPhotoRepository  // 포트
) {
    fun upload(email: String, photoFile: PhotoFile) {
        delete(email)
        val processed = memberPhotoProcessor.process(email, photoFile)  // 위임
        val newPhoto = NewPhoto.create(email, processed.filePath, photoFile.originalFileName, processed.thumbnailPath)
        memberPhotoRepository.save(newPhoto)
    }
}
```

핵심 원칙:
- Service는 **흐름 제어(조합)만** 담당: "이것 처리하고 → 저것 저장하고 → 결과 반환"
- 비즈니스 로직(파일 처리, 변환, 계산, 검증)은 **도메인 컴포넌트(@Component)**에 캡슐화
- 도메인 컴포넌트는 포트를 의존하고, 결과를 Value Object로 반환
- Service 메서드를 읽었을 때 "무엇을 하는지"가 한눈에 보여야 한다 ("어떻게 하는지"는 도메인 컴포넌트 내부)

## 13. 설정 파일은 해당 인프라 모듈에 배치

각 인프라 모듈의 설정(application-{profile}.yml)은 **해당 모듈의 `src/main/resources/config/`** 에 둔다. boot 모듈에 모든 설정을 몰아넣지 않는다.

```
# BAD - boot 모듈에 모든 인프라 설정을 몰아넣음
boot/ma-boot-web/src/main/resources/application.yml
  → datasource, redis, jwt, file upload 설정이 모두 여기에

# GOOD - 각 인프라 모듈이 자신의 설정을 관리
infrastructure/storage/ma-db-core/src/main/resources/config/application-local.yml      → datasource 설정
infrastructure/storage/ma-redis-core/src/main/resources/config/application-local.yml   → redis 설정
infrastructure/support/ma-jwt-core/src/main/resources/config/application-local.yml     → jwt 설정
infrastructure/support/ma-file-storage/src/main/resources/config/application-local.yml → file upload 설정
```

규칙:
- 프로필별(`local`, `test`, `prod`) 설정은 각 모듈의 `config/application-{profile}.yml`에 배치
- boot 모듈의 `application.yml`에는 `spring.profiles.active`와 Spring 공통 설정(multipart 등)만 둔다
- 테스트 설정도 해당 모듈에 둔다 (예: `ma-file-storage`의 테스트 경로는 `ma-file-storage`에)

## 14. 로깅은 반드시 AppLogger 사용

`org.slf4j.LoggerFactory`를 직접 사용하지 않는다. `config/ma-config-logging`의 `AppLogger`(`com.konkuk.ma.logger`)를 사용한다.

```kotlin
// BAD - SLF4J 직접 사용
import org.slf4j.LoggerFactory
private val log = LoggerFactory.getLogger(javaClass)
log.warn("실패 (email={}): {}", email, e.message)

// GOOD - AppLogger 사용
import com.konkuk.ma.logger
logger.warn { "실패 (email=$email): ${e.message}" }
```

- `logger`는 top-level val로 선언된 싱글톤이므로 별도 선언 없이 import만 하면 됨
- 람다 기반 API (`logger.info { }`, `logger.warn { }`, `logger.error { }`)로 메시지를 지연 평가

## 15. 메서드 네이밍 — 파라미터로 유추 가능한 조건은 생략

메서드명에 파라미터 타입/이름으로 유추 가능한 조건을 반복하지 않는다. 같은 시그니처의 메서드가 추가되어 구분이 필요할 때만 `findByXxx` 형태를 사용한다.

단건 조회는 `findOne`, 복수 조회는 `find`로 구분하여 호출부에서 반환 타입을 바로 유추할 수 있게 한다.

```kotlin
// BAD - 파라미터로 유추 가능한 조건을 메서드명에 반복
fun findByRegisterEmail(email: String): MatchingResults
fun findByEmails(emails: Set<String>): Members

// GOOD - 파라미터만으로 충분, 단건/복수 구분
fun findOne(email: String): MemberPhoto?    // 단건
fun find(email: String): MatchingResults     // 복수(일급 컬렉션)
fun find(emails: Set<String>): Members       // 복수

// GOOD - 같은 타입 파라미터로 다른 조건 조회가 필요할 때만 ByXxx 추가
fun findOne(email: String): Member
fun findOneByNickname(nickname: String): Member
```

## 16. RESTful URL 설계 규칙

### 리소스 중심 URL
URL은 **명사(리소스)**를 나타내고, 행위는 **HTTP 메서드**로 표현한다. URL에 동사를 넣지 않는다.

```
# BAD - URL에 동사
POST   /api/members/getMember
POST   /api/members/createMember
POST   /api/members/deleteMember

# GOOD - 리소스 + HTTP 메서드
GET    /api/members/{memberId}
POST   /api/members
DELETE /api/members/{memberId}
```

### 복수형 사용
리소스명은 항상 복수형을 사용한다.

```
# BAD
/api/member
/api/matching-result

# GOOD
/api/members
/api/matching-results
```

### 계층 관계는 URL 경로로 표현
리소스 간 포함 관계(소유 관계)는 `/` 경로로 표현한다. 단, 2단계까지만 중첩한다.

```
# GOOD - 회원의 사진
POST   /api/members/photos
DELETE /api/members/photos

# GOOD - 매칭 결과의 상태 변경
PATCH  /api/matching-results/{matchingResultId}/exclude
PATCH  /api/matching-results/{matchingResultId}/include

# BAD - 3단계 이상 중첩
GET    /api/members/{memberId}/target-infos/{targetInfoId}/matching-results
```

### HTTP 메서드 사용 규칙

| 메서드 | 용도 | 응답 코드 |
|--------|------|-----------|
| GET | 리소스 조회 | 200 OK |
| POST | 리소스 생성 | 201 Created |
| PUT | 리소스 전체 교체 | 200 OK |
| PATCH | 리소스 부분 수정 | 200 OK |
| DELETE | 리소스 삭제 | 200 OK / 204 No Content |

### kebab-case 사용
URL 경로에는 kebab-case를 사용한다. camelCase, snake_case를 쓰지 않는다.

```
# BAD
/api/targetInfos
/api/target_infos
/api/matchingResults

# GOOD
/api/target-infos
/api/matching-results
```

### 필터링/정렬/페이징은 쿼리 파라미터로
리소스 목록의 필터링, 정렬, 페이징 조건은 URL 경로가 아닌 쿼리 파라미터로 전달한다.

```
# BAD - 필터 조건이 URL 경로에
GET /api/matching-results/excluded

# GOOD - 쿼리 파라미터로 필터링
GET /api/matching-results?excluded=true
GET /api/matching-results?page=0&size=20
GET /api/matching-results?sort=matchRate,desc
```

## 17. Api(Controller)는 Service만 의존한다

Api 클래스는 **Service 클래스만** 의존한다. Repository(포트)나 다른 인프라 컴포넌트에 직접 접근하지 않는다. 비즈니스 로직, 데이터 변환, 조회 조합 등은 모두 Service에서 처리하고, Api는 요청을 받아 Service에 위임하고 응답을 반환하는 역할만 한다.

```kotlin
// BAD - Api가 Repository에 직접 접근하고 비즈니스 로직을 포함
@RestController
class CommunityPostQueryApi(
    private val postQueryService: PostQueryService,
    private val memberQueryRepository: MemberQueryRepository,  // Repository 직접 의존
) {
    @GetMapping
    fun findPosts(...): CursorResponse<List<PostResponse>> {
        val cursorResult = postQueryService.find(category, cursorCondition)
        val posts = cursorResult.data
        // 닉네임 조회 로직이 Api에 존재
        val nicknameByEmail = memberQueryRepository.findByEmails(posts.map { it.authorEmail }.toSet())
            .associate { it.email to it.nickname }
        return CursorResponse(
            data = posts.map { PostResponse.from(it, nicknameByEmail[it.authorEmail] ?: "알 수 없음") },
            ...
        )
    }
}

// GOOD - Api는 Service만 의존, 로직은 Service에서 처리
@RestController
class CommunityPostQueryApi(
    private val postQueryService: PostQueryService,
) {
    @GetMapping
    fun findPosts(...): CursorResponse<List<PostResponse>> {
        return postQueryService.find(category, cursorCondition)
    }
}
```

규칙:
- Api 클래스의 생성자에는 **Service만** 주입받는다
- Api 메서드는 요청 파싱 → Service 호출 → 응답 반환만 담당
- 데이터 변환, 조합, 조회 로직은 Service 또는 도메인 객체에서 처리

## 18. 성능 고려사항

- **N+1 쿼리 방지**: 반복문 안에서 DB 조회하지 않는다. 벌크 조회 후 메모리에서 처리한다
- **불필요한 조건 제거**: enum 값이 2개뿐인 경우(예: Gender) DB 조건으로 넣지 말고 메모리에서 필터링
- **NoOffset 페이징**: 대용량 데이터 조회 시 cursor 기반 페이징 사용

## 19. 객체 관계 설계 — is-a와 has-a 구분

기존 도메인 객체의 필드를 그대로 복사하여 새 클래스를 만들지 않는다. 객체 간 관계를 먼저 판단하고, 적절한 관계를 사용한다.

### is-a 관계 (상속)
상위 개념과 하위 개념이 **"~은 ~이다"** 관계일 때 사용한다.

```kotlin
// 동물-사자: 사자는 동물이다 → is-a
abstract class Animal(val name: String)
class Lion(name: String, val maneColor: String) : Animal(name)
```

### has-a 관계 (조합)
한 객체가 다른 객체를 **"~을 가지고 있다"** 관계일 때 사용한다. 대부분의 경우 has-a가 적합하다.

```kotlin
// BAD - Post의 필드를 전부 복사
class PostSummary(
    val id: Long,
    val nickname: String,
    val category: PostCategory,
    val title: String,        // Post에서 복사
    val content: String,      // Post에서 복사
    val likes: Int,           // Post에서 복사
    val comments: Int,        // Post에서 복사
    val createdDate: LocalDateTime,  // Post에서 복사
)

// GOOD - Post를 참조로 가짐
class PostWithAuthor(
    val post: Post,           // has-a
    val nickname: String,
)
```

### 판단 기준
- **is-a**: 하위 타입이 상위 타입으로 대체 가능한가? (리스코프 치환 원칙)
- **has-a**: 두 객체가 독립적으로 존재 가능한가? 하나가 다른 하나를 "소유"하는가?
- **필드 복사가 보이면 has-a를 의심**: 기존 객체의 필드를 3개 이상 그대로 옮기고 있다면, 해당 객체를 참조로 가져야 한다
