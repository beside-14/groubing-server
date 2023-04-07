package com.beside.groubing.groubingserver.domain.bingo.domain

enum class BingoSize(
    val x: Int,
    val y: Int,
    val description: String
) {
    NINE(3, 3, "3x3"),
    SIXTEEN(4, 4, "4x4")
}
