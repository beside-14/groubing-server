package com.beside.groubing.config

import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.boot.env.YamlPropertySourceLoader
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

/**
 * 모듈별로 흩어진 YAML 설정을 모아 하나의 PropertySource 로 병합하는 [EnvironmentPostProcessor].
 *
 * 각 모듈(gb-db-core, gb-id-obfuscator, ...)은 자신이 쓰는 설정을
 * `src/main/resources/config/application[-{profile}].yml` 에 두고 소유한다.
 * 표준 Spring 로딩은 `classpath:`(단일 매칭)이라 같은 경로의 파일이 여러 jar 에 있으면 하나만 집어가지만,
 * 본 프로세서는 `classpath*:`(전체 jar 스캔)로 읽어 **모든 모듈의 설정을 병합**한다.
 *
 * 스캔/병합 순서 (뒤가 앞을 덮어씀):
 * 1. `classpath*:application.{yml,yaml}`         — 루트 공통(프로파일 무관)
 * 2. `classpath*:config/application.{yml,yaml}`  — 모듈 공통(프로파일 무관)
 * 3. `classpath*:config/application-*.{yml,yaml}` — `spring.config.activate.on-profile` 가 활성 프로파일과 일치하는 것만(공통을 오버라이드)
 *
 * 활성 프로파일이 없으면 아무 것도 병합하지 않는다 — 프로파일 미지정 기동(datasource 부재)을 그대로 실패시키기 위함.
 */
class GbEnvironmentPostProcessor : EnvironmentPostProcessor {

    companion object {
        private const val CONFIG_DIR = "classpath*:config/"
        private const val PROFILE_FILE_GLOB = "application-*"
        private const val MERGED_PROPERTY_SOURCE_NAME = "gb-merged-config"
        private const val PROFILE_KEY = "spring.config.activate.on-profile"
        private val YAML_EXTENSIONS = listOf(".yml", ".yaml")
    }

    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        val activeProfiles = environment.activeProfiles.toSet()
        if (activeProfiles.isEmpty()) {
            return
        }

        try {
            val merged = loadAndMerge(activeProfiles)
            if (merged.isNotEmpty()) {
                environment.propertySources.addFirst(MapPropertySource(MERGED_PROPERTY_SOURCE_NAME, merged))
            }
        } catch (e: Exception) {
            throw IllegalStateException("Failed to load groubing configuration files", e)
        }
    }

    private fun loadAndMerge(activeProfiles: Set<String>): Map<String, Any> {
        val resolver = PathMatchingResourcePatternResolver()
        val merged = mutableMapOf<String, Any>()

        forEachResource(resolver, "classpath*:application") { mergeCommon(it, merged) }
        forEachResource(resolver, CONFIG_DIR + "application") { mergeCommon(it, merged) }
        forEachResource(resolver, CONFIG_DIR + PROFILE_FILE_GLOB) { mergeActiveProfile(it, activeProfiles, merged) }

        return merged
    }

    private fun forEachResource(
        resolver: PathMatchingResourcePatternResolver,
        locationWithoutExtension: String,
        action: (Resource) -> Unit
    ) {
        YAML_EXTENSIONS.forEach { extension ->
            resolver.getResources(locationWithoutExtension + extension).forEach(action)
        }
    }

    /** 프로파일 무관 공통 설정 — `on-profile` 키만 빼고 모두 병합. */
    private fun mergeCommon(resource: Resource, merged: MutableMap<String, Any>) {
        flatten(resource).forEach { (key, value) ->
            if (key != PROFILE_KEY) {
                merged[key] = value
            }
        }
    }

    /** 파일의 `on-profile` 이 활성 프로파일과 일치할 때만 병합(공통 오버라이드). */
    private fun mergeActiveProfile(resource: Resource, activeProfiles: Set<String>, merged: MutableMap<String, Any>) {
        val properties = flatten(resource)
        val profile = properties[PROFILE_KEY]?.toString()
        if (profile != null && profile in activeProfiles) {
            merged.putAll(properties)
        }
    }

    /**
     * YAML 을 점(.) 표기 평탄화 맵으로 로드한다.
     *
     * 값은 반드시 [toString] 으로 풀어 넣는다 — [YamlPropertySourceLoader] 가 내놓는 `OriginTrackedValue` 를
     * 그대로 [MapPropertySource] 에 담으면 바인더가 `OriginTrackedValue → Boolean/Int` 변환기를 못 찾아 실패한다.
     * 빈(null) 값은 제외해 의도치 않은 "null" 문자열 주입도 막는다.
     */
    private fun flatten(resource: Resource): Map<String, Any> {
        val flattened = mutableMapOf<String, Any>()
        YamlPropertySourceLoader().load(resource.filename, resource).forEach { source ->
            (source.source as Map<*, *>).forEach { (key, value) ->
                if (key != null && value != null) {
                    flattened[key.toString()] = value.toString()
                }
            }
        }
        return flattened
    }
}
