package com.yarnlog.yarnlog.dto

data class PagedResponse<T> (
    val items: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)