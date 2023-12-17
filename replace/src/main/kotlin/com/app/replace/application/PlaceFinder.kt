package com.app.replace.application

import com.app.replace.domain.Coordinate
import com.app.replace.domain.Place
import com.app.replace.domain.PlaceWithCoordinate
import org.springframework.data.domain.Pageable

interface PlaceFinder {
    fun findPlaceByCoordinate(coordinate: Coordinate): Place

    fun findPlaceByKeyword(keyword: String, coordinate: Coordinate, pageable: Pageable): List<PlaceWithCoordinate>

    fun zeroCoordinate(coordinate: Coordinate): Coordinate
}
