package school.faang.user_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class ErrorField implements Serializable {
    private String name;
    private String type;
    private String value;
    private String expected;
}
