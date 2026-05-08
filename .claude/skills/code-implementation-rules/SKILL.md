---
name: code-implementation-rules
description: "Kotlin 코드 구현 시 따라야 하는 OOP 원칙과 패턴 가이드. 도메인 객체 행위 부여, 원시값 포장(Value Object), 일급 컬렉션, 디미터 법칙, 포트 인터페이스 규칙, 팩토리 메서드, 성능 고려사항을 포함한다. 코드를 작성하거나 수정할 때, 특히 도메인 모델, 서비스, 포트 인터페이스를 다룰 때 항상 참조해야 한다."
---

# 코드 구현 규칙 (Code Implementation Rules)

코드를 작성하거나 수정할 때 반드시 따라야 하는 OOP 원칙과 구현 패턴을 정의한다. **번호가 작을수록 우선순위가 높다.**

---

## 1. ⚠️ Service는 조합만 담당 — 비즈니스 로직 금지

Service 클래스는 비즈니스 로직을 직접 구현하지 않는다. 도메인 객체/컴포넌트를 **조합(orchestrate)**하는 역할만 한다.

- BAD: Service 안에서 파일 경로 생성, 썸네일 변환, 조건 분기 등 로직을 직접 수행
- GOOD: `processor.process()` → `NewPhoto.create()` → `repository.save()` 처럼 위임만

**판단 기준**: Service 메서드를 읽었을 때 "무엇을 하는지"만 보여야 한다. "어떻게 하는지"가 보이면 도메인 컴포넌트로 추출한다.

- 비즈니스 로직(파일 처리, 변환, 계산, 검증)은 **도메인 컴포넌트(@Component)**에 캡슐화
- Service는 포트와 도메인 컴포넌트만 의존, 다른 Service 참조 금지
- **Repository(포트 구현체)도 비즈니스 로직 금지** — DAO 호출 + `toDomain()` 변환만 담당. 조건 분기, 데이터 가공, 검증 로직을 넣지 않는다

### 1-1. ⚠️ 생성/수정 전 검증은 Validator로 분리

Service의 Command 메서드에서 **여러 저장소를 조회해 수행하는 사전 검증**(중복 체크, 참조 엔티티 존재/소유권 확인, 상태 검증 등)은 Service에 두지 않고 **`XxxValidator` 도메인 컴포넌트**로 분리한다.

- 위치: `domain.xxx.domain.XxxValidator` (`@Component`)
- 시그니처: `fun validate(newXxx: NewXxx)` — 생성/수정할 도메인 객체를 받아 검증만 수행
- 기존 예시: `SignUpValidator`, `CommentValidator`, `XroomValidator`

BAD — Service가 직접 조회하고 분기해서 던짐:
```
fun create(...) {
    val targetInfo = targetInfoQueryRepository.findOne(id)
    targetInfo.validateOwnership(Email(email))
    if (xroomQueryRepository.exists(id)) throw DuplicateException(...)
    return xroomCommandRepository.save(newXroom)
}
```

GOOD — Service는 조합만, 검증은 Validator에 위임:
```
fun create(...) {
    val newXroom = NewXroom(...)
    xroomValidator.validate(newXroom)
    return xroomCommandRepository.save(newXroom)
}
```

**판단 기준**: Command 메서드에 `if (...) throw ...` 또는 조회-후-분기가 2개 이상 있으면 Validator로 분리 대상.

## 2. 도메인 객체에 행위 부여

외부에서 getter로 꺼내서 판단하지 말고, 객체 스스로 판단/행동하게 한다.

- BAD: `if (target.name == targetInfo.targetName && target.gender == targetInfo.targetGender)`
- GOOD: `target.matchesNameAndGender(name, gender)`

## 3. 원시값 포장 (Value Object)

도메인에서 의미 있는 값은 Value Object로 감싼다. `init`에서 유효성 검증을 포함한다.

- 예: `Email(value)`, `FourDigit(value)`, `Year(value)`

## 4. 일급 컬렉션

**도메인 행위가 있는 경우에만** 컬렉션을 감싸는 도메인 객체를 만든다. 행위 없이 감싸기만 하면 안 된다.

- 멤버 변수명은 `val data`로 통일, `private` 금지
- DAO는 `List<Entity>` 반환 → Repository는 `List<DomainObject>` 반환 → Service에서 일급 컬렉션으로 감쌈
- `companion object` 팩토리 메서드로 생성 의도를 드러냄

## 5. 디미터 법칙

직접 협력하는 객체에게만 메시지를 보낸다. `a.b.c.doSomething()` 체이닝 금지.

- BAD: `member.phoneNumber.middleNumber`
- GOOD: `Target.create(member)` 후 target 사용

## 6. 포트(Port) 인터페이스 규칙

