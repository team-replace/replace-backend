package com.app.replace.infrastructure

import com.app.replace.application.exception.IllegalCoordinateException
import com.app.replace.application.exception.NotBuildingPointException
import com.app.replace.domain.Coordinate
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal

@SpringBootTest
class KakaoMapPlaceFinderTest(
    @Autowired val kakaoMapPlaceFinder: KakaoMapPlaceFinder
) {
    @Test
    @DisplayName("위도와 경도 정보를 제공하면 해당 장소의 도로명주소와 건물명을 응답한다.")
    fun findPlace() {
        val coordinate = Coordinate(BigDecimal("127.103068896795"), BigDecimal("37.5152535228382"))
        val place = kakaoMapPlaceFinder.findPlaceByCoordinate(coordinate)

        place.spotName shouldBe "한국루터회관"
        place.roadAddress shouldBe "서울특별시 송파구 올림픽로35다길 42"
    }

    @Test
    @DisplayName("주소는 있으나 건물이 아닌 좌표를 입력하면 예외가 발생한다.")
    fun notBuildingPlace() {
        val coordinate = Coordinate(BigDecimal("127.055047377755"), BigDecimal("37.2368405729005"))
        shouldThrow<NotBuildingPointException> { kakaoMapPlaceFinder.findPlaceByCoordinate(coordinate) }
    }

    @Test
    @DisplayName("도로명주소와 지번주소 어느 것으로도 등록되지 않은 좌표를 입력하면 예외가 발생한다.")
    fun notExistingPlace() {
        val coordinate = Coordinate(BigDecimal("129.658223"), BigDecimal("41.479299"))
        shouldThrow<IllegalCoordinateException> { kakaoMapPlaceFinder.findPlaceByCoordinate(coordinate) }
    }
}
