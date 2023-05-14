package cn.edu.hncj.forum.aop;

import java.lang.annotation.*;

/**
 * @author FanJian
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {
    String module() default "";
    String operator() default "";
}
