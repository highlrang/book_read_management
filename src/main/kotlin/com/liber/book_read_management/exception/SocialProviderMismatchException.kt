package com.liber.book_read_management.exception

import com.liber.book_read_management.enums.SocialProvider

class SocialProviderMismatchException(
    val registeredProvider: SocialProvider,
    override val message: String
) : RuntimeException(message)
