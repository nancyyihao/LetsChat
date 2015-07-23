
package com.util.timer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 类功能描述：用来获取时间</br>
 * 
 * @author 王明献
 * @version 1.0 </p> 
 * 修改时间：</br> 
 * 修改备注：</br>
 */
public class Timer {
    private static SimpleDateFormat formatBuilder;

    public static String getDate(String format) {
        formatBuilder = new SimpleDateFormat(format);
        return formatBuilder.format(new Date());
    }

    public static String getDate() {
        return getDate("MM-dd  hh:mm:ss");
    }
}
