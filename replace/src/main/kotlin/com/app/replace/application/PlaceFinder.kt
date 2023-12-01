package com.app.replace.application

import com.app.replace.domain.Coordinate
import com.app.replace.domain.Place

interface PlaceFinder {
    fun findPlaceByCoordinate(coordinate: Coordinate): Place
}
