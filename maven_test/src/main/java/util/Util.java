package util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**Util工具类中添加数据转化的方法
 * @author zhaomin
 * @date 2020/2/13 0:57
 */
public class Util {
    public static final String DATA_PATTERN="yyyy-MM-dd hh:mm:ss";

    public static String parseSize(long size) {
        String[] danweis={"B","KB","MB","GB"};
        int idx=0;
        while(size>1024&& idx<danweis.length-1){
            size/=1024;
            idx++;
        }
        return  size+danweis[idx];
    }

    /**
     * 解析日期为中文日期描述
     * @param lastModified
     * @return
     */
    public static String parseDate(Date lastModified) {
        return new SimpleDateFormat(DATA_PATTERN).format(lastModified);
    }

    public static void main(String[] args) {
        System.out.println(parseSize(100));
        System.out.println(parseDate(new Date()));
    }
}
