package school.faang.user_service.dto.user;

import lombok.*;
import school.faang.user_service.dto.entity.EntityDto;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserDto extends EntityDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
    private Long countryId;
    private String city;
    private Integer experience;

}
