package com.example.hexagonal

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExampleHexagonalApplication

fun main(args: Array<String>) {
    runApplication<ExampleHexagonalApplication>(*args)
}
