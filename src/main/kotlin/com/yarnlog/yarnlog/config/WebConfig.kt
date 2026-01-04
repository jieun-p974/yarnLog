package com.yarnlog.yarnlog.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Paths

@Configuration
class WebConfig (
    @Value("\${file.upload-dir}") private val uploadDir: String
): WebMvcConfigurer{
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val absolutePath = Paths.get(uploadDir).toAbsolutePath().normalize().toString()

        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:$absolutePath/")
    }
}