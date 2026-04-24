현재 브랜치의 변경사항을 커밋, 푸시하고 Pull Request를 생성해줘.

대상 브랜치: $ARGUMENTS

대상 브랜치가 비어있으면 기본값으로 develop 브랜치를 사용한다.

## 1단계: 커밋
1. git status로 변경사항 확인 (staged, unstaged, untracked 모두)
2. git diff로 변경 내용 파악
3. 변경사항이 있으면 적절한 커밋 메시지를 작성하여 커밋
   - .env, credentials 등 민감 파일은 제외
   - 커밋 메시지는 이 레포지토리의 기존 커밋 메시지 스타일을 따를 것
4. 변경사항이 없으면 기존 커밋으로 진행

## 2단계: 푸시
1. 리모트 브랜치가 있는지 확인
2. git push -u origin {현재브랜치} 로 푸시

## 3단계: PR 생성
1. 대상 브랜치와의 diff를 확인하여 PR에 포함될 전체 변경사항을 파악
2. PR 제목은 70자 이내로 간결하게 작성
3. PR 본문에 변경사항 요약을 작성
4. gh pr create로 PR 생성
5. 생성된 PR URL을 반환
