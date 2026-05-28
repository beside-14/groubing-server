package com.beside.groubing.global.id

import com.beside.groubing.global.domain.id.port.IdObfuscator
import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.ConditionalGenericConverter
import org.springframework.core.convert.converter.GenericConverter.ConvertiblePair

/**
 * `@PathVariable` / `@RequestParam` 의 문자열을 [DecryptId] 어노테이션이 붙은 [Long] 으로 복호화한다.
 *
 * [ConditionalGenericConverter.matches] 에서 target 파라미터에 [DecryptId] 어노테이션이 있는지 확인해
 * 다른 일반 String→Long 변환과 충돌하지 않게 분리한다.
 *
 * [com.beside.groubing.global.config.WebConfig.addFormatters] 에서 등록된다.
 */
class DecryptIdConverter(
    private val idObfuscator: IdObfuscator
) : ConditionalGenericConverter {

    override fun matches(sourceType: TypeDescriptor, targetType: TypeDescriptor): Boolean {
        return targetType.hasAnnotation(DecryptId::class.java)
    }

    override fun getConvertibleTypes(): Set<ConvertiblePair> {
        return setOf(
            ConvertiblePair(String::class.java, Long::class.java),
            ConvertiblePair(String::class.java, Long::class.javaObjectType)
        )
    }

    override fun convert(source: Any?, sourceType: TypeDescriptor, targetType: TypeDescriptor): Any? {
        if (source == null) return null
        val annotation = targetType.getAnnotation(DecryptId::class.java)
            ?: throw IllegalStateException("@DecryptId 어노테이션을 찾을 수 없습니다.")
        return idObfuscator.decode(annotation.value, source as String)
    }
}
