package com.yarnlog.yarnlog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class YarnlogApplication

fun main(args: Array<String>) {
	runApplication<YarnlogApplication>(*args)
}
