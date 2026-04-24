지정한 plan 문서를 기반으로 브랜치 생성 → 구현+테스트 → REST Docs → 검증 → PR 생성까지 자동 수행해줘.

plan 문서 경로: $ARGUMENTS

## 1단계: plan 문서 읽기

1. 지정된 plan 문서를 Read로 읽어 구현 내용을 파악
2. plan 문서에서 기능명(feature name)을 추출

## 2단계: 브랜치 생성

1. 현재 브랜치에서 새 브랜치를 생성
2. 브랜치명 규칙: `feat/{feature-name}` (plan 문서의 기능명을 kebab-case로, `backend/` 접두사 없음)
3. `git checkout -b {브랜치명}`

## 3단계: 구현 + 테스트 (단일 에이전트 1회 호출)

code-implementer 에이전트를 **한 번** 호출한다. 프롬프트에 다음을 명시:

- plan 문서 전체 구현 (Phase 1~N, 구현 순서 준수)
- 구현 직후 **해당 PR 범위의 모든 테스트를 연속 작성**:
  - 도메인 단위 테스트 (VO, 도메인 객체, Validator)
  - Service 테스트 (Mockk)
  - 인프라 통합 테스트 (Repository/Dao)
  - Mock 어댑터 등 Boot 테스트
- REST Docs 테스트는 제외 (다음 단계에서 별도 처리)
- 필수 참조 스킬: `clean-code`, `code-implementation-rules`, `kotest-writing` 세 SKILL.md를 모두 Read
- 완료 기준: `./gradlew test` 전체 그린

구현과 테스트를 동일 에이전트에서 연속 수행해 스킬·컨벤션 재로드 비용을 제거한다. kotest-writer로 분리 호출 금지.

## 4단계: REST Docs (API가 있는 경우만, rest-docs-generator 1회 호출)

새로운 API 엔드포인트가 있으면 rest-docs-generator 에이전트를 호출한다. Vocabulary / AsciiDoc / `main.adoc` 연결 컨벤션이 특수해 분리 유지.

API가 없는 기능(배치, 도메인 로직만 변경 등)이면 이 단계를 건너뛴다.

## 5단계: 검증 및 수정

`/review` 커맨드를 실행한다. `/review` 내부 정책을 따른다:

- 검증은 단일 에이전트 1회
- 수정은 메인이 직접 Edit. 복잡 건만 code-implementer 1회 호출

## 6단계: PR 생성

`/pr` 커맨드로 커밋, 푸시, PR 생성. PR 본문에 plan 문서 경로 포함.

## 주의사항

- plan 문서가 존재하지 않으면 작업 중단하고 안내
- 구현+테스트는 반드시 **단일 에이전트**. kotest-writer로 분리 호출 금지
- REST Docs는 API가 있을 때만. 신규 Vocabulary 함수가 2개 이하로 단순하면 code-implementer에 포함 가능
- 리뷰 수정의 기계적 변환은 메인에서 Edit으로 직접 처리
- 전체 테스트 통과 확인 후 커밋
- 독립적인 단계는 병렬 실행 가능 (예: 리뷰의 검증 에이전트는 단일이지만, plan 내 독립 Phase 구현은 에이전트 내부에서 자유롭게 병렬화)