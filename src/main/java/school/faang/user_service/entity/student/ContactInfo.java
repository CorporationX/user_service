package school.faang.user_service.entity.student;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

@Data
public class ContactInfo {
    private String email;
    private String phone;
    @JsonUnwrapped
    private Address address;
}
