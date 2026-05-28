package com.beside.groubing.global.domain.id

/**
 * 응답·요청에서 노출되는 id 의 obfuscation 도메인 구분자.
 *
 * 같은 숫자 id 라도 type 이 다르면 다른 문자열로 인코딩되도록 `saltSuffix` 로 도메인을 격리한다
 * (예: memberId=1 과 bingoBoardId=1 의 인코딩 결과가 서로 다름).
 *
 * 새로운 type 을 추가할 때는 기존 값을 재정렬하지 않는다 — saltSuffix 가 인코딩 시드의 일부이므로
 * 값을 바꾸면 이미 발급된 문자열이 모두 무효화된다.
 */
enum class ObfuscationType(val saltSuffix: String) {
    MEMBER("member"),
    BINGO_BOARD("bingo-board"),
    BINGO_ITEM("bingo-item"),
    BINGO_MEMBER("bingo-member"),
    FRIEND("friend"),
    BLOCKED_MEMBER("blocked-member"),
    NOTIFICATION("notification"),
    FILE_INFO("file-info"),
}
