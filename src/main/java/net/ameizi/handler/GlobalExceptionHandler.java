package net.ameizi.handler;

import lombok.extern.slf4j.Slf4j;
import net.ameizi.valid.ParamValidException;
import net.ameizi.vo.R;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

import javax.validation.ConstraintViolationException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public R handlerException(Exception e) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 500);
        map.put("msg", e.getMessage());
        return R.ok(map);
    }

    @ResponseBody
    @ExceptionHandler(ParamValidException.class)
    public R paramValidExceptionHandler(ParamValidException ex) {
        return R.fail(500, ex.getMessage(), ex.getFieldErrors());
    }

    @ResponseBody
    @ExceptionHandler(BindException.class)
    public R bindExceptionHandler(BindException ex) {
        return paramValidExceptionHandler(new ParamValidException(ex));
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public R constraintViolationExceptionHandler(ConstraintViolationException ex, HandlerMethod handlerMethod) {
        return paramValidExceptionHandler(new ParamValidException(ex, handlerMethod.getMethodParameters()));
    }

    /**
     * 捕获RequestParamValidAspect中抛出的异常
     */
    @ResponseBody
    @ExceptionHandler(UndeclaredThrowableException.class)
    public R undeclaredThrowableException(UndeclaredThrowableException ex) {
        Throwable throwable = ex.getUndeclaredThrowable();
        // 如果是ParamValidException异常则交由paramValidExceptionHandler处理
        if (throwable instanceof ParamValidException) {
            return paramValidExceptionHandler((ParamValidException) throwable);
        }
        return handlerException(ex);
    }

}
