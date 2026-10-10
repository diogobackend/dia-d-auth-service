package com.diadsimulation.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DiadAuthServiceApplication

fun main(args: Array<String>) {
    runApplication<DiadAuthServiceApplication>(*args)
}
