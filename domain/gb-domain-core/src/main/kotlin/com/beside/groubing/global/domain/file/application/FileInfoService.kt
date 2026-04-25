package com.beside.groubing.global.domain.file.application

import com.beside.groubing.global.domain.file.domain.FileInfo
import com.beside.groubing.global.domain.file.domain.port.FileInfoQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FileInfoService(
    private val fileInfoQueryRepository: FileInfoQueryRepository
) {
    fun findByFileName(fileName: String): FileInfo {
        return fileInfoQueryRepository.findByFileName(fileName)
    }
}
