package com.liber.book_read_management.config

import com.liber.book_read_management.service.SensitiveDataEncryptor
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class SensitiveStringEncryptConverter : AttributeConverter<String?, String?> {
    override fun convertToDatabaseColumn(attribute: String?): String? {
        return SensitiveDataEncryptor.encrypt(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): String? {
        return SensitiveDataEncryptor.decrypt(dbData)
    }
}
