
package school.faang.user_service.entity.person;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.annotation.processing.Generated;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class PersonSchema {

    @JsonProperty("person")
    private Person person;
}
