package com.chuqigong.restapi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
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
    @GetMapping("/json")
    @ResponseBody
    fun json():String{
        return ObjectMapper().writeValueAsString(mapOf(Pair("username","konchoo"), Pair("age",28)))
    }
}