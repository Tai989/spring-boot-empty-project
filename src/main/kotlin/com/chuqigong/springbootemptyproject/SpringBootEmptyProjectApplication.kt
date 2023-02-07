package com.chuqigong.springbootemptyproject

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource

@SpringBootApplication
class SpringBootEmptyProjectApplication

fun main(args: Array<String>) {
    runApplication<SpringBootEmptyProjectApplication>(*args)
}
