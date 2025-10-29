package com.liber.book_read_management.annotation

import jakarta.validation.Constraint


@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [LoginIdValidator::class])
annotation class LoginId
