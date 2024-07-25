package school.faang.user_service.pojo.person;

import lombok.Data;

@Data
public class ContactInfo {
    private String email;
    private String phone;
    private String street;
    private String State;
    private String country;
    private long postalCode;
}
