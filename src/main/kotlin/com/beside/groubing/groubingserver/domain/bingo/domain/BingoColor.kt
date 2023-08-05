package com.beside.groubing.groubingserver.domain.bingo.domain

enum class BingoColor(val value: String) {
    BLUE("#8BC0FC"),
    ORANGE("#FCB179"),
    RED("FF8282"),
    GREEN("#55DEB5"),
    PURPLE("#B8B7FC");

    companion object {
        fun makeRandomBingoColor(): BingoColor {
            val randomIndex = (BingoColor.values().indices).random()
            return BingoColor.values()[randomIndex]
        }
    }
}
