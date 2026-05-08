---
name: plan-writing
description: "requirement-planner 에이전트가 구현 계획을 세울 때 따라야 하는 표준 가이드. 코드 스니펫 없이 파일 목록, 메서드 시그니처, 변환 규칙 수준으로 작성한다. 상세 코드가 필요하면 /detail-plan을 사용한다."
---

# 구현 계획 작성 규칙 (Plan Writing Rules)

코드 스니펫 없이 **핵심 설계만** 담는 구현 계획을 작성한다.

---

## 1. 문서 구조

```markdown
# Plan: {기능명}

> 작성일: YYYY-MM-DD

## 1. 개요
한두 문장으로 무엇을 왜 구현하는지 요약

## 2. 변경 전략
레이어별 변환 규칙을 테이블로 정리 (코드 스니펫 없이)

## 3. 변경 파일 목록
Phase별로 파일 경로 + 변경 내용 한 줄 요약

## 4. 고려사항
기술적 제약, 성능, 안전성

## 5. 검증 항목
테스트/빌드 확인 사항
```

---

## 2. 변경 전략 (코드 스니펫 금지)

코드 블록 대신 **테이블**로 규칙을 정리한다:

```markdown
| 레이어 | 현재 | 변경 후 | 변환 위치 |
|--------|------|---------|-----------|
| Domain Model | `email: String` | `email: Email` | 필드 타입 변경 |
| Port | `fun find(email: String)` | `fun find(email: Email)` | 파라미터 타입 변경 |
| Repository | String 전달 | `email.value` 언패킹 | Repository 구현체 |
| Entity | String 유지 | `toDomain()`에서 Email() | Entity 변환 메서드 |
```

새 클래스/메서드가 필요한 경우 **시그니처만** 기술한다:

```markdown
### 신규 클래스
- `Email(val value: String)` — data class, init에서 정규식 검증
- 위치: `domain/.../common/domain/Email.kt`
```

---

## 3. 변경 파일 목록

Phase별로 **파일 경로 + 한 줄 요약**만 작성한다:

```markdown
### Phase 1: 신규 생성
| # | 파일 | 내용 |
|---|------|------|
| 1 | `domain/.../common/domain/Email.kt` | Email Value Object 신규 |

### Phase 2: Domain Model
| # | 파일 | 내용 |
|---|------|------|
| 2 | `domain/.../member/domain/Member.kt` | `email: String` → `email: Email` |
| 3 | `domain/.../member/domain/NewMember.kt` | `email: String` → `email: Email` |
```

**코드 스니펫, import 목록, 전체 클래스 코드를 포함하지 않는다.**
변경 내용은 "어떤 필드/메서드를 어떻게 바꾸는지" 한 줄로 충분하다.

---

## 4. 고려사항

판단 근거와 대안을 간결하게:

```markdown
- **이름 충돌**: `jakarta.validation.constraints.Email`과 도메인 `Email` → import alias 사용
- **DB 스키마**: 변경 없음 (컬럼은 VARCHAR 유지)
- **성능**: Email 객체 생성 시 정규식 검증 추가, 기존 Bean Validation과 중복이나 가드 역할
```

---

## 5. 프로젝트 컨텍스트

### 기존 패턴 참조 (필수)
변경 대상과 유사한 기존 구현체를 반드시 확인하고, 일관성을 유지한다.

### DDL 변경 시
`infrastructure/storage/ma-db-core/src/main/resources/script/ddl.sql`에 추가. FK 사용 금지.

### 기존 코드 읽기 (필수)
구현 계획 작성 전에 변경 대상 파일의 현재 코드를 반드시 읽는다.

---

## 6. 파일 저장 규칙

**저장 경로**: `docs/plan/{YYYYMM}/{feature-name}.plan.md`

```
docs/plan/
  └── 202604/
      └── email-value-object.plan.md
```

---

## 7. brief / plan / detail-plan 구분

| 커맨드 | 용도 | 분량 | 코드 스니펫 |
|--------|------|------|------------|
| `/brief` | 빠른 확인용 | ~50줄 | 없음 |
| `/plan` | **표준 구현 계획** | ~200줄 | 없음 (시그니처만) |
| `/detail-plan` | 복잡한 신규 기능 | ~1000줄+ | 전체 코드 포함 |