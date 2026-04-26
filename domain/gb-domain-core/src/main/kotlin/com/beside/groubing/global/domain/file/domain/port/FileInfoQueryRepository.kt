package com.beside.groubing.global.domain.file.domain.port

import com.beside.groubing.global.domain.file.domain.FileInfo

interface FileInfoQueryRepository {
    fun findOne(fileName: String): FileInfo
}
