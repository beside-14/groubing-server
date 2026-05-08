package com.beside.groubing

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration

/**
 * gb-db-core 모듈의 슬라이스 테스트(@DataJpaTest)에서 자동 검색되는 SpringBootConfiguration anchor.
 * root 패키지(`com.beside.groubing`)에 위치하므로 `@AutoConfigurationPackage` 가 base 를 자동으로 잡아
 * `@EntityScan` / `@EnableJpaRepositories` 명시가 불필요하다.
 *
 * src/test 에 두어 testFixtures 로 외부 모듈에 노출되지 않으므로,
 * boot-web 의 [GroubingServerApplication] 과 자동 검색 충돌이 발생하지 않는다.
 * boot-web 의 슬라이스 테스트는 자기 main 의 GroubingServerApplication 을 anchor 로 사용한다.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
class PersistenceTestApplication
