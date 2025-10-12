package com.liber.read_log_api.annotation

import jakarta.validation.Constraint


@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [LoginIdValidator::class])
annotation class LoginId
