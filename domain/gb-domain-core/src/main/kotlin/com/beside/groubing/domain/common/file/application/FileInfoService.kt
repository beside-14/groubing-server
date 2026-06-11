package com.beside.groubing.domain.common.file.application

import com.beside.groubing.domain.common.file.domain.FileInfo
import com.beside.groubing.domain.common.file.domain.port.FileInfoQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FileInfoService(
    private val fileInfoQueryRepository: FileInfoQueryRepository
) {
    fun findOne(fileName: String): FileInfo {
        return fileInfoQueryRepository.findOne(fileName)
    }
}
