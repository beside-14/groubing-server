package com.beside.groubing.global.domain.file.repository

import com.beside.groubing.global.domain.file.domain.FileInfo
import com.beside.groubing.global.domain.file.domain.port.FileInfoQueryRepository
import com.beside.groubing.global.domain.file.exception.FileInfoInputException
import org.springframework.stereotype.Repository

@Repository
class FileInfoRepositoryAdapter(
    private val fileInfoJpaRepository: FileInfoJpaRepository
) : FileInfoQueryRepository {
    override fun findOne(fileName: String): FileInfo {
        return fileInfoJpaRepository.findByFileName(fileName)?.toDomain()
            ?: throw FileInfoInputException()
    }
}
