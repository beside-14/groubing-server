package com.beside.groubing.groubingserver.global.domain.file.application

import com.beside.groubing.groubingserver.global.domain.file.domain.FileInfo
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.MediaType
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartException
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID


class FileProvider {
    companion object {
        private val root = System.getProperty("user.home")
        private val IMAGE_FORMAT = listOf("png", "jpeg", "jpg")

        fun upload(file: MultipartFile): FileInfo {
            validateFormat(file)
            val fileName = createFileName(file)
            val directory = getDirectory()
            val path = Paths.get("$root/$directory")
            val newFile = File(path.toUri().path, fileName)
            if (!newFile.exists()) Files.createDirectories(path)
            file.transferTo(newFile)
            return FileInfo.create(directory, fileName, file.originalFilename!!)
        }

        fun delete(fileInfo: FileInfo) {
            Files.deleteIfExists(fileInfo.getAbsolutePath())
        }

        fun find(fileInfo: FileInfo): Resource {
            val path = fileInfo.getAbsolutePath()
            return UrlResource(path.toUri())
        }

        fun getContentType(fileName: String): MediaType {
            val fileFormat = StringUtils.getFilenameExtension(fileName)
            if (IMAGE_FORMAT[0].equals(fileFormat, ignoreCase = true)) return MediaType.IMAGE_PNG
            return MediaType.IMAGE_JPEG
        }

        private fun validateFormat(file: MultipartFile) {
            if (file.isEmpty) throw MultipartException("파일이 정상적으로 업로드되지 않았습니다")
            val fileFormat = getFileFormat(file)
            val isIncludeFormat = IMAGE_FORMAT.any { format -> format.equals(fileFormat, ignoreCase = true) }
            if (!isIncludeFormat) throw MultipartException(".png / .jpg / .jpeg 형식의 파일만 업로드 가능합니다.")
        }

        private fun createFileName(file: MultipartFile): String {
            val fileFormat = getFileFormat(file)
            return "${System.currentTimeMillis()}-${UUID.randomUUID()}.$fileFormat"
        }

        private fun getFileFormat(file: MultipartFile): String? {
            val originalFilename = file.originalFilename
            return StringUtils.getFilenameExtension(originalFilename)
        }

        private fun getDirectory(): String {
            return "/groubing/${LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))}"
        }
    }
}
