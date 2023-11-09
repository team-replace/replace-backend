package com.app.replace.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import java.io.Serializable

@Entity
@IdClass(ConnectionId::class)
class Connection(
    @Id @Column val hostId: Long,
    @Id @Column val partnerId: Long
) : TemporalRecord()

@Embeddable
data class ConnectionId(var hostId: Long?, var partnerId: Long?) : Serializable
