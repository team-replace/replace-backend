package com.app.replace.infrastructure

import com.app.replace.application.PlaceFinder
import com.app.replace.application.exception.IllegalCoordinateException
import com.app.replace.domain.Coordinate
import com.app.replace.domain.Place
import com.app.replace.domain.PlaceWithCoordinate
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

private const val KAKAO_MAP_API_URL = "https://dapi.kakao.com/v2/local"

@Component
class KakaoMapPlaceFinder(
    @Value("\${kakao_map_rest_api.key}") private val restApiKey: String,
    private val restTemplate: RestTemplate
) : PlaceFinder {
    override fun findPlaceByCoordinate(coordinate: Coordinate): Place {
        val kakaoRestApiUrl =
            URI.create("${KAKAO_MAP_API_URL}/geo/coord2address.json?x=${coordinate.longitude}&y=${coordinate.latitude}")

        val headers = HttpHeaders()
        headers.add("Authorization", restApiKey)

        val requestEntity = RequestEntity<Map<String, String>>(headers, HttpMethod.GET, kakaoRestApiUrl)
        val responseEntity = restTemplate.exchange(requestEntity, KakaoPlaceInformationByCoordinate::class.java).body
            ?: throw IllegalCoordinateException()

        return Place(
            responseEntity.getPlaceName(),
            responseEntity.getRoadAddressName()
        )
    }

    override fun findPlaceByKeyword(keyword: String, coordinate: Coordinate, pageable: Pageable): List<PlaceWithCoordinate> {
        val kakaoRestApiUrl = makeRequestUrlOfPlaceByKeyword(keyword, coordinate, pageable)

        val headers = HttpHeaders()
        headers.add("Authorization", restApiKey)

        val requestEntity = RequestEntity<Map<String, String>>(headers, HttpMethod.GET, kakaoRestApiUrl)
        val responseEntity = restTemplate.exchange(requestEntity, KakaoPlaceInformationByKeyword::class.java).body
            ?: throw IllegalCoordinateException()

        return responseEntity.getPlace()
    }

    private fun makeRequestUrlOfPlaceByKeyword(
        keyword: String,
        coordinate: Coordinate,
        pageable: Pageable
    ) = UriComponentsBuilder.fromHttpUrl("${KAKAO_MAP_API_URL}/search/keyword.json")
        .queryParam("query", keyword)
        .queryParam("x", coordinate.longitude)
        .queryParam("y", coordinate.latitude)
        .queryParam("page", pageable.pageNumber)
        .queryParam("size", pageable.pageSize)
        .queryParam("sort", "accuracy")
        .build()
        .encode()
        .toUri()
}
