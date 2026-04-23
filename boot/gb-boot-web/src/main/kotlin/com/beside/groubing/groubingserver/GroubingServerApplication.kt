package com.beside.groubing.groubingserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GroubingServerApplication

fun main(args: Array<String>) {
    runApplication<GroubingServerApplication>(*args)
}
