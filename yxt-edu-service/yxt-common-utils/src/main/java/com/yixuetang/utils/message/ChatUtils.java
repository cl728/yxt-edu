package com.yixuetang.utils.message;

import com.yixuetang.entity.message.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author Colin
 * @version 1.0.0
 * @description TODO
 * @date 2020/12/1 21:00
 */
public class ChatUtils {

    /**
     * 对List集合中的数据按照时间顺序排序
     *
     * @param list List<ChatMessage>
     */
    public static void sort(List<ChatMessage> list) {
        list.sort( Comparator.comparing( ChatMessage::getTime ) );
    }

    /**
     * 格式化日期
     *
     * @param date 待格式化的日期对象
     * @return 格式化后的日期字符串
     */
    public static String format(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        return sdf.format( date );
    }
}
