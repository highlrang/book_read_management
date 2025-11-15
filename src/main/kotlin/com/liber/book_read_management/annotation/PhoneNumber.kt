package com.liber.book_read_management.annotation

import jakarta.validation.Constraint

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PhoneNumberValidator::class])
annotation class PhoneNumber