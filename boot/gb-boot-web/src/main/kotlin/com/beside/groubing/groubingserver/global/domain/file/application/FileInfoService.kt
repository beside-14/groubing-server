package com.beside.groubing.groubingserver.global.domain.file.application

import com.beside.groubing.groubingserver.global.domain.file.domain.port.FileInfoQueryRepository
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FileInfoService(
    private val fileInfoQueryRepository: FileInfoQueryRepository
) {
    fun findByFileName(fileName: String): Resource {
        val fileInfo = fileInfoQueryRepository.findByFileName(fileName)
        return FileProvider.find(fileInfo)
    }
}
