package com.app.replace.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Place(_spotName: String, _roadAddress: String) {
    @Column
    val spotName: String
    @Column
    val roadAddress: String

    init {
        spotName = _spotName
        roadAddress = _roadAddress
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Place

        if (spotName != other.spotName) return false
        return roadAddress == other.roadAddress
    }

    override fun hashCode(): Int {
        var result = spotName.hashCode()
        result = 31 * result + roadAddress.hashCode()
        return result
    }
}
