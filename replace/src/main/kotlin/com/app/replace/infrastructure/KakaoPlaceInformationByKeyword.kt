package com.app.replace.infrastructure

import com.app.replace.domain.Coordinate
import com.app.replace.domain.PlaceWithCoordinate
import java.math.BigDecimal

data class KakaoPlaceInformationByKeyword(
    val meta: LinkedHashMap<String, Any>,
    val documents: ArrayList<LinkedHashMap<String, String>>
) {
    fun getPlace() : List<PlaceWithCoordinate> {
        return documents.map { document ->
            PlaceWithCoordinate(
                document.get("place_name") ?: "unknown",
                Coordinate(
                    BigDecimal(document.get("x")),
                    BigDecimal((document.get("y")))
                ),
            )
        }.toList()
    }
}
