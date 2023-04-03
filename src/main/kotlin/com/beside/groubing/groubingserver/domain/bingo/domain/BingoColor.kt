package com.beside.groubing.groubingserver.domain.bingo.domain

enum class BingoColor(
    val hex: String, val
    description: String
) {
    RED("#ff0000", "빨강색"),
    ORANGE("#f67828", "주황색"),
    YELLOW("#ffff00", "노랑색"),
    GREEN("#008000", "초록색"),
    BLUE("#0000ff", "파랑색"),
    NAVY("#000080", "남색"),
    PURPLE("#800080", "보라색"),
    RANDOM("", "랜덤 색상")
}
