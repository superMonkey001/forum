package cn.edu.hncj.forum;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @Author FanJian
 * @Date 2022/5/18
 */

public class Test {
    @org.junit.jupiter.api.Test
    public void test() {
        String s = DigestUtils.md5Hex("111cd7d9b");
        System.out.println(s);
    }
}
