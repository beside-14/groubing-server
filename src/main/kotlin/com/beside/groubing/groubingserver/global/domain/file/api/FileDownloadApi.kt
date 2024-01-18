package com.beside.groubing.groubingserver.global.domain.file.api

import com.beside.groubing.groubingserver.global.domain.file.application.FileInfoService
import com.beside.groubing.groubingserver.global.domain.file.application.FileProvider
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
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
        val resource = fileInfoService.findByFileName(fileName)
        return ResponseEntity.ok()
            .contentType(FileProvider.getContentType(fileName))
            .body(resource)
    }

    @GetMapping("/bingo-item-image/{fileName:.+}")
    fun downloadBingoItemImage(
        @PathVariable fileName: String
    ): ResponseEntity<Resource> {
        return ResponseEntity.ok()
            .contentType(FileProvider.getContentType(fileName))
            .body(UrlResource("/groubing/bingoitem/$fileName"))
    }
}
