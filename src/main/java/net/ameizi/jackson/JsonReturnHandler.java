package net.ameizi.jackson;

import net.ameizi.annotation.JSONFieldFilter;
import net.ameizi.annotation.JSONFieldFilters;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class JsonReturnHandler implements HandlerMethodReturnValueHandler, BeanPostProcessor {

    @Autowired
    private JsonSerializer jsonSerializer;

    List<ResponseBodyAdvice<Object>> advices = new ArrayList<>();

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(JSONFieldFilter.class) || returnType.hasMethodAnnotation(JSONFieldFilters.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        // 设置这个就是最终的处理类了，处理完不再去找下一个类进行处理
        mavContainer.setRequestHandled(true);

        for (int i = 0; i < advices.size(); i++) {
            ResponseBodyAdvice<Object> advice = advices.get(i);
            if (advice.supports(returnType, null)) {
                returnValue = advice.beforeBodyWrite(
                        returnValue, returnType, MediaType.APPLICATION_JSON_UTF8, null,
                        new ServletServerHttpRequest(webRequest.getNativeRequest(HttpServletRequest.class)),
                        new ServletServerHttpResponse(webRequest.getNativeResponse(HttpServletResponse.class))
                );
            }
        }

        // 获得注解并执行filter方法 最后返回
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        Annotation[] annos = returnType.getMethodAnnotations();
        Arrays.asList(annos).forEach(a -> {
            // 处理 @JSONFieldFilter注解
            if (a instanceof JSONFieldFilter) {
                JSONFieldFilter jsonFieldFilter = (JSONFieldFilter) a;
                jsonSerializer.filter(jsonFieldFilter);
                // 处理 @JSONFieldFilters注解
            } else if (a instanceof JSONFieldFilters) {
                JSONFieldFilters jsonFieldFilters = (JSONFieldFilters) a;
                Arrays.asList(jsonFieldFilters.value()).forEach(json -> jsonSerializer.filter(json));
            }
        });

        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        String json = jsonSerializer.toJson(returnValue);
        response.getWriter().write(json);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ResponseBodyAdvice) {
            advices.add((ResponseBodyAdvice<Object>) bean);
        } else if (bean instanceof RequestMappingHandlerAdapter) {
            List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>(
                    ((RequestMappingHandlerAdapter) bean).getReturnValueHandlers());
            JsonReturnHandler jsonHandler = null;
            for (int i = 0; i < handlers.size(); i++) {
                HandlerMethodReturnValueHandler handler = handlers.get(i);
                if (handler instanceof JsonReturnHandler) {
                    jsonHandler = (JsonReturnHandler) handler;
                    break;
                }
            }
            if (jsonHandler != null) {
                handlers.remove(jsonHandler);
                handlers.add(0, jsonHandler);
                ((RequestMappingHandlerAdapter) bean).setReturnValueHandlers(handlers); // change the jsonhandler sort
            }
        }
        return bean;
    }
}
