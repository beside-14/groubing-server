package com.beside.groubing.global.domain.jpa

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdDate: LocalDateTime = LocalDateTime.MIN
        private set

    @CreatedBy
    @Column(nullable = false, updatable = false)
    var createdBy = ""
        private set

    @LastModifiedDate
    var lastModifiedDate: LocalDateTime = LocalDateTime.MIN
        private set

    @LastModifiedBy
    var lastModifiedBy = ""
        private set

    @Column(nullable = false)
    var deleted: Boolean = false
        private set

    fun markDeleted() {
        this.deleted = true
    }
}
