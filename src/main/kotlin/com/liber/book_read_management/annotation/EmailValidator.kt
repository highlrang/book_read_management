package com.liber.book_read_management.annotation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class EmailValidator : ConstraintValidator<Email, String> {

    val EMAIL_PATTERN = Regex.fromLiteral("^[a-zA-Z0-9_!#\$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$")
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return !(value == null || !value.matches(EMAIL_PATTERN))
    }

}