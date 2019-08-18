package net.ameizi.valid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldError implements Serializable {

    private String name;
    private String message;

    @Override
    public String toString() {
        return String.format("字段 %s %s", name, message);
    }

}