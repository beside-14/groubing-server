package com.beside.groubing.domain.common.file.domain

class FileInfo private constructor(
    val id: Long,
    val directory: String,
    val fileName: String,
    val originalName: String
) {
    val url: String
        get() = urlOf(fileName)

    companion object {
        private const val URL_PREFIX = "/api/files/"

        fun urlOf(fileName: String): String = "$URL_PREFIX$fileName"

        fun urlOfOrNull(fileName: String?): String? = fileName?.let(::urlOf)

        fun create(directory: String, fileName: String, originalName: String): FileInfo {
            return FileInfo(id = 0L, directory = directory, fileName = fileName, originalName = originalName)
        }

        fun of(id: Long, directory: String, fileName: String, originalName: String): FileInfo {
            return FileInfo(id = id, directory = directory, fileName = fileName, originalName = originalName)
        }
    }
}
