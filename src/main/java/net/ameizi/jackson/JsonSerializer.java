package net.ameizi.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import net.ameizi.annotation.JSONFieldFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JsonSerializer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JacksonJsonFilter jacksonFilter;

    /**
     * @param clazz   target type
     * @param include include fields
     * @param filter  filter fields
     */
    public void filter(Class<?> clazz, String[] include, String[] filter) {
        if (clazz == null) return;
        if (include != null && include.length > 0) {
            jacksonFilter.include(clazz, include);
        }
        if (filter != null && filter.length > 0) {
            jacksonFilter.filter(clazz, filter);
        }
        objectMapper.addMixIn(clazz, jacksonFilter.getClass());
    }

    public String toJson(Object object) throws JsonProcessingException {
        objectMapper.setFilterProvider(jacksonFilter);
        return objectMapper.writeValueAsString(object);
    }

    public void filter(JSONFieldFilter json) {
        this.filter(json.type(), json.include(), json.filter());
    }
}
