package com.beside.groubing.domain.common.id.exception

class InvalidObfuscatedIdException(encoded: String) :
    RuntimeException("유효하지 않은 식별자입니다. : $encoded")
