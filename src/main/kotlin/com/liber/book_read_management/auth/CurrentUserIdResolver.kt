package com.liber.book_read_management.auth

import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class CurrentUserIdResolver : HandlerMethodArgumentResolver {

    // CurrentUserId 어노테이션을 파라미터로 갖고 있는지
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(CurrentUserId::class.java) &&
                (parameter.parameterType == java.lang.Long::class.java ||
                parameter.parameterType == Long::class.javaPrimitiveType)
    }

    // request에서 userId를 반환
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val attrs = RequestContextHolder.currentRequestAttributes()
        println("userId = " + attrs.getAttribute("userId", RequestAttributes.SCOPE_REQUEST))
        return attrs.getAttribute("userId", RequestAttributes.SCOPE_REQUEST) as? Long
    }
}