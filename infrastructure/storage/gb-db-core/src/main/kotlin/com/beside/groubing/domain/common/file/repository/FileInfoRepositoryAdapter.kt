package com.beside.groubing.domain.common.file.repository

import com.beside.groubing.domain.common.file.domain.FileInfo
import com.beside.groubing.domain.common.file.domain.port.FileInfoQueryRepository
import com.beside.groubing.domain.common.file.exception.FileInfoInputException
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
