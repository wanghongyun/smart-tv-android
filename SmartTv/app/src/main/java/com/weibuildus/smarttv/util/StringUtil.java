/**
 * @company Chengdu ZhiXiao planning consulting co., LTD.
 * @copyright 2015, Chengdu ZhiXiao planning consulting co., LTD.
 */
package com.weibuildus.smarttv.util;

import android.content.Context;

import java.io.File;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 字符串处理
 *
 * @author wumaojie.gmail.com
 * @ClassName: StringUtil
 * @date 2015-8-10 上午11:56:30
 */
public class StringUtil {

    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回 true
     */
    public static boolean isMobile(String str) {
        // 验证手机号
        return Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$").matcher(str)
                .matches();
    }

    /**
     * 手机号验证，简单验证，11位数字
     *
     * @param str
     * @return 验证通过返回 true
     */
    public static boolean isMobileSimple(String str) {
        // 验证手机号
        return Pattern.compile("[0-9]{11}$").matcher(str)
                .matches();
    }

    /**
     * 验证密码数字和字母
     *
     * @param str
     * @return boolean
     * @throws
     * @Title: isPassWord
     */
    public static boolean isPassWord(String str) {
        // 验证密码
        return Pattern.compile("[0-9A-Za-z]*").matcher(str).matches();
    }

    /**
     * 从文件路径中获取文件后缀(文件格式)
     *
     * @param filePath
     * @return
     * @Title getFileType
     */
    public static String getFileType(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            int start = filePath.lastIndexOf(".");
            if (start != -1) {
                return filePath.substring(start);
            } else {
                return "";
            }
        } else {
            return null;
        }
    }

    /**
     * 格式化银行卡
     *
     * @param bankCardNumber
     * @return
     * @Title getFormatBankCardNumber
     */
    public static String getFormatBankCardNumber(String bankCardNumber) {
        if (bankCardNumber != null) {
            StringBuffer buffer = new StringBuffer(bankCardNumber);
            int index = buffer.length() / 4;
            for (int i = index; i > 0; i--) {
                buffer.insert(i * 4, " ");
            }
            return buffer.toString();
        }
        return null;
    }

    /**
     * 格式化时间
     *
     * @param time
     * @param format 格式
     * @return
     * @Title getFormatTime
     */
    public static String getFormatTime(long time, String format) {
        return new SimpleDateFormat(format).format(new Date(time));
    }

    /**
     * 计算时间差， 输出格式化字符串 $Y $M $D $h $m $s (如： $Y年$M月$D天$h小时$m分钟$s秒，$D天$h时，$m分$s秒)
     *
     * @param startTime
     * @param endTime
     * @param format
     * @return
     * @Title getDifferTimeFormat
     */
    public static String getDifferTimeFormat(long startTime, long endTime,
                                             String format) {
        long difference = Math.abs(startTime - endTime);
        if (format != null) {
            long Y = 0;
            long M = 0;
            long D = 0;
            long h = 0;
            long m = 0;
            long s = 0;
            // 年
            if (format.indexOf("$Y") != -1) {
                Y = difference / 1000 / 60 / 60 / 24 / 30 / 12;
                difference = difference % (1000 * 60 * 60 * 24 * 30 * 12);
            }
            // 月
            if (format.indexOf("$M") != -1) {
                M = difference / 1000 / 60 / 60 / 24 / 30;
                difference = difference % (1000 * 60 * 60 * 24 * 30);
            }
            // 天
            if (format.indexOf("$D") != -1) {
                D = difference / 1000 / 60 / 60 / 24;
                difference = difference % (1000 * 60 * 60 * 24);
            }
            if (format.indexOf("$h") != -1) {
                h = difference / 1000 / 60 / 60;
                difference = difference % (1000 * 60 * 60);
            }
            if (format.indexOf("$m") != -1) {
                m = difference / 1000 / 60;
                difference = difference % (1000 * 60);
            }
            if (format.indexOf("$s") != -1) {
                s = difference / 1000;
            }
            return format.replace("$Y", Y + "").replace("$M", M + "")
                    .replace("$D", D + "").replace("$h", h + "")
                    .replace("$m", m + "").replace("$s", s + "");
        }
        return "";
    }

    /**
     * 字符串转时间
     *
     * @param format
     * @param timeStr
     * @return
     * @Title getTimeToLongForString
     */
    public static Long getTimeToLongForString(String format, String timeStr) {
        if (timeStr == null) {
            return null;
        }
        if (format == null) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        long time;
        try {
            time = new SimpleDateFormat(format).parse(timeStr).getTime();
            return new Long(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 格式化小数到后两位
     *
     * @param d
     * @return
     * @Title getDoubleFloatFormat
     */
    public static String getDoubleFloatFormat(double d) {
        return new DecimalFormat("0.00").format(d);
    }


    /**
     * is null or its length is 0 or it is made by space
     * <p/>
     * <pre>
     * isBlank(null) = true;
     * isBlank(&quot;&quot;) = true;
     * isBlank(&quot;  &quot;) = true;
     * isBlank(&quot;a&quot;) = false;
     * isBlank(&quot;a &quot;) = false;
     * isBlank(&quot; a&quot;) = false;
     * isBlank(&quot;a b&quot;) = false;
     * </pre>
     *
     * @param str
     * @return if string is null or its size is 0 or it is made by space, return true, else return false.
     */
    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }

    /**
     * is null or its length is 0
     * <p/>
     * <pre>
     * isEmpty(null) = true;
     * isEmpty(&quot;&quot;) = true;
     * isEmpty(&quot;  &quot;) = false;
     * </pre>
     *
     * @param str
     * @return if string is null or its size is 0, return true, else return false.
     */
    public static boolean isEmpty(CharSequence str) {
        return (str == null || str.length() == 0);
    }

    /**
     * 获取非空字符串
     *
     * @param str
     * @return
     */
    public static String getNoEmpty(CharSequence str) {
        return isEmpty(str) ? "" : str.toString();
    }


    /**
     * get length of CharSequence
     * <p/>
     * <pre>
     * length(null) = 0;
     * length(\"\") = 0;
     * length(\"abc\") = 3;
     * </pre>
     *
     * @param str
     * @return if str is null or empty, return 0, else return {@link CharSequence#length()}.
     */
    public static int length(CharSequence str) {
        return str == null ? 0 : str.length();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String MD5Encryption(String text) {
        try {
            //生成实现指定摘要算法的 MessageDigest 对象。
            MessageDigest md = MessageDigest.getInstance("MD5");
            //使用指定的字节数组更新摘要。
            md.update(text.getBytes());
            //通过执行诸如填充之类的最终操作完成哈希计算。
            byte b[] = md.digest();
            //生成具体的md5密码到buf数组
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
