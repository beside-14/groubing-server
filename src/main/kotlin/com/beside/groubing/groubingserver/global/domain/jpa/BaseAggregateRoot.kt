package com.beside.groubing.groubingserver.global.domain.jpa

import jakarta.persistence.MappedSuperclass
import org.springframework.data.domain.AfterDomainEventPublication
import org.springframework.data.domain.DomainEvents
import org.springframework.util.Assert

@MappedSuperclass
open class BaseAggregateRoot<A : BaseAggregateRoot<A>>: BaseEntity() {

    @Transient
    private val domainEvents = mutableListOf<Any>()

    protected fun <T : Any> registerEvent(event: T): T {
        Assert.notNull(event, "Domain event must not be null")
        domainEvents.add(event)
        return event
    }

    @AfterDomainEventPublication
    protected fun clearDomainEvents() {
        domainEvents.clear()
    }

    @DomainEvents
    protected fun domainEvents(): List<Any> {
        return domainEvents.toList()
    }

    protected fun andEventsFrom(aggregate: A): A {
        Assert.notNull(aggregate, "Aggregate must not be null")
        domainEvents.addAll(aggregate.domainEvents())
        return this as A
    }

    protected fun andEvent(event: Any): A {
        registerEvent(event)
        return this as A
    }
}
