package school.faang.user_service.pojo.student;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

@Data
public class ContactInfo {
    public String email;
    public String phone;
    @JsonUnwrapped
    public Address address;
}
