package com.liber.book_read_management.annotation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PhoneNumberValidator: ConstraintValidator<PhoneNumber, String> {

    val PHONENUMBER_PATTERN = Regex.fromLiteral("^010-\\d{4}-\\d{4}\$")

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return !(value == null || !value.matches(PHONENUMBER_PATTERN))
    }
}