package com.beside.groubing.domain.common.id

/**
 * `saltSuffix` 는 인코딩 시드의 일부다 — 한 번 정한 값을 바꾸면 이미 발급된 모든 문자열이 무효화된다.
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
