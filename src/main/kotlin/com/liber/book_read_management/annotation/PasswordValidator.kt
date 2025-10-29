package com.liber.book_read_management.annotation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PasswordValidator: ConstraintValidator<Password, String> {

    val PASSWORD_PATTERN = Regex.fromLiteral("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{6,}\$")

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return !(value == null || !value.matches(PASSWORD_PATTERN))
    }
}