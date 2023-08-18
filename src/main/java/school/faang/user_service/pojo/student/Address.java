package school.faang.user_service.pojo.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    public String street;
    public String city;
    public String state;
    public String country;
    public String postalCode;
}
