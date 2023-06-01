package com.beside.groubing.groubingserver.global.domain.file.application

import com.beside.groubing.groubingserver.global.domain.file.domain.FileInfoRepository
import com.beside.groubing.groubingserver.global.domain.file.exception.FileInfoInputException
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class FileInfoService(
    private val fileInfoRepository: FileInfoRepository
) {
    fun findByFileName(fileName: String): Resource {
        val fileInfo = fileInfoRepository.findFirstByFileName(fileName) ?: throw FileInfoInputException()
        return FileProvider.find(fileInfo)
    }
}
