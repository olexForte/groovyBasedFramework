package com.fortegrp.at.common.utils

import groovy.time.TimeCategory

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime

class DateUtils {
    static TIMEZONE = "ET";

    static incrementDate(sourceString, sourceFormat, Map<String, Integer> incrementValues) {
        incrementDate(Date.parse(sourceFormat, sourceString), incrementValues)
    }

    static incrementDate(Date sourceDate, Map<String, Integer> incrementValues) {
        Date result
        result = sourceDate
        incrementValues.keySet().toArray().each {
            def value = incrementValues.get(it).toInteger()
            def measure = it
            use(TimeCategory) {
                result = result + value."${measure}"
            }
        }
        result
    }

    static dateToEpochTime(Date sourceDate) {
        (int) (sourceDate.time / 1000)
    }

    static epochToDate(Long epochTime) {
        new Date(epochTime * 1000)
    }

    static tryDateStrToTimestampStr(sourceDateStr, format = 'MM/dd/yyyy') {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        try {
            sdf.setLenient(false)
            Date.parse(format, sourceDateStr).format(format).equals(sourceDateStr) ? sdf.format(Date.parse(format, sourceDateStr)) : sourceDateStr
        } catch (ParseException ex) {
            sourceDateStr
        }
    }

    static getDateOfPreviousWeekday(Date baseDate, weekDayCode) {
        def cal = Calendar.instance
        cal.setTime(baseDate)
        while (cal.get(Calendar.DAY_OF_WEEK) != weekDayCode) {
            cal.add(Calendar.DAY_OF_WEEK, -1)
        }
        cal.time
    }

    static Date getDateFromString (String string){
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        (Date)formatter.parse(string);
    }

    static String dateToString (Date date){
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        formatter.format(date);
    }

    static String currentDateInFormat(String format) {
        new SimpleDateFormat(format).format(new Date())
    }

    static String currentDateTime() {
        currentDateInFormat("yyMMdd_HHmmss")
    }

    static transformToUnifiedTimeStamp(String time){
        return time.replace(" ","").replace("m","").replace("M","").replace(TIMEZONE, "").replaceAll("^0","").trim()
    }
}
