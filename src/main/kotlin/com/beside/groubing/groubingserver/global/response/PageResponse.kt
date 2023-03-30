package com.beside.groubing.groubingserver.global.response

import org.springframework.data.domain.Page

class PageResponse<T>(page: Page<T>) {
    val contents: List<T>? = page.content

    val totalElements: Long = page.totalElements

    val numberOfElements = page.numberOfElements

    val currentPage = page.number + 1

    val totalPages = page.totalPages

    val size = page.size

    val first = page.isFirst

    val last = page.isLast
}
