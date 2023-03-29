package com.beside.groubing.groubingserver.global.domain.jpa

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseCreatedTimeEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdDate = LocalDateTime.MIN
        private set

    @CreatedBy
    @Column(nullable = false, updatable = false)
    var createdBy = ""
        private set
}