package com.chuqigong.springbootemptyproject

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Controller
@RestController
class HelloController {
    @GetMapping("/sayHello")
    fun sayHello(@RequestParam(name = "name", required = false) name: String?): String {
        return if (name.isNullOrEmpty()) "Hello,Everybody!" else "Hello,%s!".format(name)
    }
}