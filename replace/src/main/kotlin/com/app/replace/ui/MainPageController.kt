package com.app.replace.ui

import com.app.replace.application.DiaryService
import com.app.replace.application.PlaceService
import com.app.replace.application.response.DiaryPreviewsByCoordinate
import com.app.replace.domain.Coordinate
import com.app.replace.ui.argumentresolver.Authenticated
import com.app.replace.ui.response.SearchPlacesResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class MainPageController(
    val placeService: PlaceService,
    val diaryService: DiaryService
) {
    @GetMapping("/search")
    fun search(
        @RequestParam longitude: String,
        @RequestParam latitude: String,
        @RequestParam query: String,
        @RequestParam page: Int,
        @RequestParam size: Int
    ) : ResponseEntity<SearchPlacesResponse> {
        val coordinate = Coordinate(BigDecimal(longitude), BigDecimal(latitude))
        val places = placeService.searchPlaceByKeyword(query, coordinate, page, size)
        return ResponseEntity.ok(SearchPlacesResponse(places))
    }

    @GetMapping("/map")
    fun loadPlaceWithDiariesByCoordinate(
        @Authenticated userId: Long,
        @RequestParam latitude: String,
        @RequestParam longitude: String
    ) : ResponseEntity<DiaryPreviewsByCoordinate> {
        val coordinate = Coordinate(BigDecimal(longitude), BigDecimal(latitude))
        return ResponseEntity.ok(diaryService.loadDiariesByCoordinate(userId, coordinate))
    }
}
