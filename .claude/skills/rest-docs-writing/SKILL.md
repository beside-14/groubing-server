---
name: rest-docs-writing
description: "Spring REST Docs API 문서화 테스트 코드 작성 가이드. 이 프로젝트의 REST Docs 테스트 컨벤션, Vocabulary 패턴(필드 정의 함수 재활용), AsciiDoc snippet 생성, main.adoc 연결까지 전체 워크플로를 포함한다. rest-docs-generator 에이전트가 REST Docs 테스트를 작성할 때 항상 참조해야 하는 스킬이다. API 문서화, REST Docs, andDocument, Vocabulary 필드 정의와 관련된 작업 시 반드시 사용한다."
---

# REST Docs Writing Guide

이 프로젝트의 Spring REST Docs 테스트 코드 작성 컨벤션과 워크플로를 정의한다.

## 전체 워크플로

사용자가 API URL을 전달하면 다음 순서로 작업한다:

1. **Controller 분석** — 해당 URL의 Controller 클래스를 찾아 HTTP method, path, request/response DTO, path variables, query parameters를 파악
2. **Vocabulary 정의** — API 필드를 Vocabulary 파일에 재사용 가능한 함수로 정의
3. **테스트 코드 작성** — KoTest FunSpec 기반 REST Docs 테스트 작성
4. **AsciiDoc snippet 생성** — `src/docs/asciidoc/{도메인이름}/` 하위에 `.adoc` 파일 생성
5. **main.adoc 연결** — 생성한 snippet을 main.adoc에 링크 추가
6. **테스트 실행** — `./gradlew test --tests "TestClassName" -p boot/ma-boot-web`으로 검증

## 프로젝트 구조

```
boot/ma-boot-web/
├── src/docs/asciidoc/           # AsciiDoc 문서
│   ├── main.adoc                # 문서 인덱스
│   └── {domain}/                # 도메인별 API 문서
│       └── {api-name}.adoc
├── src/test/kotlin/com/konkuk/ma/
│   ├── config/BaseApiTest.kt    # API 테스트 베이스 어노테이션
│   ├── extension/               # REST Docs 확장 유틸
│   │   ├── RestDocsExtensions.kt
│   │   ├── CustomDescriptor.kt
│   │   ├── DocsFieldType.kt
│   │   ├── MockMvcExtensions.kt
│   │   └── RestDocsUtils.kt
│   ├── vocabulary/              # 필드 정의 Vocabulary (신규 도입)
│   │   └── {Domain}Vocabulary.kt
│   └── domain/{domain}/api/     # API 테스트 클래스
│       └── {Name}ApiTest.kt
```

## 1. Vocabulary 패턴

### 목적

API 필드 정의를 Vocabulary 파일에 함수로 분리하여 **여러 테스트에서 재사용**한다. 동일한 필드(email, nickname 등)를 매번 inline으로 정의하지 않고, Vocabulary 함수를 호출한다.

### 위치

`boot/ma-boot-web/src/test/kotlin/com/konkuk/ma/vocabulary/{Domain}Vocabulary.kt`

### 작성 규칙

```kotlin
package com.konkuk.ma.vocabulary

import com.konkuk.ma.extension.*

// --- 공통 필드 ---
fun email(fieldName: String = "email") =
    fieldName responseType STRING means "이메일" example "user@example.com"

fun nickname(fieldName: String = "nickname") =
    fieldName responseType STRING means "닉네임" example "테스트닉네임"

fun duplicated(fieldName: String = "duplicated") =
    fieldName responseType BOOLEAN means "중복 여부" example "false"
```

**함수 시그니처 규칙:**
- 함수명은 필드명과 동일하게 (camelCase)
- 첫 번째 파라미터는 항상 `fieldName: String = "기본필드명"` — 다른 이름으로 재사용 가능
- 필요한 경우 `description: String` 파라미터 추가 가능
- 반환 타입은 `CustomDescriptor<FieldDescriptor>`  (명시하지 않아도 됨)

**필드 정의 DSL:**
- 응답 필드: `fieldName responseType TYPE means "설명" example "예시값"`
- 요청 필드: `fieldName responseType TYPE means "설명"` (requestBody에서 사용 시)
- Enum 필드: `fieldName responseType STRING means "설명" formattedAs ENUM(EnumClass::class).toString()`
- 선택 필드: 끝에 `isOptional true` 추가
- Path Variable / Query Parameter: 별도 함수로 정의

```kotlin
// Path Variable용
fun targetInfoIdPath(fieldName: String = "targetInfoId") =
    fieldName requestParam "찾는 사람 정보 ID"

// Query Parameter용
fun pageParam(fieldName: String = "page") =
    fieldName requestParam "페이지 번호"
```

### Vocabulary 파일 분리 기준

- **도메인 단위**로 Vocabulary 파일을 분리한다
  - `MemberVocabulary.kt` — 회원 관련 필드
  - `AuthVocabulary.kt` — 인증 관련 필드
  - `MatchingVocabulary.kt` — 매칭 관련 필드
- 여러 도메인에서 공통으로 쓰이는 필드는 `CommonVocabulary.kt`에 정의

## 2. 테스트 코드 작성 규칙

