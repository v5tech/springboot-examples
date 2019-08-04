package net.ameizi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义JSONFieldFilters注解，用于处理重复标记 @JSONFieldFilter注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONFieldFilters {

    JSONFieldFilter[] value();

}
