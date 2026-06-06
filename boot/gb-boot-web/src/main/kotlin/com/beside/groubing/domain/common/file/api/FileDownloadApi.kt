package com.beside.groubing.domain.common.file.api

import com.beside.groubing.domain.common.file.application.FileInfoService
import com.beside.groubing.domain.common.file.application.FileProvider
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/files")
class FileDownloadApi(
    private val fileInfoService: FileInfoService
) {
    @GetMapping("/{fileName:.+}")
    fun download(
        @PathVariable fileName: String
    ): ResponseEntity<Resource> {
        val fileInfo = fileInfoService.findOne(fileName)
        val resource = FileProvider.find(fileInfo)
        return ResponseEntity.ok()
            .contentType(FileProvider.getContentType(fileName))
            .body(resource)
    }

    @GetMapping("/bingo-item-image/{fileName:.+}")
    fun downloadBingoItemImage(
        @PathVariable fileName: String
    ): ResponseEntity<Resource> {
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(ClassPathResource("$BINGO_ITEM_IMAGE_PATH$fileName"))
    }

    companion object {
        private const val BINGO_ITEM_IMAGE_PATH = "static/bingo-item-image/"
    }
}
