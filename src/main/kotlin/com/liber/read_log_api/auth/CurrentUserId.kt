package com.liber.read_log_api.auth

import io.swagger.v3.oas.annotations.Hidden

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Hidden
annotation class CurrentUserId
