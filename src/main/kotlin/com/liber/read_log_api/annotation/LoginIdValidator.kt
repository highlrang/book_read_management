package com.liber.read_log_api.annotation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class LoginIdValidator : ConstraintValidator<LoginId, String> {

    val LOGIN_PATTERN = Regex.fromLiteral("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}\$")
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return !(value == null || !value.matches(LOGIN_PATTERN))
    }

}