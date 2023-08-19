package school.faang.user_service.dto.user.person_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddressDto {
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
}
