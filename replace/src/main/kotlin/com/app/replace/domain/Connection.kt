package com.app.replace.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import org.springframework.data.domain.Persistable
import java.io.Serializable

@Entity
@SQLDelete(sql = "update connection SET deleted = true")
@Where(clause = "deleted is false")
@IdClass(ConnectionId::class)
class Connection(
    @Id @Column val hostId: Long,
    @Id @Column val partnerId: Long
) : TemporalRecord(), Persistable<ConnectionId> {
    private var deleted: Boolean = false

    override fun getId(): ConnectionId? {
        return ConnectionId(hostId, partnerId)
    }

    override fun isNew(): Boolean {
        return true
    }
}

@Embeddable
data class ConnectionId(var hostId: Long?, var partnerId: Long?) : Serializable
