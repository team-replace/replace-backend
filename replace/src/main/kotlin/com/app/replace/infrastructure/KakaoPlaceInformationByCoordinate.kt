package com.app.replace.infrastructure

import com.app.replace.application.exception.IllegalCoordinateException
import com.app.replace.application.exception.NotBuildingPointException

data class KakaoPlaceInformationByCoordinate(
    val meta: LinkedHashMap<String, Int>,
    val documents: ArrayList<LinkedHashMap<String, LinkedHashMap<String, String>>>
) {
    fun getPlaceName(): String {
        validateValidCoordinate()

        val roadAddress: LinkedHashMap<String, String> = getRoadAddress()
        return roadAddress.get("building_name") ?: throw NotBuildingPointException()
    }

    fun getRoadAddressName() : String {
        val roadAddress: LinkedHashMap<String, String> = getRoadAddress()
        return roadAddress.get("address_name") ?: throw NotBuildingPointException()
    }

    private fun getRoadAddress() = documents.get(0).get("road_address")
        ?: throw NotBuildingPointException()

    private fun validateValidCoordinate() {
        if (meta.get("total_count") == 0 || documents.isEmpty()) {
            throw IllegalCoordinateException()
        }
    }
}
