package com.example.kopring.controller

import com.example.kopring.service.HelloService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController(
    val helloService: HelloService
) {

    @GetMapping("/hello")
    fun hello(): HelloResponse {
        return HelloResponse(helloService.getHello())
    }

}