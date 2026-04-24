package com.beside.groubing.groubingserver.global.domain.file.domain

class FileInfo private constructor(
    val id: Long,
    val directory: String,
    val fileName: String,
    val originalName: String
) {
    val url: String
        get() = "/api/files/$fileName"

    companion object {
        fun create(directory: String, fileName: String, originalName: String): FileInfo {
            return FileInfo(id = 0L, directory = directory, fileName = fileName, originalName = originalName)
        }

        fun of(id: Long, directory: String, fileName: String, originalName: String): FileInfo {
            return FileInfo(id = id, directory = directory, fileName = fileName, originalName = originalName)
        }
    }
}
