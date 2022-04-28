package cn.edu.hncj.forum;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class ForumApplicationTests {

    @Test
    void contextLoads() {
        String str = "java,,spring";
//        String[] split = str.split(",");
        String[] split = StringUtils.split(str, ",");
        System.out.println(Arrays.toString(split));
    }

}
