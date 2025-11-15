package com.yarnlog.yarnlog.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthContoller {
    @GetMapping("/api/health")
    fun health(): String{
        return "OK"
    }
}