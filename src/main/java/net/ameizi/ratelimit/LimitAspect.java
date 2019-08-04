package net.ameizi.ratelimit;

import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Classname LimitAspect
 * @Description 注解拦截
 */
@Slf4j
@Aspect
@Configuration
public class LimitAspect {


    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    private DefaultRedisScript<Number> redisluaScript;

    // 声明切入点
    @Pointcut("@annotation(net.ameizi.ratelimit.RateLimit)")
    public void rateLimit() {
    }

    // 执行限流策略
    @Around("rateLimit()")
    public Object interceptor(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();

        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        if (rateLimit != null) {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            String ipAddress = ServletUtil.getClientIP(request);

            String string = ipAddress + "-" + targetClass.getName() + "- " + method.getName() + "-" + rateLimit.key();
            List<String> keys = Collections.singletonList(string);
            Number number = redisTemplate.execute(redisluaScript, keys, rateLimit.count(), rateLimit.time());

            if (number != null && number.intValue() != 0 && number.intValue() <= rateLimit.count()) {
                log.info("限流时间段内访问第：{} 次", number.toString());
                return joinPoint.proceed();
            }

        } else {
            return joinPoint.proceed();
        }
        log.error("已经超过最大访问次数");
        throw new RuntimeException("已经超过最大访问次数");
    }

}
