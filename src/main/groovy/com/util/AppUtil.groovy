package com.util

import java.text.SimpleDateFormat

/**
 * Created by karan on 16/12/17.
 */
class AppUtil {

    static final String mySqlDateFormat = 'yyyy-MM-dd HH:mm:ss'

    static String formattedDate(Date date, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat)
        format.format(date)
    }
}
