package com.beside.groubing.groubingserver.global.domain.jpa

import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate

@MappedSuperclass
open class BaseEntity : BaseCreatedTimeEntity() {

    @LastModifiedDate
    var lastModifiedDate = LocalDateTime.MIN
        private set

    @LastModifiedBy
    var lastModifiedBy = ""
        private set
}
