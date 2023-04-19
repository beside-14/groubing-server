package com.beside.groubing.groubingserver.docs

import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.snippet.IgnorableDescriptor

open class CustomDescriptor<T : IgnorableDescriptor<T>>(
    val descriptor: IgnorableDescriptor<T>
) {
    val isIgnored: Boolean = descriptor.isIgnored

    protected open var default: String
        get() = descriptor.attributes.getOrDefault(RestDocsUtils.DEFAULT_KEY, "") as String
        set(value) {
            descriptor.attributes(RestDocsUtils.defaultValue(value))
        }

    protected open var format: String
        get() = descriptor.attributes.getOrDefault(RestDocsUtils.FORMAT_KEY, "") as String
        set(value) {
            descriptor.attributes(RestDocsUtils.customFormat(value))
        }

    protected open var example: String
        get() = descriptor.attributes.getOrDefault(RestDocsUtils.EXAMPLE_KEY, "") as String
        set(value) {
            descriptor.attributes(RestDocsUtils.customSample(value))
        }

    protected open var description: String
        get() = descriptor.description as String
        set(value) {
            descriptor.description(value)
        }

    open infix fun means(value: String): CustomDescriptor<T> {
        this.description = value
        return this
    }

    open infix fun attributes(block: CustomDescriptor<T>.() -> Unit): CustomDescriptor<T> {
        block()
        return this
    }

    open infix fun withDefaultValue(value: String): CustomDescriptor<T> {
        this.default = value
        return this
    }

    open infix fun formattedAs(value: String): CustomDescriptor<T> {
        this.format = value
        return this
    }

    open infix fun example(value: String): CustomDescriptor<T> {
        this.example = value
        return this
    }

    open infix fun isOptional(value: Boolean): CustomDescriptor<T> {
        when {
            value && descriptor is FieldDescriptor -> descriptor.optional()
            value && descriptor is ParameterDescriptor -> descriptor.optional()
        }
        return this
    }

    open infix fun isIgnored(value: Boolean): CustomDescriptor<T> {
        if (value) descriptor.ignored()
        return this
    }
}

/**
 * DocsFieldType.ENUM 타입을 제외한 타입의 요청 필드 생성 시 사용
 */
infix fun String.requestType(docsFieldType: DocsFieldType): CustomDescriptor<FieldDescriptor> {
    val field = createField(this, docsFieldType.type, true)
    when (docsFieldType) {
        is DATE -> field formattedAs RestDocsUtils.DATE_FORMAT
        is DATETIME -> field formattedAs RestDocsUtils.DATETIME_FORMAT
        else -> {}
    }
    return field
}

/**
 * DocsFieldType.ENUM 타입의 요청 필드 생성 시 사용
 */
infix fun <E : Enum<E>> String.requestType(enumFieldType: ENUM<E>): CustomDescriptor<FieldDescriptor> {
    val field = createField(this, JsonFieldType.STRING, false)
    field.formattedAs(enumFieldType.toString())
    return field
}

/**
 * DocsFieldType.ENUM 타입을 제외한 타입의 응답 필드 생성 시 사용
 * ("data." prefix 추가)
 */
infix fun String.responseType(docsFieldType: DocsFieldType): CustomDescriptor<FieldDescriptor> {
    val field = createField(this.prefix(), docsFieldType.type, true)
    when (docsFieldType) {
        is DATE -> field formattedAs RestDocsUtils.DATE_FORMAT
        is DATETIME -> field formattedAs RestDocsUtils.DATETIME_FORMAT
        else -> {}
    }
    return field
}

/**
 * DocsFieldType.ENUM 타입의 응답 필드 생성 시 사용
 * ("data." prefix 추가)
 */
infix fun <E : Enum<E>> String.responseType(enumFieldType: ENUM<E>): CustomDescriptor<FieldDescriptor> {
    val field = createField(this.prefix(), JsonFieldType.STRING, false)
    field.formattedAs(enumFieldType.toString())
    return field
}

private fun String.prefix(): String {
    val prefix = "data."
    return if (!this.contains(prefix) && this != "code" && this != "message") prefix + this else this
}

private fun createField(
    value: String,
    type: JsonFieldType,
    optional: Boolean
): CustomDescriptor<FieldDescriptor> {
    val descriptor = fieldWithPath(value)
        .type(type)
        .attributes(RestDocsUtils.emptyExample(), RestDocsUtils.emptyFormat(), RestDocsUtils.emptyDefaultValue())
        .description("")
    if (optional) descriptor.optional()

    return CustomDescriptor(descriptor)
}

infix fun String.optional(optional: Boolean): CustomDescriptor<ParameterDescriptor> = createParameter(this, optional)

private fun createParameter(value: String, optional: Boolean): CustomDescriptor<ParameterDescriptor> {
    val descriptor = parameterWithName(value).description("")
    if (optional) descriptor.optional()

    return CustomDescriptor(descriptor)
}
