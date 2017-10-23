package com.fortegrp.at.common.utils

import java.text.DecimalFormat

class DimensionUtils {

    static FF_SEMICIRCLES_TO_DEGREES = 11930464.7111111
    static METERS_IN_MILE = 1609.34
    static MS_TO_KMH = 3.6
    static MS_TO_MPH = MS_TO_KMH / METERS_IN_MILE * 1000
    static FOOT_IN_METER = 3.28084
    static inchHgInBar = 29.53

    static farengateToCelsiusStr(farengateTemp, outputFormat = "%.1f") {
        String.format(outputFormat, ((farengateTemp - 32) * 5 / 9))
    }


    static milliBarToInchHG(miliBar, outputFormat = "##.##") {
        new DecimalFormat(outputFormat).format(miliBar * inchHgInBar / 1000)
    }


    static celsiusToFarengateStr(celsiusTemp, outputFormat = "##.#") {
        new DecimalFormat(outputFormat).format(celsiusToFarengate(celsiusTemp))
    }

    static metersToFeets(meters, outputFormat = "##.#") {
        new DecimalFormat(outputFormat).format(meters * FOOT_IN_METER)
    }

    static celsiusToFarengate(celsiusTemp) {
        celsiusTemp * 9 / 5 + 32
    }

    static semicirclesToDegrees(semicircles) {
        semicircles / FF_SEMICIRCLES_TO_DEGREES
    }

    static degreesToSemicircles(semicircles) {
        (semicircles * FF_SEMICIRCLES_TO_DEGREES).setScale(0, BigDecimal.ROUND_HALF_UP)
    }

    static metersToMiles(meters, format = null) {
        (format == null) ? meters / METERS_IN_MILE : String.format(format, meters / METERS_IN_MILE)
    }

    static kmhToMph(kmh) {
        kmh * 1000 / METERS_IN_MILE
    }

    static msToKmh(meterInSecond) {
        meterInSecond * MS_TO_KMH
    }

    static msToMph(meterInSecond) {
        meterInSecond * MS_TO_MPH
    }

    static kmhToPaceMinKm(speedKmH) {
        60 / speedKmH
    }

    static kmhToPaceMinMi(speedKmH) {
        0.06 * METERS_IN_MILE / (speedKmH)
    }
}
