package com.fortegrp.at.common.report.internal

import groovy.time.TimeDuration

class DateStringFormatHelper {

	static private final MINUTE = 60 * 1000
	static private final HOUR = 60 * MINUTE

	static String toTimeDuration( timeInMs ) {
		long t = timeInMs.toLong()
		int hours = ( t / HOUR ).toInteger()
		int mins = ( ( t - HOUR * hours ) / MINUTE ).toInteger()
		int secs = ( ( t - HOUR * hours - mins * MINUTE ) / 1000 ).toInteger()
		int millis = ( t % 1000 ).toInteger()
		new TimeDuration( hours, mins, secs, millis ).toString().replace("minutes","min").replace("seconds","sec")
	}

	static String toPercentage( double rate ) {
		String.format( '%.2f%%', rate * 100 ).replace( '.00', '.0' )
	}

	static String formatToHtml( String text ) {
		text.replaceAll( /[\t\n]/, '<br/>' )
	}

	static String toDateString( Date date , format="MMM dd H:m:s z yyyy") {
        date.format(format)
	}
}
