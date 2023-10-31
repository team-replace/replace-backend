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
}
