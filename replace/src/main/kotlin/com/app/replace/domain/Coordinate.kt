package com.app.replace.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.math.BigDecimal

@Embeddable
class Coordinate(longitude: BigDecimal, latitude: BigDecimal) {
    @Column(precision = 25, scale = 20)
    val longitude: BigDecimal

    @Column(precision = 25, scale = 20)
    val latitude: BigDecimal

    init {
        this.longitude = longitude
        this.latitude = latitude
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Coordinate

        if (longitude != other.longitude) return false
        return latitude == other.latitude
    }

    override fun hashCode(): Int {
        var result = longitude.hashCode()
        result = 31 * result + latitude.hashCode()
        return result
    }
}
