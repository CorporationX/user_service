
package school.faang.user_service.entity.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Address {
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
}