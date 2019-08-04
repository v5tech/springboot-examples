package net.ameizi.advice;

import net.ameizi.annotation.JSONFieldFilter;
import net.ameizi.annotation.JSONFieldFilters;
import net.ameizi.jackson.JsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * RestController增强
 */
// @RestControllerAdvice(annotations = RestController.class)
public class RespBodyAdvice implements ResponseBodyAdvice {

    @Autowired
    private JsonSerializer jsonSerializer;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return returnType.hasMethodAnnotation(JSONFieldFilter.class) || returnType.hasMethodAnnotation(JSONFieldFilters.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
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
        try {
            String json = jsonSerializer.toJson(body);
            return jsonSerializer.getObjectMapper().readTree(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

}