
package school.faang.user_service.entity.person;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PersonSchema {

    @JsonProperty("person")
    private Person person;
}
