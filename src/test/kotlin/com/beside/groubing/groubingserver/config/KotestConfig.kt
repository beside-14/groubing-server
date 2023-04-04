package com.beside.groubing.groubingserver.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.extensions.spring.SpringAutowireConstructorExtension
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode

class KotestConfig : AbstractProjectConfig() {
    override fun extensions() = listOf(
        SpringTestExtension(SpringTestLifecycleMode.Root),
        SpringAutowireConstructorExtension
    )

    override val isolationMode = IsolationMode.InstancePerLeaf
}
