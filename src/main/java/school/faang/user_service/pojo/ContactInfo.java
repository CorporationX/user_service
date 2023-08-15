package school.faang.user_service.pojo;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ContactInfo {
    public String email;
    public String phone;
    @JsonUnwrapped
    public Address address;

}
