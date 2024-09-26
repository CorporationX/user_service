
package school.faang.user_service.pojo.student;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class PersonSchema {

    private Person person;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

}
