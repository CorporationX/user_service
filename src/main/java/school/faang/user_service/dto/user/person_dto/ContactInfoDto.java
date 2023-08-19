package school.faang.user_service.dto.user.person_dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ContactInfoDto {
    @EqualsAndHashCode.Include
    private String email;
    @EqualsAndHashCode.Include
    private String phone;
    private AddressDto address;
}
