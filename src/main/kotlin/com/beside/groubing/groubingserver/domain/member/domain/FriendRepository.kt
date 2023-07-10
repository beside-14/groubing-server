package com.beside.groubing.groubingserver.domain.member.domain

import org.springframework.data.jpa.repository.JpaRepository

interface FriendRepository : JpaRepository<Friend, Long>
