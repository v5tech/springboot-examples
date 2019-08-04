package net.ameizi.annotation;

import java.lang.annotation.*;

/**
 * 自定义JSONFilter注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(JSONFieldFilters.class)
public @interface JSONFieldFilter {

    Class type();

    String[] include() default {};

    String[] filter() default {};

}