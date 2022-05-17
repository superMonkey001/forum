package cn.edu.hncj.forum.utils;

import java.util.UUID;

public class RandomLongUtils {
    /**
     * 随机生成Long值
     * @param bit 位数
     * @return 返回Long值
     * @throws Exception 异常
     */
    public static Long randomLong(int bit) throws Exception {
        if (bit > 16) {
            throw new Exception("bit must <= 16");
        }
        if (bit < 6) {
            throw new Exception("bit must >=6");
        }
        String midStr = "";
        byte[] bytes = UUID.randomUUID().toString().getBytes();
        for (int i = 0; i < bit; i++) {
            midStr += String.valueOf(bytes[i]).toCharArray()[String.valueOf(bytes[i]).toCharArray().length - 1];
        }
        return Long.parseLong(midStr);
    }
 
}