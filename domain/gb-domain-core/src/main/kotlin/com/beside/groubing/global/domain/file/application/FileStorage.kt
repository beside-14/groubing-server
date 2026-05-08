package com.beside.groubing.global.domain.file.application

import com.beside.groubing.global.domain.file.domain.FileInfo
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FileStorage {
    companion object {
        private val root: String = System.getProperty("user.home")

        fun delete(fileInfo: FileInfo) {
            Files.deleteIfExists(absolutePath(fileInfo))
        }

        fun absolutePath(fileInfo: FileInfo): Path {
            return Paths.get("$root/${fileInfo.directory}", fileInfo.fileName)
        }
    }
}