- 파라미터/반환 타입은 **도메인 객체** 사용
- 일급 컬렉션은 포트 반환 타입으로 사용하지 않음 → `List<DomainObject>` 반환
- 단건 조회는 **non-null 반환**, 없으면 Repository 구현체에서 예외. nullable이 필요하면 `OrNull` 접미사
- BAD: Service에서 `?: throw EntityNotFoundException(...)` — GOOD: Repository 구현체에서 예외

## 7. 비즈니스 로직은 도메인 객체 안에

Writer/Controller/Service 같은 외부 계층에 비즈니스 로직을 두지 않는다. 도메인 객체에게 위임한다.

## 8. 팩토리 메서드

복잡한 객체 생성은 `companion object`의 팩토리 메서드로 의도를 드러낸다. **반드시 자기 자신의 단일 인스턴스를 반환**. `List<T>` 반환은 팩토리가 아니라 일급 컬렉션의 책임이다.

## 9. DAO → Entity → Domain 변환

- QueryDao는 `List<XxxEntity>` 또는 `XxxEntity?` 반환
- Entity는 `toDomain()` 메서드와 `companion object`의 `from(row: ResultRow)` 팩토리 제공
- Repository 구현체에서 `entity.toDomain()` 호출

## 10. 하드코딩 지양

매직 넘버/스트링 금지. 상수로 추출하거나 파라미터로 주입받는다.

- **상수**: 거의 안 바뀌는 값 (전화번호 자릿수, 비밀번호 최소 길이)
- **파라미터**: 운영 중 변경 가능한 값 (만료일, 페이지 크기)
- **반환 타입**: `Pair`, `Triple`, `Map<Long, Int>` 금지 → 의도를 드러내는 도메인 객체 사용

## 11. Validation 상수 관리

Bean Validation의 `message`, `regexp`는 하드코딩 금지. `ValidationMessages`, `ValidationPatterns` 상수를 사용한다.

## 12. Api(Controller) 규칙

- Api는 **Service만** 의존. Repository, 인프라 컴포넌트 직접 접근 금지
- 요청 파싱 → Service 호출 → 응답 반환만 담당

## 13. 메서드 네이밍

- 파라미터로 유추 가능한 조건은 메서드명에 반복하지 않는다 (`find`, `findOne`, `exists`, `delete`, `count` 등 **모든 조회/존재/삭제/집계 메서드에 동일 적용**)
  - 나쁜 예: `existsByTargetInfoId(targetInfoId: Long)`, `deleteByMemberId(memberId: Long)`
  - 좋은 예: `exists(targetInfoId: Long)`, `delete(memberId: Long)`
- 단건 조회: `findOne`, 복수 조회: `find`로 구분
- 같은 타입 파라미터로 **다른 조건**이 필요할 때만 `findByXxx`, `findOneByXxx`, `existsByXxx`, `deleteByXxx` 등 접미사 허용 (오버로드 구분 목적)

## 14. RESTful URL 설계

- URL은 **명사(리소스)**, 행위는 **HTTP 메서드**로 표현. URL에 동사 금지
- 리소스명은 **복수형**, **kebab-case** 사용
- 계층 관계는 URL 경로로 표현하되 **2단계까지만** 중첩
- 필터링/정렬/페이징은 쿼리 파라미터로
- POST → 201 Created, GET → 200 OK, PATCH → 200 OK, DELETE → 200/204

## 15. SOLID 원칙

- **⚠️ OCP (개방-폐쇄 원칙)**: 확장에 열려 있고 수정에 닫혀 있어야 한다. 하드코딩된 값을 클래스에 박아놓지 말고 파라미터로 주입받도록 설계한다
- **SRP**: 클래스/함수는 하나의 책임만. 변경 이유가 하나
- **DIP**: 상위 모듈은 하위 모듈이 아닌 추상화(포트)에 의존

## 16. 설정 파일 배치

각 인프라 모듈의 설정은 해당 모듈의 `src/main/resources/config/`에 배치. boot 모듈에 몰아넣지 않는다.

## 17. 로깅은 AppLogger 사용

`org.slf4j.LoggerFactory` 직접 사용 금지. `com.konkuk.ma.logger`를 import하여 사용한다.

## 18. 성능 고려사항

- **N+1 방지**: 반복문 안에서 DB 조회 금지. 벌크 조회 후 메모리 처리
- **NoOffset 페이징**: 대용량 조회 시 cursor 기반
- **exists 쿼리**: `count() > 0` 사용 금지 (전체 행을 셈). `limit(1).any()` 사용
- **단건 조회**: `.map { }.singleOrNull()` 사용 금지 (전체 결과를 메모리에 올림). `.limit(1).firstOrNull()?.let { Entity.from(it) }` 사용

## 19. 객체 관계 — is-a와 has-a 구분

기존 객체 필드를 그대로 복사하지 않는다. 필드 3개 이상 복사하면 has-a(참조)를 사용한다.

- BAD: `PostSummary(id, title, content, likes, ...)` — Post 필드 전부 복사
- GOOD: `PostWithAuthor(val post: Post, val nickname: String)` — Post를 참조