### 테스트 클래스 구조

```kotlin
package com.konkuk.ma.domain.{domain}.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.konkuk.ma.config.BaseApiTest
import com.konkuk.ma.extension.*
import com.konkuk.ma.vocabulary.{Domain}Vocabulary.* // Vocabulary import
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest({Controller}::class)
@BaseApiTest
class {Controller}ApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val {service}: {ServiceClass}
) : FunSpec({

    test("{API 설명} API 문서화") {
        // Given - mock 설정
        every { service.method(any()) } returns result

        // When & Then
        mockMvc.postJson("/api/{path}") {
            content = mapper.writeValueAsString(request)
        }
            .andExpect { status { isOk() } }
            .andDocument(
                "{domain}/{api-identifier}",
                requestBody(
                    email(),       // Vocabulary 함수 사용
                    password(),
                ),
                responseBody(
                    email(),
                    nickname(),
                    accessToken(),
                )
            )
    }
})
```

### 핵심 컨벤션

- **Spec 스타일**: `FunSpec` 사용 (`test("설명") { ... }`)
- **Mock 프레임워크**: Mockk (Mockito 사용 금지)
- **Base 어노테이션**: `@WebMvcTest` + `@BaseApiTest`
- **MockMvc 확장**: `postJson()`, `getJson()` 사용 (MockMvcExtensions.kt)
- **andDocument identifier**: `"{도메인}/{api-이름}"` 형식 (kebab-case)
- **필드 정의**: Vocabulary 함수 호출로 재사용 (inline 정의 최소화)
- **한국어 설명**: 필드 description은 한국어로 작성

### 필수 테스트 케이스 범위

REST Docs 테스트도 **성공 케이스와 실패 케이스를 모두** 작성한다.

| 케이스 | 설명 | 예시 |
|--------|------|------|
| 성공 문서화 | 정상 요청 → 2xx 응답, REST Docs snippet 생성 | `status { isOk() }` + `andDocument(...)` |
| 빈 목록 | 데이터 없는 경우 빈 응답 | 목록 조회 API의 empty 케이스 |
| 유효성 검증 실패 | 필수 필드 누락, 길이 초과 등 → 400 | `@Size(max=40)` 초과 입력 |
| 인증 실패 | 미인증 요청 → 401 | 해당되는 경우만 |

```kotlin
test("게시글 작성 API 문서화") { ... }               // 성공 + REST Docs
test("제목이 40자를 초과하면 400을 반환한다") { ... }   // 유효성 실패
test("내용이 비어있으면 400을 반환한다") { ... }        // 유효성 실패
```

### REST Docs snippet 함수 사용법

```kotlin
// Request Body
requestBody(
    email(),
    password(),
)

// Response Body
responseBody(
    targetInfoId(),
    registerEmail(),
)

// Path Variables
pathVariables(
    targetInfoIdPath(),
)

// Query Parameters
requestParam(
    pageParam(),
    sizeParam(),
)

// Response Body with Pagination
responseBodyWithPage(
    email(),
    nickname(),
)
```

### 인증이 필요한 API

```kotlin
@WithAuthMember(email = "test@example.com")  // 클래스 레벨 또는 메서드 레벨
```

## 3. AsciiDoc 문서 작성 규칙

### snippet 파일 생성 위치

`boot/ma-boot-web/src/docs/asciidoc/{도메인이름}/{api-이름}.adoc`

### snippet 파일 구조

```asciidoc
:doctype: book
:icons: font
:source-highlighter: highlightjs
:toc: left
:toclevels: 2
:sectlinks:

= {API 이름} API

== {API 동작 설명}

{API에 대한 간단한 설명}

=== 요청

include::{snippets}/{domain}/{api-identifier}/http-request.adoc[]

=== 요청 필드

include::{snippets}/{domain}/{api-identifier}/request-fields.adoc[]

=== 응답

include::{snippets}/{domain}/{api-identifier}/http-response.adoc[]

=== 응답 필드

include::{snippets}/{domain}/{api-identifier}/response-fields.adoc[]
```

**include 경로 규칙:**
- `{snippets}` 변수는 Spring REST Docs가 자동 설정
- andDocument의 identifier와 동일한 경로 사용
- Path Variable이 있으면 `path-parameters.adoc` 추가
- Query Parameter가 있으면 `query-parameters.adoc` 추가

### main.adoc 연결 규칙

기존 main.adoc의 구조를 따라 해당 도메인 섹션에 링크를 추가한다:

```asciidoc
[[{domain}-{api-name}]]
=== {API 한글 이름}

* link:{domain}/{api-name}.html[{API 한글 이름} API,window=_blank]
```

- 이미 해당 도메인 섹션이 있으면 그 아래에 추가
- 도메인 섹션이 없으면 새로 생성

## 4. 테스트 검증

테스트 작성 후 반드시 실행하여 통과를 확인한다:

```bash
./gradlew test --tests "{TestClassName}" -p boot/ma-boot-web
```

실패 시:
- Missing bean → `@MockkBean` 누락 확인
- Security 관련 → `@BaseApiTest`, `@WithAuthMember` 확인
- Field 관련 → request/response DTO 필드와 Vocabulary 함수의 fieldName 일치 확인
