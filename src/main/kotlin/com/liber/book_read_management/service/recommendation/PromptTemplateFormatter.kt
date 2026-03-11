package com.liber.book_read_management.service.recommendation

import org.springframework.stereotype.Component

@Component
class PromptTemplateFormatter {

    fun format(template: String, values: Map<String, String>): String {
        var formatted = template
        values.forEach { (key, value) ->
            formatted = formatted.replace("{{$key}}", value)
        }
        return formatted
    }
}
