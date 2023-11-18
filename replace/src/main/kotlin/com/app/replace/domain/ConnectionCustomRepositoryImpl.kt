package com.app.replace.domain

import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
class ConnectionCustomRepositoryImpl(
    private val entityManager: EntityManager
) : ConnectionCustomRepository {
    override fun existsConnectionHavingUserId(userId: Long): Boolean {
        val count = entityManager.createQuery(
            "select count(c) from Connection c where c.hostId = :userId or c.partnerId = :userId", Long::class.java
        )
            .setParameter("userId", userId)
            .singleResult
        return count != 0L
    }

    override fun deleteByUserId(userId: Long) {
        entityManager.createQuery("update Connection c set c.deleted = true where c.hostId = :userId or c.partnerId = : userId")
            .setParameter("userId", userId)
            .executeUpdate()
    }

    override fun existsDeletedConnectionHavingHostIdAndPartnerId(hostId: Long, partnerId: Long): Boolean {
        val count = entityManager.createNativeQuery(
            "select count(*) from connection where deleted = true and (host_id = :userId and partner_id = :partnerId)",
            Long::class.java
        )
            .setParameter("userId", hostId.coerceAtMost(partnerId))
            .setParameter("partnerId", hostId.coerceAtLeast(partnerId))
            .singleResult

        return count != 0L
    }

    override fun findHavingId(userId: Long): Connection? {
        return entityManager.createQuery(
            "select c from Connection c where c.hostId = :userId or c.partnerId = :userId",
            Connection::class.java
        )
            .setParameter("userId", userId)
            .resultStream
            .findFirst()
            .getOrNull()
    }
}
