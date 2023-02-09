package com.chuqigong.restapi.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class HomeController {
    @GetMapping("/")
    fun home(): String {
        return "Hello World!"
    }

    @GetMapping("/sayHello")
    fun sayHello(@RequestParam("username", required = false) username: String): String {
        return if (username.isNullOrBlank()) "Hello,Anonymous" else "Hello,%s".format(username)
    }
}