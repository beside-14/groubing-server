이번 변경분 코드가 `code-implementation-rules`, `clean-code` 두 스킬을 준수하는지 검증하고 위반을 수정해줘:

$ARGUMENTS

## 1. 검증 대상 결정

- 인자가 파일 경로면 해당 파일들을 검증
- 비어있으면 `git diff --name-only develop -- '*.kt'` 결과를 자동 탐지

## 2. 검증 (단일 에이전트 1회 호출)

code-rules-reviewer 에이전트를 **한 번만** 호출한다. 프롬프트에 다음을 명시:

- `.claude/skills/code-implementation-rules/SKILL.md`와 `.claude/skills/clean-code/SKILL.md`를 **모두** Read
- 두 스킬 기준 위반 사항을 **한 번에** 보고
- 각 위반 형식: `파일:라인 / 규칙명(스킬명: 규칙 번호) / 위반 내용 / 수정 방향`
- 수정은 하지 말고 보고만
- 칭찬/통과 사유 없이 위반만 보고

두 reviewer 에이전트로 분할 금지. SKILL 로드·git diff·파일 Read가 중복되어 토큰 낭비.

## 3. 수정 방침 (우선순위와 분류)

### 3-1. "함수 크기/단일 책임" 위반 처리 원칙 (중요)

Service/도메인 함수가 너무 길거나 한 가지 일만 하지 않는다는 위반이 나오면, **private 함수 추출을 가장 먼저 떠올리지 말 것**. private 메서드 추출은 위반을 해결하는 게 아니라 **숨기는 것**일 때가 많다.

다음 우선순위로 검토:

1. **도메인 재분배** (최우선)
   - "조립·계산·판단"을 하고 있다면 도메인 객체나 일급 컬렉션, Finder/Provider 같은 협력자로 옮긴다
   - 예: `findProductWithDiscount()`를 Service private로 두지 말고 `PointProductWithDiscountFinder.findOne(id)`로 이관
   - 예: `router.resolve(method).approve(request)` 2단 호출을 `router.approve(method, request)` 단일 메서드로 통합 (디미터)
   - 예: Value Object·일급 컬렉션에 행위를 부여해 Service 코드 흡수

2. **한 줄 래퍼 삭제**
   - private 함수 본문이 다른 객체 메서드 호출 한 줄이면 그 private은 **가짜 추상화**. 삭제하고 호출부에 inline
   - 예: `private fun recordTransaction(...) = pointTransactionRepository.save(...)` → 삭제

3. **그래도 남으면 private 추출**
   - Service orchestration 본질에 해당하고 (Repository + 도메인 조합) 도메인으로 옮길 수 없을 때만 private 허용
   - 이 경우에도 "private 함수 2개 이상 생길 것 같다" 싶으면 1번으로 돌아가 재검토

**금지: 함수 크기 위반을 단순 private 추출로 해결하고 종료**. 규칙 위반을 숨기는 것과 같다.

### 3-2. 처리 주체 분류

**기계적 변환 → 메인에서 Edit 직접 수행**
- 메서드 리네임, import 교체, 단일 예외 타입 변경
- DAO/Entity 팩토리 메서드 추가 같은 정형 패턴
- 파라미터 객체화로 인자 수 축소
- 한 줄 래퍼 private 삭제

기계적 변환을 에이전트에 위임 금지. 스킬 재로드 비용이 수정 비용보다 크다.

**복잡한 리팩토링 → code-implementer 1회 호출**
- 3-1의 "도메인 재분배" 수준 변경 (신규 클래스 도입, 기존 책임 이관)
- 도메인 타입 전역 변경 (예: 원시값 → VO 포장이 다수 파일에 파급)
- 여러 파일의 상호 의존 리팩토링
- 판단이 필요한 설계 변경

기계적 건과 복잡 건이 섞여있으면: 기계적 건을 먼저 메인에서 처리 → 남은 복잡 건만 모아 code-implementer 1회 호출.

## 4. 검증

수정 완료 후 `./gradlew test` 그린 확인. 실패 시 해당 수정부터 재점검.

## 결과 보고

```
## 검증 결과 종합

| 출처 | 위반 | 처리 방식 | 상태 |
|------|------|-----------|------|
| code-rules #N / clean-code #N | 요약 | 메인 직접 / code-implementer | 수정완료 / 미해결 |

1차 위반 N건 → 최종 N건 (메인 직접 N건, 에이전트 N건)
```

위반이 0건이면 "위반 사항 없음"으로 보고.

## 주의사항

- 검증은 반드시 **단일 에이전트 호출**. 두 reviewer로 분할 금지
- 기계적 변환은 **반드시 메인에서 Edit**. 에이전트 위임 금지
- 재검증은 기본적으로 하지 않음. 수정 범위가 크거나 구조를 바꾼 경우에만 1회 재호출
- 에이전트에 전달할 검증 대상 파일 목록을 프롬프트에 명확히 포함