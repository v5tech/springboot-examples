package net.ameizi.valid;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class RequestParamValidAspect {

    @Pointcut("execution(* net.ameizi.controller.*.*(..))")
    public void requestParamValidAspect() {
    }

    ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    private final ExecutableValidator validator = factory.getValidator().forExecutables();


    @Before("requestParamValidAspect()")
    public void before(JoinPoint point) throws ParamValidException {
        //  获得切入目标对象
        Object target = point.getThis();
        // 获得切入方法参数
        Object[] args = point.getArgs();
        // 获得切入的方法
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        // 执行校验，获得校验结果
        Set<ConstraintViolation<Object>> validResult = validMethodParams(target, method, args);
        if (!validResult.isEmpty()) {
            // 获得方法的参数名称
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
            List<FieldError> errors = validResult.stream().map(constraintViolation -> {
                // 获得校验的参数路径信息
                PathImpl pathImpl = (PathImpl) constraintViolation.getPropertyPath();
                // 获得校验的参数位置
                int paramIndex = pathImpl.getLeafNode().getParameterIndex();
                // 获得校验的参数名称
                String paramName = parameterNames[paramIndex];
                // 将需要的信息包装成简单的对象，方便后面处理
                FieldError error = new FieldError();
                // 参数名称（校验错误的参数名称）
                error.setName(paramName);
                // 校验的错误信息
                error.setMessage(constraintViolation.getMessage());
                return error;
            }).collect(Collectors.toList());
            // 抛出异常，交给上层处理
            throw new ParamValidException(errors);
        }
    }

    private <T> Set<ConstraintViolation<T>> validMethodParams(T obj, Method method, Object[] params) {
        return validator.validateParameters(obj, method, params);
    }

}
