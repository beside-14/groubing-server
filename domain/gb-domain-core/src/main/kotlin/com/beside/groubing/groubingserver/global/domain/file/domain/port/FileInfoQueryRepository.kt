package com.beside.groubing.groubingserver.global.domain.file.domain.port

import com.beside.groubing.groubingserver.global.domain.file.domain.FileInfo

interface FileInfoQueryRepository {
    fun findByFileName(fileName: String): FileInfo
}
