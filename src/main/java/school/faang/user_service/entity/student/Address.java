package school.faang.user_service.entity.student;

import lombok.Data;

@Data
public class Address {
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
}