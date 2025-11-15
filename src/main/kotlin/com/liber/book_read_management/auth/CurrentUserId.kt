package com.liber.book_read_management.auth

import io.swagger.v3.oas.annotations.Hidden

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Hidden
annotation class CurrentUserId
