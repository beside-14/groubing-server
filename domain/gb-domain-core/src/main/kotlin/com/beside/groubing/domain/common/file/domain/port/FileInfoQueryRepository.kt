package com.beside.groubing.domain.common.file.domain.port

import com.beside.groubing.domain.common.file.domain.FileInfo

interface FileInfoQueryRepository {
    fun findOne(fileName: String): FileInfo
}
