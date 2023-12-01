package com.app.replace.application

import com.app.replace.domain.Coordinate
import com.app.replace.domain.PlaceWithCoordinate
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class PlaceService(
    val placeFinder: PlaceFinder
) {
    fun searchPlaceByKeyword(keyword: String, coordinate: Coordinate, page: Int, size: Int): List<PlaceWithCoordinate> {
        val pageRequest = getPageRequest(page, size)
        return placeFinder.findPlaceByKeyword(keyword, coordinate, pageRequest)
    }

    private fun getPageRequest(page: Int, size: Int): PageRequest {
        require(page >= 1) { "유효하지 않은 페이지 번호입니다." }
        require(size >= 0) { "유효하지 않은 페이지 사이즈입니다." }
        return PageRequest.of(page, size)
    }
}
