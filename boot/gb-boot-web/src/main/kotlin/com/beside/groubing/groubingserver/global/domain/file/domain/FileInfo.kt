package com.beside.groubing.groubingserver.global.domain.file.domain

import com.beside.groubing.groubingserver.global.domain.jpa.BaseCreatedTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.nio.file.Path
import java.nio.file.Paths

@Entity
@Table(name = "FILE_INFOS")
class FileInfo private constructor(
    private val directory: String,
    private val fileName: String,
    private val originalName: String
) : BaseCreatedTimeEntity() {
    @Id
    @Column(name = "FILE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    val url: String
        get() = "/api/files/$fileName"

    fun getAbsolutePath(): Path {
        val root = System.getProperty("user.home")
        return Paths.get("$root/$directory", fileName)
    }

    companion object {
        fun create(directory: String, fileName: String, originalName: String): FileInfo {
            return FileInfo(directory, fileName, originalName)
        }
    }
}
