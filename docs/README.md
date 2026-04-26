# docs

프로젝트 문서 모음.

| 문서 | 대상 / 용도 |
|---|---|
| [ONBOARDING.md](./ONBOARDING.md) | **새로 합류한 개발자가 가장 먼저 읽을 글.** 모듈 구조 / 빌드 / 한 use case 흐름 / 컨벤션 / 트러블슈팅까지 한 문서에 |
| [../CLAUDE.md](../CLAUDE.md) | Claude Code AI 용 프로젝트 가이드 (사람도 읽을 수 있음, 같은 내용 압축본) |
| [../.claude/skills/](../.claude/skills/) | 코드 작성 시 따라야 할 19개 규칙 + Clean Code + Kotlin 패턴 + 테스트 / REST Docs 컨벤션 |
| `../boot/gb-boot-web/src/docs/asciidoc/` | REST Docs 소스. `./gradlew asciidoctor` 후 `build/docs/asciidoc/*.html` 또는 실행 중인 서버의 `/docs/api.html` 에서 확인 |
