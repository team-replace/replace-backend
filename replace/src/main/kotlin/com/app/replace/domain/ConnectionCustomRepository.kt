package com.app.replace.domain

interface ConnectionCustomRepository {
    fun existsConnectionHavingUserId(userId: Long): Boolean

    fun deleteByUserId(userId: Long)

    fun existsDeletedConnectionHavingHostIdAndPartnerId(hostId: Long, partnerId: Long): Boolean
}
