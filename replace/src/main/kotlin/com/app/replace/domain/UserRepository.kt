package com.app.replace.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, Long> {
    @Query("select u.id from User u where u.nickname = :nickname")
    fun findIdByNickname(nickname: String): Long?

    @Query("select u.connectionCode from User u where u.id = :userId")
    fun findConnectionCodeById(userId: Long): String?

    @Query("select u.id from User u where u.connectionCode = :connectionCode")
    fun findIdByConnectionCode(connectionCode: String): Long?
}
