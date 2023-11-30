package com.app.replace.infrastructure

import com.app.replace.application.exception.IllegalCoordinateException
import com.app.replace.application.PlaceFinder
import com.app.replace.domain.Coordinate
import com.app.replace.domain.Place
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.net.URI

private const val KAKAO_MAP_API_URL = "https://dapi.kakao.com/v2/local/geo/coord2address.json"

@Component
class KakaoMapPlaceFinder(
    @Value("\${kakao_map_rest_api.key}") private val restApiKey: String,
    private val restTemplate: RestTemplate
) : PlaceFinder {
    override fun findPlaceByCoordinate(coordinate: Coordinate): Place {
        val kakaoRestApiUrl =
            URI.create("${KAKAO_MAP_API_URL}?x=${coordinate.longitude}&y=${coordinate.latitude}")

        val headers = HttpHeaders()
        headers.add("Authorization", restApiKey)

        val requestEntity = RequestEntity<Map<String, String>>(headers, HttpMethod.GET, kakaoRestApiUrl)
        val responseEntity = restTemplate.exchange(requestEntity, KakaoPlaceInformation::class.java).body
            ?: throw IllegalCoordinateException()

        return Place(
            responseEntity.getPlaceName(),
            responseEntity.getRodaAddressName()
        )
    }
}
