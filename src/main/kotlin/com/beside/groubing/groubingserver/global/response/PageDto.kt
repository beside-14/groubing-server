package com.beside.groubing.groubingserver.global.response

import org.springframework.data.domain.Page

class PageDto<T>(val page: Page<T>) {
    private var contents: List<T>? = page.content

    private var totalElements: Long = page.totalElements

    private var numberOfElements = page.numberOfElements

    private var currentPage = page.number + 1

    private var totalPages = page.totalPages

    private var size = page.size

    private var first = page.isFirst

    private var last = page.isLast
}